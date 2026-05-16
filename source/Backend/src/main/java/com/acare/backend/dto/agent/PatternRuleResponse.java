package com.acare.backend.dto.agent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternRuleResponse {

    private String name;

    private String regex;

    private String severity;
}
