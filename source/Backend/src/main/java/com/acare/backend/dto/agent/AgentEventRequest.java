package com.acare.backend.dto.agent;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentEventRequest {

    private String deviceId;

    private String platform;

    private Long userId;

    // ANDROID_AGENT
    private String sourceType;

    // FORM_DLP_MATCHED, COPY_PATIENT_DATA, EXPORT_BLOCKED...
    private String eventType;

    // COPY, EXPORT, SUBMIT_FORM, VIEW...
    private String action;

    // CCCD, PHONE, EMAIL, KEYWORD:HIV...
    private String violationType;

    // LOW, MEDIUM, HIGH, CRITICAL
    private String severity;

    private String contentSnippet;

    private Map<String, Object> details;

    private String timestamp;
}
