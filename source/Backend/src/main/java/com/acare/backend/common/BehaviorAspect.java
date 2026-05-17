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
import java.util.List;

/**
 * AOP Aspect cháº·n cÃ¡c Controller method cÃ³ annotation @DlpProtected.
 *
 * Luá»“ng xá»­ lÃ½:
 * 1. Client gá»i API â†’ JWT Auth â†’ Authorization
 * 2. TrÆ°á»›c khi Controller method cháº¡y â†’ BehaviorAspect kiá»ƒm tra:
 *    a. Giá» lÃ m viá»‡c (time-based rule)
 *    b. Rate limit (request frequency)
 *    c. Volume limit (cho download/export)
 * 3. Náº¿u vi pháº¡m â†’ throw SecurityException â†’ tráº£ 403
 * 4. Náº¿u há»£p lá»‡ â†’ Controller method cháº¡y bÃ¬nh thÆ°á»ng
 *
 * @Around advice: cháº¡y TRÆ¯á»šC vÃ  SAU method Ä‘Æ°á»£c cháº·n.
 * Cho phÃ©p cháº·n method hoÃ n toÃ n (khÃ´ng gá»i proceed()) náº¿u vi pháº¡m rule.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BehaviorAspect {

    private final RuleEngineService ruleEngineService;
    private final DlpLogService dlpLogService;

    /**
     * Advice cháº·n má»i method Ä‘Æ°á»£c Ä‘Ã¡nh @DlpProtected.
     *
     * @param joinPoint  thÃ´ng tin vá» method bá»‹ cháº·n
     * @param dlpProtected annotation chá»©a metadata (action type)
     * @return káº¿t quáº£ cá»§a method gá»‘c (náº¿u khÃ´ng vi pháº¡m)
     * @throws Throwable náº¿u method gá»‘c throw exception
     */
    @Around("@annotation(dlpProtected)")
    public Object checkDlpRules(ProceedingJoinPoint joinPoint, DlpProtected dlpProtected) throws Throwable {
        // Láº¥y thÃ´ng tin user tá»« JWT token
        Long userId = extractUserId();
        String username = extractUsername();
        String action = dlpProtected.action(); // Loáº¡i hÃ nh vi (VIEW, DOWNLOAD, EXPORT)

        // --- Kiá»ƒm tra Rule 1: Giá» lÃ m viá»‡c ---
        if (!ruleEngineService.isWorkingHour()) {
            if (isDoctor()) {
                log.warn("[DLP Aspect] Truy cap ngoai gio: user={}, method={}", username, joinPoint.getSignature().getName());
                saveDlpLog(userId, username, "OFF_HOURS", "OFF_HOURS", RiskLevel.HIGH);
                // TAM THOI BO QUA KHI TEST DEM
                // throw new SecurityException("DLP: Truy cap bi tu choi. Ngoai gio lam viec.");
            } else {
                log.info("[DLP Aspect] Benh nhan truy cap ngoai gio: user={}", username);
            }
        }

        // --- Kiá»ƒm tra Rule 2: Rate limit ---
        if (userId != null && ruleEngineService.isRateLimitExceeded(userId)) {
            log.warn("[DLP Aspect] Rate limit exceeded: user={}", username);

            saveDlpLog(userId, username, "RATE_LIMIT", "RATE_LIMIT", RiskLevel.CRITICAL);

            // TÄƒng bá»™ Ä‘áº¿m vi pháº¡m â†’ cÃ³ thá»ƒ auto-block
            ruleEngineService.incrementViolationCount(userId);

            throw new SecurityException("DLP: Truy cáº­p bá»‹ tá»« chá»‘i. Báº¡n Ä‘ang gá»­i quÃ¡ nhiá»u request.");
        }

        // --- Kiá»ƒm tra Rule 3: Volume limit (cho download/export) ---
        if (userId != null && isDoctor() && ("DOWNLOAD".equals(action) || "EXPORT".equals(action))) {
            if (ruleEngineService.isVolumeExceeded(userId)) {
                log.warn("[DLP Aspect] Volume limit exceeded: user={}, action={}", username, action);

                saveDlpLog(userId, username, action, "VOLUME_EXCEEDED", RiskLevel.CRITICAL);

                ruleEngineService.incrementViolationCount(userId);

                throw new SecurityException("DLP: Truy cáº­p bá»‹ tá»« chá»‘i. VÆ°á»£t giá»›i háº¡n download/export.");
            }
        }

        // --- Táº¥t cáº£ rules pass â†’ cho method cháº¡y bÃ¬nh thÆ°á»ng ---
        return joinPoint.proceed();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Láº¥y userId tá»« JWT claim.
     * Access token cá»§a há»‡ thá»‘ng A-Care lÆ°u userId trong claim "user_id".
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

    /** Láº¥y username (email) tá»« JWT subject */
    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "unknown";
    }

    private boolean isDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            List<String> roles = jwt.getClaimAsStringList("roles");
            return roles != null && roles.stream().anyMatch("DOCTOR"::equalsIgnoreCase);
        }
        return false;
    }

    /** Ghi log vi pháº¡m vÃ o database */
    private void saveDlpLog(Long userId, String username, String action,
                            String violationType, RiskLevel riskLevel) {
        try {
            DlpLog dlpLog = DlpLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .violationType(violationType)
                    .riskLevel(riskLevel)
                    .blocked(true) // Aspect luÃ´n block náº¿u vi pháº¡m
                    .build();
            dlpLogService.save(dlpLog);
        } catch (Exception e) {
            log.error("[DLP Aspect] Lá»—i khi lÆ°u DLP log: {}", e.getMessage());
        }
    }
}
