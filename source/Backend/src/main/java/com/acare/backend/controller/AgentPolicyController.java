package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.agent.AgentPolicyResponse;
import com.acare.backend.service.AgentPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentPolicyController {

    private final AgentPolicyService agentPolicyService;

    @GetMapping("/policy")
    public ApiResponse<AgentPolicyResponse> getPolicy() {
        return ApiResponse.<AgentPolicyResponse>builder()
                .data(agentPolicyService.getPolicy())
                .build();
    }
}
