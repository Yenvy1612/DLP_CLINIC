package com.acare.backend.dto.agent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStatusResponse {
    private boolean installed;
    private boolean trusted;
    private String deviceId;
    private String message;
}
