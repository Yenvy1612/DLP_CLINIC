package com.acare.backend.dlp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.service.DlpLogService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ==================== DLP FILTER ====================
 * Đây là TRÁI TIM của module DLP (Data Loss Prevention).
 *
 * Filter này nằm trong Spring Security Filter Chain, chạy SAU khi JWT đã xác thực.
 * Mọi request/response đều đi qua đây.
 *
 * HAI NHIỆM VỤ CHÍNH:
 *
 * 1. INPUT CHECK (POST/PUT):
 *    - Đọc request body → quét bằng regex (CCCD, SĐT, email, từ khóa nhạy cảm)
 *    - Nếu vi phạm → ghi log + trả 403 Forbidden (chặn request, không cho đến Controller)
 *
 * 2. OUTPUT CHECK (GET/mọi method):
 *    - Để request đi qua Controller bình thường
 *    - Chặn response trước khi trả về client → quét JSON response body
 *    - Nếu vi phạm → MASK dữ liệu nhạy cảm (VD: CCCD → 0123****8901)
 *    - Response đã mask được trả về client
 *
 * LUỒNG XỬ LÝ:
 *   Client → JWT Auth → [DLP FILTER] → Controller → DB → [DLP FILTER] → Client
 *                         ↑ Input check                    ↑ Output check
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DlpFilter extends OncePerRequestFilter {

    private final DlpScannerService dlpScannerService;
    private final DlpLogService dlpLogService;

    /**
     * Các HTTP method có request body cần kiểm tra input.
     * GET, DELETE thường không có body → chỉ check output.
     */
    private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT", "PATCH");

    /**
     * SKIP_ALL: Bỏ qua DLP HOÀN TOÀN (không quét input, không quét output).
     * Dùng cho auth endpoints — login chứa password, register chứa PII hợp lệ.
     */
    private static final Set<String> SKIP_ALL_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/error",
            "/actuator"
    );

    /**
     * SKIP_INPUT: Bỏ qua INPUT CHECK nhưng VẪN quét OUTPUT.
     *
     * Đây là các endpoint mà PII (CCCD, SĐT, email) ĐƯỢC PHÉP xuất hiện trong request body
     * vì nghiệp vụ yêu cầu (tạo user, cập nhật hồ sơ, ghi bệnh án).
     *
     * Tuy nhiên, OUTPUT vẫn được mask để bảo vệ PII khi trả dữ liệu về client.
     *
     * Ai nhập PII:
     * - /api/users/**              → Admin tạo/sửa user (nhập CCCD, SĐT, email)
     * - /api/patient-profiles/**   → Bác sĩ/Admin cập nhật hồ sơ bệnh nhân (BHYT, dị ứng)
     * - /api/medical-records/**    → Bác sĩ tạo/sửa bệnh án (chẩn đoán, phác đồ)
     * - /api/appointments/**       → Bệnh nhân/Bác sĩ đặt lịch (thông tin liên hệ)
     */
    private static final Set<String> SKIP_INPUT_PATHS = Set.of(
            "/api/users",
            "/api/patient-profiles",
            "/api/medical-records",
            "/api/appointments"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // --- Bỏ qua hoàn toàn (auth, actuator) ---
        if (shouldSkipAll(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- Kiểm tra endpoint có được phép nhập PII không ---
        boolean skipInput = shouldSkipInput(path);

        // ==================== INPUT CHECK (POST/PUT/PATCH) ====================
        if (METHODS_WITH_BODY.contains(method)) {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            // CHỈ quét input nếu endpoint KHÔNG nằm trong danh sách skip
            // VD: POST /api/users → skip input (Admin được phép nhập CCCD)
            //     POST /api/search → quét input (không nên chứa CCCD)
            if (!skipInput) {
                String requestBody = cachedRequest.getCachedBody();
                DlpScanResult inputResult = dlpScannerService.scan(requestBody);

                if (inputResult.isViolated()) {
                    log.warn("[DLP INPUT] Vi phạm tại {} {} - Violations: {}",
                            method, path, inputResult.getViolations());

                    saveDlpLog(request, "INPUT_SCAN", inputResult, true);

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"status\":403,\"message\":\"DLP: Phát hiện dữ liệu nhạy cảm trong request. Yêu cầu bị chặn.\"}"
                    );
                    return;
                }
            }

            // Output check vẫn chạy cho TẤT CẢ endpoint (kể cả skip input)
            doOutputCheck(cachedRequest, response, filterChain, path, method);
        } else {
            doOutputCheck(request, response, filterChain, path, method);
        }
    }

    /**
     * OUTPUT CHECK: quét response body trước khi trả về client.
     *
     * Quy tắc theo role:
     * - ADMIN:   Xem FULL dữ liệu (không mask) — cần quản lý user, xem CCCD đầy đủ
     * - DOCTOR:  Bị mask PII (CCCD, SĐT) — chỉ cần thông tin y tế, không cần CCCD bệnh nhân
     * - PATIENT: Bị mask PII — bảo vệ thông tin người khác
     *
     * Dùng ContentCachingResponseWrapper để cache response body.
     * Sau khi Controller ghi xong response → đọc body từ cache → quét → mask nếu cần.
     */
    private void doOutputCheck(HttpServletRequest request,
                               HttpServletResponse response,
                               FilterChain filterChain,
                               String path, String method)
            throws ServletException, IOException {

        // Wrap response để cache body (mặc định response body chỉ ghi 1 lần)
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Cho request đi qua Controller bình thường
        filterChain.doFilter(request, responseWrapper);

        // --- ADMIN được xem full dữ liệu, không mask ---
        if (isAdmin()) {
            responseWrapper.copyBodyToResponse();
            return;
        }

        // --- Với DOCTOR/PATIENT: quét và mask response ---
        byte[] responseBodyBytes = responseWrapper.getContentAsByteArray();
        String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);

        // Chỉ quét nếu response có nội dung và là JSON
        String contentType = responseWrapper.getContentType();
        if (responseBody.length() > 0 && contentType != null && contentType.contains("application/json")) {

            DlpScanResult outputResult = dlpScannerService.scan(responseBody);

            if (outputResult.isViolated()) {
                log.warn("[DLP OUTPUT] Vi phạm tại {} {} - Violations: {}",
                        method, path, outputResult.getViolations());

                // Ghi log (output vi phạm nhưng KHÔNG block, chỉ mask)
                saveDlpLog(request, "OUTPUT_SCAN", outputResult, false);

                // MASK dữ liệu nhạy cảm trong response body
                // VD: "012345678901" → "0123****8901"
                String maskedBody = dlpScannerService.mask(responseBody);

                // Ghi response đã mask trở lại
                response.setContentType(contentType);
                response.setContentLength(maskedBody.getBytes(StandardCharsets.UTF_8).length);
                response.getOutputStream().write(maskedBody.getBytes(StandardCharsets.UTF_8));
                return;
            }
        }

        // Nếu output an toàn → copy response cache về response gốc (bắt buộc khi dùng wrapper)
        responseWrapper.copyBodyToResponse();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Bỏ qua DLP hoàn toàn (auth, error, actuator).
     */
    private boolean shouldSkipAll(String path) {
        return SKIP_ALL_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Bỏ qua INPUT CHECK cho các endpoint mà PII là dữ liệu hợp lệ.
     * VD: POST /api/users/add-user chứa CCCD → hợp lệ vì Admin đang tạo user.
     *     POST /api/search chứa CCCD → bất thường → cần chặn.
     */
    private boolean shouldSkipInput(String path) {
        return SKIP_INPUT_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Kiểm tra user hiện tại có phải ADMIN không.
     * ADMIN được xem full dữ liệu (không mask output).
     *
     * Đọc role từ JWT authorities trong SecurityContext.
     */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Lưu 1 record DLP log vào database.
     *
     * Lấy thông tin user từ SecurityContext (JWT đã xác thực trước đó).
     * Nếu user chưa đăng nhập (anonymous) → userId = null.
     */
    private void saveDlpLog(HttpServletRequest request, String action,
                            DlpScanResult result, boolean blocked) {
        try {
            // Lấy userId và username từ JWT token đã xác thực
            Long userId = null;
            String username = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                // Subject trong JWT là email/username
                username = jwt.getSubject();
                // userId có thể nằm trong claim "user_id" (nếu có)
                Object userIdClaim = jwt.getClaim("user_id");
                if (userIdClaim instanceof Number num) {
                    userId = num.longValue();
                }
            }

            // Tạo snippet (200 ký tự đầu) để lưu vào log
            String snippet = result.getViolations().toString();
            if (snippet.length() > 200) {
                snippet = snippet.substring(0, 200) + "...";
            }

            // Xác định mức rủi ro dựa trên loại vi phạm
            RiskLevel riskLevel = determineRiskLevel(result.getPrimaryViolationType());

            DlpLog dlpLog = DlpLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .endpoint(request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .contentSnippet(snippet)
                    .violationType(result.getPrimaryViolationType())
                    .riskLevel(riskLevel)
                    .blocked(blocked)
                    .clientIp(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            dlpLogService.save(dlpLog);
        } catch (Exception e) {
            // Ghi log lỗi nhưng KHÔNG throw exception → không ảnh hưởng đến request chính
            log.error("[DLP] Lỗi khi lưu DLP log: {}", e.getMessage(), e);
        }
    }

    /**
     * Xác định mức rủi ro dựa trên loại vi phạm.
     *
     * Quy tắc:
     * - CCCD_DETECTED → HIGH (thông tin định danh quan trọng nhất)
     * - SENSITIVE_WORD → HIGH (từ khóa y tế nhạy cảm)
     * - PHONE_DETECTED → MEDIUM
     * - EMAIL_DETECTED → LOW
     * - Khác → MEDIUM (mặc định)
     */
    private RiskLevel determineRiskLevel(String violationType) {
        if (violationType == null) return RiskLevel.LOW;
        return switch (violationType) {
            case "CCCD_DETECTED"   -> RiskLevel.HIGH;
            case "SENSITIVE_WORD"  -> RiskLevel.HIGH;
            case "PHONE_DETECTED"  -> RiskLevel.MEDIUM;
            case "EMAIL_DETECTED"  -> RiskLevel.LOW;
            default                -> RiskLevel.MEDIUM;
        };
    }

    /**
     * Lấy IP thật của client.
     * Ưu tiên header X-Forwarded-For (khi qua reverse proxy/load balancer).
     * Fallback về getRemoteAddr() nếu không có.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For có thể chứa nhiều IP (proxy chain), lấy cái đầu tiên
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
