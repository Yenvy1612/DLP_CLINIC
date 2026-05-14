package com.acare.backend.dto.security;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SecurityDashboardResponse {
    /** Tổng số sự kiện trong 24h */
    private long totalEvents24h;
    /** Số sự kiện CRITICAL trong 24h */
    private long criticalEvents24h;
    /** Số sự kiện HIGH trong 24h */
    private long highEvents24h;
    /** Số session bị thu hồi trong 24h */
    private long revokedSessions24h;
    /** Top event types trong 24h: {type → count} */
    private Map<String, Long> topEventTypes;
    /** 10 sự kiện CRITICAL gần nhất */
    private List<SecurityEventResponse> recentCriticalEvents;
}
