package com.acare.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="activity_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;         // "APPOINTMENT", "SERVICE", "USER", ...
    @Column String message;   
    @Column(name = "actor_user_id")
    private Long actorUserId;
    @Column(name = "target_user_id")
    private Long targetUserId;
    @Column(name = "appointment_id")
    private Long appointmentId;
    private LocalDateTime time; 

    @PrePersist
    protected void onCreate() {
        if (this.time == null) {
            this.time = LocalDateTime.now();
        }
    }

    public static ActivityLog of(String type, String message) {
        return ActivityLog.builder()
                .type(type)
                .message(message)
                .time(LocalDateTime.now())
                .build();
    }

    public static ActivityLog notification(String type, Long actorUserId, Long targetUserId, Long appointmentId, String message) {
        return ActivityLog.builder()
                .type(type)
                .message(message)
                .actorUserId(actorUserId)
                .targetUserId(targetUserId)
                .appointmentId(appointmentId)
                .time(LocalDateTime.now())
                .build();
    }
}