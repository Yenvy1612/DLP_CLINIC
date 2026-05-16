package com.acare.backend.service;


import com.acare.backend.dto.agent.AgentHeartbeatRequest;
import com.acare.backend.dto.agent.AgentRegisterRequest;
import com.acare.backend.dto.agent.AgentStatusResponse;
import com.acare.backend.entity.Agent;
import com.acare.backend.entity.enums.AgentStatus;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    public Agent register(AgentRegisterRequest request, String ipAddress) {
        return agentRepository.findByDeviceId(request.getDeviceId())
                .map(existing -> {
                    existing.setPlatform(defaultPlatform(request.getPlatform()));
                    existing.setHostname(request.getHostname());
                    existing.setDeviceName(request.getDeviceName());
                    existing.setOsVersion(request.getOsVersion());
                    existing.setAgentVersion(request.getAgentVersion());
                    existing.setAppVersion(request.getAppVersion());
                    existing.setUsername(request.getUsername());
                    existing.setIpAddress(ipAddress);
                    existing.setStatus(AgentStatus.ONLINE);
                    existing.setLastHeartbeat(LocalDateTime.now());

                    return agentRepository.save(existing);
                })
                .orElseGet(() -> agentRepository.save(
                        Agent.builder()
                                .deviceId(request.getDeviceId())
                                .platform(defaultPlatform(request.getPlatform()))
                                .hostname(request.getHostname())
                                .deviceName(request.getDeviceName())
                                .osVersion(request.getOsVersion())
                                .agentVersion(request.getAgentVersion())
                                .appVersion(request.getAppVersion())
                                .username(request.getUsername())
                                .ipAddress(ipAddress)
                                .trusted(true) // demo: true, production nên để false rồi admin duyệt
                                .status(AgentStatus.ONLINE)
                                .registeredAt(LocalDateTime.now())
                                .lastHeartbeat(LocalDateTime.now())
                                .build()
                ));
    }

    public void heartbeat(String deviceId, AgentHeartbeatRequest request) {
        Agent agent = agentRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        agent.setLastHeartbeat(LocalDateTime.now());
        agent.setStatus(request != null && request.getStatus() != null
                ? request.getStatus()
                : AgentStatus.ONLINE);

        if (request != null && request.getAppVersion() != null) {
            agent.setAppVersion(request.getAppVersion());
        }

        agentRepository.save(agent);
    }

    public boolean isTrusted(String deviceId) {
        return agentRepository.existsByDeviceIdAndTrustedTrue(deviceId);
    }

    public AgentStatusResponse getStatus(String deviceId) {
        Agent agent = agentRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        return AgentStatusResponse.builder()
                .installed(true)
                .trusted(Boolean.TRUE.equals(agent.getTrusted()))
                .deviceId(agent.getDeviceId())
                .platform(agent.getPlatform())
                .status(agent.getStatus())
                .message("Agent found")
                .build();
    }

    public List<AgentStatusResponse> getAllAgents() {
        // 1. Lấy tất cả các agent từ database
        List<Agent> agents = agentRepository.findAll();

        // 2. Chuyển đổi danh sách Agent entity sang danh sách AgentStatusResponse
        return agents.stream()
                .map(agent -> AgentStatusResponse.builder()
                        .installed(true)
                        .trusted(Boolean.TRUE.equals(agent.getTrusted()))
                        .deviceId(agent.getDeviceId())
                        .platform(agent.getPlatform())
                        .status(agent.getStatus())
                        .message("Agent found")
                        .build())
                .toList();
    }

    private String defaultPlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            return "ANDROID";
        }

        return platform.toUpperCase();
    }
}
