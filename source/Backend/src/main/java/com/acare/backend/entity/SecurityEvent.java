package com.acare.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_events", indexes = {
        @Index(name = "idx_sec_events_user", columnList = "user_id"),
        @Index(name = "idx_sec_events_ip", columnList = "ip_address"),
        @Index(name = "idx_sec_events_type", columnList = "event_type"),
        @Index(name = "idx_sec_events_time", columnList = "occurred_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID người dùng (null nếu chưa xác thực) */
    @Column(name = "user_id")
    private Long userId;

    /** Loại sự kiện bất thường */
    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    /** Mức độ nghiêm trọng: LOW / MEDIUM / HIGH / CRITICAL */
    @Column(nullable = false, length = 20)
    private String severity;

    /** IP của request */
    @Column(name = "ip_address", length = 60)
    private String ipAddress;

    /** Endpoint bị tác động */
    @Column(name = "request_uri", length = 300)
    private String requestUri;

    /** HTTP method */
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    /** Mô tả chi tiết hành vi */
    @Column(length = 1000)
    private String description;

    /** Risk score tại thời điểm xảy ra (0–100) */
    @Column(name = "risk_score")
    private Integer riskScore;

    /** Hành động đã thực hiện: LOGGED / WARNED / BLOCKED / TOKEN_REVOKED */
    @Column(name = "action_taken", length = 30)
    private String actionTaken;

    /** Thời điểm xảy ra */
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    protected void prePersist() {
        if (this.occurredAt == null) {
            this.occurredAt = LocalDateTime.now();
        }
    }
}
