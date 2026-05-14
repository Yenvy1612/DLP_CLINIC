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

    // ─── API công khai ─────────────────────────────────────────────────────────

    /**
     * Kiểm tra rate limit: quá nhiều request đến cùng endpoint từ cùng user.
     * Ghi log sự kiện nếu vượt ngưỡng, không block (để Filter quyết định).
     *
     * @return risk score (0–100). Nếu >= 70 → Filter sẽ block.
     */
    public int evaluateRequest(Long userId, String ip, String method, String uri) {
        int score = 0;

        // 1. Kiểm tra endpoint rate limit
        if (userId != null) {
            long countEndpoint = securityEventRepository
                    .countByUserIdAndRequestUriAndOccurredAtAfter(userId, uri, oneMinuteAgo());
            if (countEndpoint >= RATE_LIMIT_PER_MINUTE) {
                score += 50;
                recordAsync(userId, ip, method, uri,
                        "ENDPOINT_RATE_LIMIT",
                        "HIGH",
                        String.format("User %d đã gửi %d request đến %s trong 1 phút", userId, countEndpoint + 1, uri),
                        score, "LOGGED");
            }
        }

        // 2. Kiểm tra IP rate limit (phòng bot/scanner)
        long countIp = securityEventRepository.countByIpAddressAndOccurredAtAfter(ip, oneMinuteAgo());
        if (countIp >= IP_RATE_LIMIT_PER_MINUTE) {
            score += 40;
            recordAsync(null, ip, method, uri,
                    "IP_RATE_LIMIT",
                    "MEDIUM",
                    String.format("IP %s đã gửi %d request trong 1 phút", ip, countIp + 1),
                    score, "LOGGED");
        }

        // 3. Kiểm tra IP bất ngờ thay đổi (session hijacking hint)
        if (userId != null) {
            String lastIp = securityEventRepository.findLastKnownIp(userId);
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
                .countByUserIdAndEventTypeAndOccurredAtAfter(userId, "LOGIN_FAILURE", tenMinutesAgo());
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
}
