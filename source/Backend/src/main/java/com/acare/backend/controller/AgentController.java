package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.agent.AgentRegisterRequest;
import com.acare.backend.dto.agent.AgentStatusResponse;
import com.acare.backend.entity.Agent;
import com.acare.backend.service.AgentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/register")
    public ApiResponse<AgentStatusResponse> register(
            @RequestBody AgentRegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        Agent agent = agentService.register(
                request,
                servletRequest.getRemoteAddr()
        );

        return ApiResponse.<AgentStatusResponse>builder()
                .data(AgentStatusResponse.builder()
                        .installed(true)
                        .trusted(Boolean.TRUE.equals(agent.getTrusted()))
                        .deviceId(agent.getDeviceId())
                        .message("Agent registered successfully")
                        .build())
                .build();
    }

    @PostMapping("/{deviceId}/heartbeat")
    public ApiResponse<String> heartbeat(@PathVariable String deviceId) {
        agentService.heartbeat(deviceId);

        return ApiResponse.<String>builder()
                .data("Heartbeat received")
                .build();
    }

    @GetMapping("/{deviceId}/status")
    public ApiResponse<AgentStatusResponse> status(@PathVariable String deviceId) {
        boolean trusted = agentService.isTrusted(deviceId);

        return ApiResponse.<AgentStatusResponse>builder()
                .data(AgentStatusResponse.builder()
                        .installed(trusted)
                        .trusted(trusted)
                        .deviceId(deviceId)
                        .message(trusted ? "Trusted device" : "Device not trusted")
                        .build())
                .build();
    }
}
