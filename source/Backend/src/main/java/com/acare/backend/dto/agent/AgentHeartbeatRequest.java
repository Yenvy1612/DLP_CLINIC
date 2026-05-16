package com.acare.backend.dto.agent;

import com.acare.backend.entity.enums.AgentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentHeartbeatRequest {

    private String deviceId;

    private String platform;

    // ONLINE / OFFLINE
    private AgentStatus status;

    private Integer batteryLevel;

    private String networkType;

    private String appVersion;
}