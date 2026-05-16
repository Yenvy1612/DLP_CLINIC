package com.acare.backend.dto.agent;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentPolicyResponse {

    private String version;

    private List<PatternRuleResponse> patterns;

    private List<String> keywords;

    private Map<String, Object> settings;
}
