package com.acare.backend.entity;

import java.time.LocalDateTime;

import com.acare.backend.entity.enums.RiskLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "dlp_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DlpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private String sourceType;
    private String platform;
    private String eventType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "action", length = 50, nullable = false)
    private String action;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "content_snippet", columnDefinition = "TEXT")
    private String contentSnippet;

    @Column(name = "violation_type", length = 80)
    private String violationType;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    @Builder.Default
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "blocked")
    @Builder.Default
    private Boolean blocked = false;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.timestamp == null) {
            this.timestamp = this.createdAt;
        }
        if (this.severity == null && this.riskLevel != null) {
            this.severity = this.riskLevel.name();
        }
    }
}
