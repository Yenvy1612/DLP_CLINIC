package com.acare.backend.service;


import com.acare.backend.dto.agent.AgentEventRequest;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.AgentRepository;
import com.acare.backend.repository.DlpLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgentEventService {

    private final AgentRepository agentRepository;
    private final DlpLogRepository dlpLogRepository;
    private final ObjectMapper objectMapper;

    public void handleEvent(AgentEventRequest request) {
        agentRepository.findByDeviceId(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (isDlpViolation(request)) {
            saveDlpLog(request);
        }

        // Nếu muốn log hành vi thường thì gọi ActivityLogService ở đây.
        // Ví dụ VIEW_PATIENT_DETAIL, LOGIN_SUCCESS...
    }

    private boolean isDlpViolation(AgentEventRequest request) {
        return request.getViolationType() != null
                && !request.getViolationType().isBlank()
                && !"NONE".equalsIgnoreCase(request.getViolationType());
    }

    private void saveDlpLog(AgentEventRequest request) {
        DlpLog log = DlpLog.builder()
                .userId(request.getUserId())
                .deviceId(request.getDeviceId())
                .sourceType(defaultSourceType(request.getSourceType()))
                .platform(defaultPlatform(request.getPlatform()))
                .eventType(request.getEventType())
                .action(request.getAction())
                .violationType(request.getViolationType())
                .severity(defaultSeverity(request.getSeverity()))
                .contentSnippet(request.getContentSnippet())
                .details(toJson(request.getDetails()))
                .timestamp(request.getTimestamp() != null
                        ? request.getTimestamp()
                        : LocalDateTime.now())
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
}
