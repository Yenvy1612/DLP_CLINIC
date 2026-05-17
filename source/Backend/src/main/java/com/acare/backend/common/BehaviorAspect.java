package com.acare.backend.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.acare.backend.dlp.DlpProtected;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.service.DlpLogService;
import com.acare.backend.service.RuleEngineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AOP Aspect chặn các Controller method có annotation @DlpProtected.
 *
 * Luồng xử lý:
 * 1. Client gọi API → JWT Auth → Authorization
 * 2. Trước khi Controller method chạy → BehaviorAspect kiểm tra:
 *    a. Giờ làm việc (time-based rule)
 *    b. Rate limit (request frequency)
 *    c. Volume limit (cho download/export)
 * 3. Nếu vi phạm → throw SecurityException → trả 403
 * 4. Nếu hợp lệ → Controller method chạy bình thường
 *
 * @Around advice: chạy TRƯỚC và SAU method được chặn.
 * Cho phép chặn method hoàn toàn (không gọi proceed()) nếu vi phạm rule.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BehaviorAspect {

    private final RuleEngineService ruleEngineService;
    private final DlpLogService dlpLogService;

    /**
     * Advice chặn mọi method được đánh @DlpProtected.
     *
     * @param joinPoint  thông tin về method bị chặn
     * @param dlpProtected annotation chứa metadata (action type)
     * @return kết quả của method gốc (nếu không vi phạm)
     * @throws Throwable nếu method gốc throw exception
     */
    @Around("@annotation(dlpProtected)")
    public Object checkDlpRules(ProceedingJoinPoint joinPoint, DlpProtected dlpProtected) throws Throwable {
        // Lấy thông tin user từ JWT token
        Long userId = extractUserId();
        String username = extractUsername();
        String action = dlpProtected.action(); // Loại hành vi (VIEW, DOWNLOAD, EXPORT)

        // --- Kiểm tra Rule 1: Giờ làm việc ---
        if (!ruleEngineService.isWorkingHour()) {
            log.warn("[DLP Aspect] Truy cập ngoài giờ: user={}, method={}",
                    username, joinPoint.getSignature().getName());

            // Ghi log vi phạm
            saveDlpLog(userId, username, "OFF_HOURS", "OFF_HOURS", RiskLevel.HIGH);

            throw new SecurityException("DLP: Truy cập bị từ chối. Ngoài giờ làm việc cho phép.");
        }

        // --- Kiểm tra Rule 2: Rate limit ---
        if (userId != null && ruleEngineService.isRateLimitExceeded(userId)) {
            log.warn("[DLP Aspect] Rate limit exceeded: user={}", username);

            saveDlpLog(userId, username, "RATE_LIMIT", "RATE_LIMIT", RiskLevel.CRITICAL);

            // Tăng bộ đếm vi phạm → có thể auto-block
            ruleEngineService.incrementViolationCount(userId);

            throw new SecurityException("DLP: Truy cập bị từ chối. Bạn đang gửi quá nhiều request.");
        }

        // --- Kiểm tra Rule 3: Volume limit (cho download/export) ---
        if (userId != null && ("DOWNLOAD".equals(action) || "EXPORT".equals(action))) {
            if (ruleEngineService.isVolumeExceeded(userId)) {
                log.warn("[DLP Aspect] Volume limit exceeded: user={}, action={}", username, action);

                saveDlpLog(userId, username, action, "VOLUME_EXCEEDED", RiskLevel.CRITICAL);

                ruleEngineService.incrementViolationCount(userId);

                throw new SecurityException("DLP: Truy cập bị từ chối. Vượt giới hạn download/export.");
            }
        }

        // --- Tất cả rules pass → cho method chạy bình thường ---
        return joinPoint.proceed();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lấy userId từ JWT claim.
     * Access token của hệ thống A-Care lưu userId trong claim "user_id".
     */
    private Long extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaim("user_id");
            if (userIdClaim instanceof Number num) {
                return num.longValue();
            }
        }
        return null;
    }

    /** Lấy username (email) từ JWT subject */
    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "unknown";
    }

    /** Ghi log vi phạm vào database */
    private void saveDlpLog(Long userId, String username, String action,
                            String violationType, RiskLevel riskLevel) {
        try {
            DlpLog dlpLog = DlpLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .violationType(violationType)
                    .riskLevel(riskLevel)
                    .blocked(true) // Aspect luôn block nếu vi phạm
                    .build();
            dlpLogService.save(dlpLog);
        } catch (Exception e) {
            log.error("[DLP Aspect] Lỗi khi lưu DLP log: {}", e.getMessage());
        }
    }
}
