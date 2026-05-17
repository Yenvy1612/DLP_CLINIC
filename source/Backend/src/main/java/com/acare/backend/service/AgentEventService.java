package com.acare.backend.service;


import com.acare.backend.dto.agent.AgentEventRequest;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.DlpLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgentEventService {

    private final DlpLogRepository dlpLogRepository;
    private final AgentService agentService;
    private final ObjectMapper objectMapper;

    public void handleEvent(AgentEventRequest request) {
        if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
            throw new ResourceNotFoundException("Agent deviceId is required");
        }

        if (isDlpViolation(request)) {
            boolean blocked = shouldBlock(request);
            saveDlpLog(request, blocked);
            if (blocked) {
                agentService.quarantineAgentAndRevokeSession(
                        request.getDeviceId(),
                        request.getUserId(),
                        "Agent DLP violation: " + request.getViolationType()
                );
            }
        }

        // Nếu muốn log hành vi thường thì gọi ActivityLogService ở đây.
        // Ví dụ VIEW_PATIENT_DETAIL, LOGIN_SUCCESS...
    }

    private boolean isDlpViolation(AgentEventRequest request) {
        return request.getViolationType() != null
                && !request.getViolationType().isBlank()
                && !"NONE".equalsIgnoreCase(request.getViolationType());
    }

    private void saveDlpLog(AgentEventRequest request, boolean blocked) {
        DlpLog log = DlpLog.builder()
                .userId(request.getUserId())
                .deviceId(request.getDeviceId())
                .sourceType(defaultSourceType(request.getSourceType()))
                .platform(defaultPlatform(request.getPlatform()))
                .eventType(request.getEventType())
                .action(request.getAction())
                .violationType(request.getViolationType())
                .severity(defaultSeverity(request.getSeverity()))
                .riskLevel(toRiskLevel(request.getSeverity()))
                .blocked(blocked)
                .endpoint("/api/agent-events")
                .httpMethod("POST")
                .contentSnippet(request.getContentSnippet())
                .details(toJson(request.getDetails()))
                .timestamp(parseTimestamp(request.getTimestamp()))
                .build();

        dlpLogRepository.save(log);
    }

    private String defaultSourceType(String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            return "ANDROID_AGENT";
        }

        return sourceType;
    }

    private String defaultPlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            return "ANDROID";
        }

        return platform;
    }

    private String defaultSeverity(String severity) {
        if (severity == null || severity.isBlank()) {
            return "LOW";
        }

        return severity;
    }

    private RiskLevel toRiskLevel(String severity) {
        if (severity == null || severity.isBlank()) return RiskLevel.LOW;
        return switch (severity.trim().toUpperCase()) {
            case "CRITICAL" -> RiskLevel.CRITICAL;
            case "HIGH" -> RiskLevel.HIGH;
            case "MEDIUM" -> RiskLevel.MEDIUM;
            default -> RiskLevel.LOW;
        };
    }

    private boolean shouldBlock(AgentEventRequest request) {
        String sev = defaultSeverity(request.getSeverity()).toUpperCase();
        if ("HIGH".equals(sev) || "CRITICAL".equals(sev)) return true;

        String violation = request.getViolationType();
        if (violation == null) return false;
        String v = violation.toUpperCase();
        return v.contains("CCCD") || v.contains("SENSITIVE") || v.contains("HIV");
    }

    private String toJson(Map<String, Object> details) {
        if (details == null) {
            return "{}";
        }

        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            return details.toString();
        }
    }

    private LocalDateTime parseTimestamp(String raw) {
        if (raw == null || raw.isBlank()) return LocalDateTime.now();
        try {
            return OffsetDateTime.parse(raw).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return Instant.parse(raw).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(raw);
        } catch (Exception ignored) {
        }
        return LocalDateTime.now();
    }
}
