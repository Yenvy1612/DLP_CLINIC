package com.acare.backend.service;

import com.acare.backend.entity.SecurityEvent;
import com.acare.backend.repository.RefreshTokenRepository;
import com.acare.backend.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * AnomalyDetectionService — phân tích hành vi bất thường từ request HTTP.
 * <p>
 * Hoạt động độc lập với:
 * - DLP (RegexPattern / BehaviorAspect): DLP kiểm tra nội dung dữ liệu
 * - Agent (AgentService): Agent tracking thiết bị vật lý
 * <p>
 * Service này theo dõi hành vi tại tầng HTTP request.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final SecurityEventRepository securityEventRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // ─── Ngưỡng phát hiện bất thường ──────────────────────────────────────────

    /** Số request tối đa đến cùng endpoint trong 1 phút */
    private static final int RATE_LIMIT_PER_MINUTE = 30;

    /** Số request tối đa từ 1 IP trong 1 phút (chưa đăng nhập) */
    private static final int IP_RATE_LIMIT_PER_MINUTE = 60;

    /** Số lần thử login thất bại tối đa trong 10 phút */
    private static final int MAX_LOGIN_FAILURES = 5;
    private static final int ESCALATION_STEP = 2;

    /** Bộ đếm in-memory theo cửa sổ 1 phút để tránh ghi mọi request vào DB */
    private final ConcurrentHashMap<String, Deque<LocalDateTime>> endpointWindow = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<LocalDateTime>> ipWindow = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> lastKnownIpByUser = new ConcurrentHashMap<>();

    // ─── API công khai ─────────────────────────────────────────────────────────

    /**
     * Kiểm tra rate limit: quá nhiều request đến cùng endpoint từ cùng user.
     * Ghi log sự kiện nếu vượt ngưỡng, không block (để Filter quyết định).
     *
     * @return risk score (0–100). Nếu >= 70 → Filter sẽ block.
     */
    public int evaluateRequest(Long userId, String ip, String method, String uri) {
        int score = 0;
        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra endpoint rate limit
        if (userId != null) {
            String endpointKey = userId + "|" + method + "|" + uri;
            int countEndpoint = incrementAndCount(endpointWindow, endpointKey, now, 1);
            if (countEndpoint >= RATE_LIMIT_PER_MINUTE && shouldLogThresholdBreach(countEndpoint, RATE_LIMIT_PER_MINUTE)) {
                SeverityRisk endpointSeverity = severityForEndpointRate(countEndpoint);
                score += endpointSeverity.risk;
                recordAsync(userId, ip, method, uri,
                        "ENDPOINT_RATE_LIMIT",
                        endpointSeverity.severity,
                        String.format("User %d đã gửi %d request đến %s trong 1 phút", userId, countEndpoint, uri),
                        Math.min(score, 100), actionForScore(score));
            }
        }

        // 2. Kiểm tra IP rate limit (phòng bot/scanner)
        // Theo thiết kế: IP rate limit chủ yếu cho anonymous/bot scanner.
        // User đã đăng nhập có thể tạo nhiều request hợp lệ khi chuyển tab/load dữ liệu.
        if (userId == null) {
            int countIp = incrementAndCount(ipWindow, ip, now, 1);
            if (countIp >= IP_RATE_LIMIT_PER_MINUTE && shouldLogThresholdBreach(countIp, IP_RATE_LIMIT_PER_MINUTE)) {
                SeverityRisk ipSeverity = severityForIpRate(countIp);
                score += ipSeverity.risk;
                recordAsync(null, ip, method, uri,
                        "IP_RATE_LIMIT",
                        ipSeverity.severity,
                        String.format("IP %s đã gửi %d request trong 1 phút", ip, countIp),
                        Math.min(score, 100), actionForScore(score));
            }
        }

        // 3. Kiểm tra IP bất ngờ thay đổi (session hijacking hint)
        if (userId != null) {
            String lastIp = lastKnownIpByUser.put(userId, ip);
            if (lastIp != null && !lastIp.equals(ip)) {
                score += 30;
                recordAsync(userId, ip, method, uri,
                        "IP_CHANGE_DETECTED",
                        "MEDIUM",
                        String.format("User %d đổi IP từ %s → %s trong session", userId, lastIp, ip),
                        score, "LOGGED");
            }
        }

        return Math.min(score, 100);
    }

    /**
     * Ghi nhận sự kiện login thất bại.
     * Sau MAX_LOGIN_FAILURES lần → nâng lên CRITICAL.
     */
    public void recordLoginFailure(String ip, String attemptedUsername) {
        long failures = securityEventRepository.countByIpAddressAndOccurredAtAfter(ip, tenMinutesAgo());
        String severity = failures >= MAX_LOGIN_FAILURES ? "CRITICAL" : "MEDIUM";
        String action = failures >= MAX_LOGIN_FAILURES ? "BLOCKED" : "LOGGED";

        record(null, ip, "POST", "/api/auth/login",
                "LOGIN_FAILURE",
                severity,
                String.format("Đăng nhập thất bại: tài khoản '%s', lần thứ %d từ IP %s",
                        attemptedUsername, failures + 1, ip),
                failures >= MAX_LOGIN_FAILURES ? 90 : 40,
                action);
    }

    /**
     * Ghi nhận login thành công sau nhiều lần thất bại (suspicious success).
     */
    public void recordSuspiciousLoginSuccess(Long userId, String ip) {
        long priorFailures = securityEventRepository
                .countByIpAddressAndEventTypeAndOccurredAtAfter(ip, "LOGIN_FAILURE", tenMinutesAgo());
        if (priorFailures >= 3) {
            record(userId, ip, "POST", "/api/auth/login",
                    "SUSPICIOUS_LOGIN_SUCCESS",
                    "HIGH",
                    String.format("User %d đăng nhập thành công sau %d lần thất bại", userId, priorFailures),
                    75, "LOGGED");
        }
    }

    /**
     * Ghi nhận truy cập ngoài giờ làm việc (bổ sung cho RuleEngineService).
     * Không trùng lặp: RuleEngineService block ngay, service này chỉ ghi log riêng.
     */
    public void recordOffHoursAccess(Long userId, String ip, String method, String uri) {
        record(userId, ip, method, uri,
                "OFF_HOURS_ACCESS",
                "MEDIUM",
                String.format("User %d truy cập %s %s ngoài giờ làm việc", userId, method, uri),
                45, "LOGGED");
    }

    /**
     * Thu hồi token của user bị phát hiện hành vi CRITICAL.
     */
    @Transactional
    public void revokeSessionFor(Long userId, String reason) {
        if (userId == null) return;
        refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());
        log.warn("[SECURITY] Token revoked for userId={} | Reason: {}", userId, reason);
    }

    // ─── Ghi sự kiện ──────────────────────────────────────────────────────────

    @Transactional
    public void record(Long userId, String ip, String method, String uri,
                       String eventType, String severity, String description,
                       int riskScore, String actionTaken) {
        try {
            securityEventRepository.save(SecurityEvent.builder()
                    .userId(userId)
                    .ipAddress(ip)
                    .httpMethod(method)
                    .requestUri(uri)
                    .eventType(eventType)
                    .severity(severity)
                    .description(description)
                    .riskScore(riskScore)
                    .actionTaken(actionTaken)
                    .occurredAt(LocalDateTime.now())
                    .build());
            log.warn("[SECURITY_EVENT] type={} severity={} userId={} ip={} score={} action={}",
                    eventType, severity, userId, ip, riskScore, actionTaken);
        } catch (Exception ex) {
            log.error("[SECURITY_EVENT] Failed to persist event: {}", ex.getMessage());
        }
    }

    @Async
    public void recordAsync(Long userId, String ip, String method, String uri,
                            String eventType, String severity, String description,
                            int riskScore, String actionTaken) {
        record(userId, ip, method, uri, eventType, severity, description, riskScore, actionTaken);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private LocalDateTime oneMinuteAgo() {
        return LocalDateTime.now().minusMinutes(1);
    }

    private LocalDateTime tenMinutesAgo() {
        return LocalDateTime.now().minusMinutes(10);
    }

    private int incrementAndCount(ConcurrentHashMap<String, Deque<LocalDateTime>> windows,
                                  String key,
                                  LocalDateTime now,
                                  int minutes) {
        LocalDateTime threshold = now.minusMinutes(minutes);
        Deque<LocalDateTime> deque = windows.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && deque.peekFirst().isBefore(threshold)) {
                deque.pollFirst();
            }
            deque.addLast(now);
            return deque.size();
        }
    }

    /**
     * Tránh spam log: chỉ ghi tại ngưỡng đầu tiên và mỗi 10 request vượt ngưỡng.
     * Ví dụ threshold=30 => ghi tại 30, 40, 50...
     */
    private boolean shouldLogThresholdBreach(int count, int threshold) {
        return count == threshold || ((count - threshold) % 10 == 0);
    }

    private SeverityRisk severityForEndpointRate(int count) {
        int level = count / RATE_LIMIT_PER_MINUTE;
        if (level >= 4) return new SeverityRisk("CRITICAL", 95);
        if (level >= 3) return new SeverityRisk("HIGH", 80);
        if (level >= ESCALATION_STEP) return new SeverityRisk("MEDIUM", 55);
        return new SeverityRisk("LOW", 25);
    }

    private SeverityRisk severityForIpRate(int count) {
        int level = count / IP_RATE_LIMIT_PER_MINUTE;
        if (level >= 6) return new SeverityRisk("CRITICAL", 95);
        if (level >= 4) return new SeverityRisk("HIGH", 80);
        if (level >= ESCALATION_STEP) return new SeverityRisk("MEDIUM", 50);
        return new SeverityRisk("LOW", 20);
    }

    private String actionForScore(int score) {
        if (score >= 90) return "TOKEN_REVOKED";
        if (score >= 70) return "BLOCKED";
        return "LOGGED";
    }

    private static class SeverityRisk {
        final String severity;
        final int risk;

        SeverityRisk(String severity, int risk) {
            this.severity = severity;
            this.risk = risk;
        }
    }
}
