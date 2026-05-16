package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.agent.AgentEventRequest;
import com.acare.backend.service.AgentEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent-events")
@RequiredArgsConstructor
public class AgentEventController {

    private final AgentEventService agentEventService;

    @PostMapping
    public ApiResponse<String> receiveEvent(@RequestBody AgentEventRequest request) {
        agentEventService.handleEvent(request);

        return ApiResponse.<String>builder()
                .data("Agent event received")
                .build();
    }
}
