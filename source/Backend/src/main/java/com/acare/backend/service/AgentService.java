package com.acare.backend.service;

import com.acare.backend.dto.agent.AgentRegisterRequest;
import com.acare.backend.entity.Agent;
import com.acare.backend.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    public Agent register(AgentRegisterRequest request, String ipAddress) {
        return agentRepository.findByDeviceId(request.getDeviceId())
                .map(existing -> {
                    existing.setHostname(request.getHostname());
                    existing.setOsVersion(request.getOsVersion());
                    existing.setAgentVersion(request.getAgentVersion());
                    existing.setUsername(request.getUsername());
                    existing.setIpAddress(ipAddress);
                    existing.setLastHeartbeat(LocalDateTime.now());
                    return agentRepository.save(existing);
                })
                .orElseGet(() -> agentRepository.save(
                        Agent.builder()
                                .deviceId(request.getDeviceId())
                                .hostname(request.getHostname())
                                .osVersion(request.getOsVersion())
                                .agentVersion(request.getAgentVersion())
                                .username(request.getUsername())
                                .ipAddress(ipAddress)
                                .trusted(true)
                                .registeredAt(LocalDateTime.now())
                                .lastHeartbeat(LocalDateTime.now())
                                .build()
                ));
    }

    public boolean isTrusted(String deviceId) {
        return agentRepository.existsByDeviceIdAndTrustedTrue(deviceId);
    }

    public void heartbeat(String deviceId) {
        agentRepository.findByDeviceId(deviceId).ifPresent(agent -> {
            agent.setLastHeartbeat(LocalDateTime.now());
            agentRepository.save(agent);
        });
    }
}
