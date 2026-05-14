package com.acare.backend.dto.security;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SecurityEventResponse {
    private Long id;
    private Long userId;
    private String eventType;
    private String severity;
    private String ipAddress;
    private String requestUri;
    private String httpMethod;
    private String description;
    private Integer riskScore;
    private String actionTaken;
    private LocalDateTime occurredAt;
}
