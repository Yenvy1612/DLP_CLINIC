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
}