package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.security.SecurityDashboardResponse;
import com.acare.backend.dto.security.SecurityEventResponse;
import com.acare.backend.entity.SecurityEvent;
import com.acare.backend.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SecurityEventController — Dashboard bảo mật dành cho ADMIN.
 * <p>
 * Tất cả endpoint đều yêu cầu role ADMIN.
 * Hoàn toàn độc lập với:
 * - /api/agents  (Agent của đồng nghiệp 1)
 * - DlpLog       (DLP của đồng nghiệp 2)
 */
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SecurityEventController {

    private final SecurityEventRepository securityEventRepository;

    /**
     * GET /api/security/events
     * Lấy toàn bộ security events, phân trang, mới nhất trước.
     */
    @GetMapping("/events")
    public ApiResponse<Page<SecurityEventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String severity
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SecurityEvent> events = severity != null
                ? securityEventRepository.findBySeverityOrderByOccurredAtDesc(severity.toUpperCase(), pageable)
                : securityEventRepository.findAllByOrderByOccurredAtDesc(pageable);

        return ApiResponse.ok("Security events", events.map(this::toResponse));
    }

    /**
     * GET /api/security/events/user/{userId}
     * Lấy security events của một user cụ thể.
     */
    @GetMapping("/events/user/{userId}")
    public ApiResponse<Page<SecurityEventResponse>> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SecurityEvent> events = securityEventRepository
                .findByUserIdOrderByOccurredAtDesc(userId, pageable);
        return ApiResponse.ok("Events for user " + userId, events.map(this::toResponse));
    }

    /**
     * GET /api/security/dashboard
     * Tổng quan bảo mật trong 24h — dành cho Admin Dashboard.
     */
    @GetMapping("/dashboard")
    public ApiResponse<SecurityDashboardResponse> getDashboard() {
        LocalDateTime since24h = LocalDateTime.now().minusHours(24);

        long totalEvents = securityEventRepository.countByIpAddressAndOccurredAtAfter(null, since24h);
        // Dùng countAll vì không có userId filter cho total
        long total = securityEventRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(0, 1))
                .getTotalElements();
        long total24h = securityEventRepository
                .findAllByOrderByOccurredAtDesc(PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .filter(e -> e.getOccurredAt().isAfter(since24h))
                .count();

        long critical24h = securityEventRepository
                .findBySeverityAndOccurredAtAfterOrderByOccurredAtDesc("CRITICAL", since24h).size();

        long high24h = securityEventRepository
                .findBySeverityAndOccurredAtAfterOrderByOccurredAtDesc("HIGH", since24h).size();

        long revoked24h = securityEventRepository
                .findBySeverityAndOccurredAtAfterOrderByOccurredAtDesc("CRITICAL", since24h)
                .stream()
                .filter(e -> "TOKEN_REVOKED".equals(e.getActionTaken()))
                .count();

        // Top event types
        List<Object[]> rawStats = securityEventRepository.countByEventTypeSince(since24h);
        Map<String, Long> topTypes = rawStats.stream()
                .limit(5)
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // Recent critical events (top 10)
        List<SecurityEventResponse> recentCritical = securityEventRepository
                .findBySeverityAndOccurredAtAfterOrderByOccurredAtDesc("CRITICAL", since24h)
                .stream()
                .limit(10)
                .map(this::toResponse)
                .collect(Collectors.toList());

        SecurityDashboardResponse dashboard = SecurityDashboardResponse.builder()
                .totalEvents24h(total24h)
                .criticalEvents24h(critical24h)
                .highEvents24h(high24h)
                .revokedSessions24h(revoked24h)
                .topEventTypes(topTypes)
                .recentCriticalEvents(recentCritical)
                .build();

        return ApiResponse.ok("Security dashboard", dashboard);
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────

    private SecurityEventResponse toResponse(SecurityEvent e) {
        return SecurityEventResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .eventType(e.getEventType())
                .severity(e.getSeverity())
                .ipAddress(e.getIpAddress())
                .requestUri(e.getRequestUri())
                .httpMethod(e.getHttpMethod())
                .description(e.getDescription())
                .riskScore(e.getRiskScore())
                .actionTaken(e.getActionTaken())
                .occurredAt(e.getOccurredAt())
                .build();
    }
}
