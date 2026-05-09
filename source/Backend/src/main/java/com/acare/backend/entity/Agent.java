package com.acare.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String deviceId;

    private String hostname;

    private String osVersion;

    private String agentVersion;

    private String ipAddress;

    private Boolean trusted;

    private LocalDateTime registeredAt;

    private LocalDateTime lastHeartbeat;

    private String username;
}