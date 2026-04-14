package com.acare.backend.dto.activity;

import java.time.LocalDateTime;

import com.acare.backend.entity.ActivityLog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogResponse {
    private Long id;
    private String type;
    private String message;
    private Long actorUserId;
    private Long targetUserId;
    private Long appointmentId;
    private LocalDateTime time;

    public static ActivityLogResponse from(ActivityLog activityLog) {
        if (activityLog == null) {
            return null;
        }

        return ActivityLogResponse.builder()
                .id(activityLog.getId())
                .type(activityLog.getType())
                .message(activityLog.getMessage())
                .actorUserId(activityLog.getActorUserId())
                .targetUserId(activityLog.getTargetUserId())
                .appointmentId(activityLog.getAppointmentId())
                .time(activityLog.getTime())
                .build();
    }
}
