package com.acare.backend.service;

import com.acare.backend.dto.agent.AgentPolicyResponse;
import com.acare.backend.dto.agent.PatternRuleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgentPolicyService {

    public AgentPolicyResponse getPolicy() {
        return AgentPolicyResponse.builder()
                .version("1.0.0")
                .patterns(List.of(
                        PatternRuleResponse.builder()
                                .name("CCCD")
                                .regex("\\b0\\d{11}\\b")
                                .severity("HIGH")
                                .build(),
                        PatternRuleResponse.builder()
                                .name("PHONE")
                                .regex("\\b(84|0[35789])\\d{8}\\b")
                                .severity("MEDIUM")
                                .build(),
                        PatternRuleResponse.builder()
                                .name("EMAIL")
                                .regex("\\b[\\w.-]+@[\\w.-]+\\.\\w{2,4}\\b")
                                .severity("MEDIUM")
                                .build()
                ))
                .keywords(List.of("HIV", "Ung thư", "Tuyệt mật"))
                .settings(Map.of(
                        "scanForm", true,
                        "scanCopy", true,
                        "scanExport", true,
                        "blockCopyOnViolation", true,
                        "blockExportOnViolation", true
                ))
                .build();
    }
}
