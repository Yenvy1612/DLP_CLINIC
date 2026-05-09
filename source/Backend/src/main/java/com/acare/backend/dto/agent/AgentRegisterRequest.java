package com.acare.backend.dto.agent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentRegisterRequest {
    private String deviceId;
    private String hostname;
    private String osVersion;
    private String agentVersion;
    private String username;
}
