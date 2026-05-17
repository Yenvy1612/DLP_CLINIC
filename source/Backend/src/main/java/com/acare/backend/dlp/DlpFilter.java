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

@Component
@RequiredArgsConstructor
@Slf4j
public class DlpFilter extends OncePerRequestFilter {

    private final DlpScannerService dlpScannerService;
    private final DlpLogService dlpLogService;

    private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT", "PATCH");

    private static final Set<String> SKIP_ALL_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/error",
            "/actuator"
    );

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

        if (shouldSkipAll(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean skipInput = shouldSkipInput(path);

        if (METHODS_WITH_BODY.contains(method)) {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            if (!skipInput) {
                String requestBody = cachedRequest.getCachedBody();
                DlpScanResult inputResult = dlpScannerService.scan(requestBody);

                if (inputResult.isViolated()) {
                    log.warn("[DLP INPUT] Vi pham tai {} {} - Violations: {}",
                            method, path, inputResult.getViolations());

                    saveDlpLog(request, "INPUT_SCAN", inputResult, true);

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"status\":403,\"message\":\"DLP: Phat hien du lieu nhay cam trong request. Yeu cau bi chan.\"}"
                    );
                    return;
                }
            }

            doOutputCheck(cachedRequest, response, filterChain, path, method);
        } else {
            doOutputCheck(request, response, filterChain, path, method);
        }
    }

    private void doOutputCheck(HttpServletRequest request,
                               HttpServletResponse response,
                               FilterChain filterChain,
                               String path, String method)
            throws ServletException, IOException {

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        if (isAdmin()) {
            responseWrapper.copyBodyToResponse();
            return;
        }

        byte[] responseBodyBytes = responseWrapper.getContentAsByteArray();
        String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);

        String contentType = responseWrapper.getContentType();
        if (!responseBody.isEmpty() && contentType != null && contentType.contains("application/json")) {
            DlpScanResult outputResult = dlpScannerService.scan(responseBody);

            if (outputResult.isViolated()) {
                log.warn("[DLP OUTPUT] Vi pham tai {} {} - Violations: {}",
                        method, path, outputResult.getViolations());

                saveDlpLog(request, "OUTPUT_SCAN", outputResult, false);

                String maskedBody = dlpScannerService.mask(responseBody);
                response.setContentType(contentType);
                response.setContentLength(maskedBody.getBytes(StandardCharsets.UTF_8).length);
                response.getOutputStream().write(maskedBody.getBytes(StandardCharsets.UTF_8));
                return;
            }
        }

        responseWrapper.copyBodyToResponse();
    }

    private boolean shouldSkipAll(String path) {
        return SKIP_ALL_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean shouldSkipInput(String path) {
        return SKIP_INPUT_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void saveDlpLog(HttpServletRequest request, String action,
                            DlpScanResult result, boolean blocked) {
        try {
            Long userId = null;
            String username = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                username = jwt.getSubject();
                Object userIdClaim = jwt.getClaim("user_id");
                if (userIdClaim instanceof Number num) {
                    userId = num.longValue();
                }
            }

            String snippet = result.getViolations().toString();
            if (snippet.length() > 200) {
                snippet = snippet.substring(0, 200) + "...";
            }

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
            log.error("[DLP] Loi khi luu DLP log: {}", e.getMessage(), e);
        }
    }

    private RiskLevel determineRiskLevel(String violationType) {
        if (violationType == null) return RiskLevel.LOW;
        return switch (violationType) {
            case "CCCD_DETECTED" -> RiskLevel.HIGH;
            case "SENSITIVE_WORD" -> RiskLevel.HIGH;
            case "PHONE_DETECTED" -> RiskLevel.MEDIUM;
            case "EMAIL_DETECTED" -> RiskLevel.LOW;
            default -> RiskLevel.MEDIUM;
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
