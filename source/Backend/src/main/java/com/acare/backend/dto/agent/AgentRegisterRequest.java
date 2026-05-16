package com.acare.backend.dto.agent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentRegisterRequest {

    private String deviceId;

    // ANDROID / WINDOWS
    private String platform;

    private String hostname;

    private String deviceName;

    private String osVersion;

    private String agentVersion;

    private String appVersion;

    private String username;
}
