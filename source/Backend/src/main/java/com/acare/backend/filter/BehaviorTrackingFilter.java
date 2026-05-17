package com.acare.backend.filter;

import com.acare.backend.service.AnomalyDetectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * BehaviorTrackingFilter — HTTP filter chạy sau Spring Security authentication.
 * <p>
 * Vị trí trong filter chain: sau JwtAuthenticationFilter.
 * Tầng: HTTP layer (khác với BehaviorAspect của đồng nghiệp ở tầng AOP method).
 * <p>
 * Không đụng đến:
 * - DlpLog / RegexPattern (nội dung dữ liệu)
 * - Agent / AgentService (thiết bị vật lý)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorTrackingFilter extends OncePerRequestFilter {

    private final AnomalyDetectionService anomalyDetectionService;

    /** Các path không cần theo dõi */
    private static final Set<String> SKIP_PATHS = Set.of(
            "/actuator/health",
            "/error",
            "/favicon.ico"
    );

    /** Các prefix nội bộ/dashboard để tránh tự tạo nhiễu khi admin mở màn hình log */
    private static final Set<String> SKIP_PREFIXES = Set.of(
            "/api/security",
            "/api/dlp/logs",
            "/api/agent-events"
    );

    /** Ngưỡng risk score để tự động block request */
    private static final int BLOCK_THRESHOLD = 70;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (SKIP_PATHS.contains(uri)) return true;
        return SKIP_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Long userId = extractUserId();
        String ip = resolveClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Đánh giá mức độ rủi ro của request hiện tại
        int riskScore = anomalyDetectionService.evaluateRequest(userId, ip, method, uri);

        if (riskScore >= BLOCK_THRESHOLD) {
            log.warn("[BEHAVIOR_FILTER] Blocked request: userId={} ip={} uri={} score={}",
                    userId, ip, uri, riskScore);

            // Ghi nhận hành động chặn ngay tại request hiện tại.
            anomalyDetectionService.record(
                    userId, ip, method, uri,
                    "REQUEST_BLOCKED",
                    riskScore >= 90 ? "CRITICAL" : "HIGH",
                    "Request bị chặn tự động do vượt ngưỡng rủi ro",
                    riskScore,
                    riskScore >= 90 ? "TOKEN_REVOKED" : "BLOCKED"
            );

            // Nếu CRITICAL → thu hồi token luôn
            if (riskScore >= 90 && userId != null) {
                anomalyDetectionService.revokeSessionFor(userId,
                        "Risk score " + riskScore + " tại " + uri);
                anomalyDetectionService.record(userId, ip, method, uri,
                        "SESSION_REVOKED", "CRITICAL",
                        "Session bị thu hồi tự động do risk score vượt ngưỡng 90",
                        riskScore, "TOKEN_REVOKED");
            }

            writeBlockedResponse(response, riskScore);
            return;
        }

        // Request bình thường — tiếp tục filter chain
        filterChain.doFilter(request, response);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Lấy userId từ JWT token đã được Spring Security parse.
     * Trả về null nếu request chưa xác thực.
     */
    private Long extractUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();
            if (principal instanceof Jwt jwt) {
                Number userIdClaim = jwt.getClaim("user_id");
                return userIdClaim != null ? userIdClaim.longValue() : null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Lấy IP thực của client, hỗ trợ proxy / load balancer.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    /** Trả về 429 Too Many Requests khi block */
    private void writeBlockedResponse(HttpServletResponse response, int riskScore) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"status\":429,\"success\":false,\"message\":\"Yêu cầu bị chặn do hành vi bất thường (risk score: %d). Vui lòng thử lại sau.\",\"data\":null}",
                riskScore
        ));
    }
}
