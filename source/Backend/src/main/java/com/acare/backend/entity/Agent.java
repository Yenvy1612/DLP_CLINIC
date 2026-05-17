package com.acare.backend.entity;

import com.acare.backend.entity.enums.AgentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String deviceId;

    // ANDROID / WINDOWS
    @Column(nullable = false)
    private String platform;

    // Windows dùng hostname, Android có thể null
    private String hostname;

    // Android dùng deviceName: Samsung A55, Pixel 8...
    private String deviceName;

    private String osVersion;

    private String agentVersion;

    private String appVersion;

    private String ipAddress;

    private Boolean trusted;

    // ONLINE / OFFLINE / BLOCKED
    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime lastHeartbeat;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}