package com.acare.backend.common;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.acare.backend.dto.request.CheckRuleRequest;
import com.acare.backend.service.RuleEngineService;



import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class BehaviorAspect {
    private final RuleEngineService ruleEngineService;
    
    public void checkRules(CheckRuleRequest request) {
        if(!ruleEngineService.isWorkingHour()) {
            throw new SecurityException("Access denied. Not within working hours.");
        }
        if(!ruleEngineService.isDownloadAllowed(request.getCount())) {
            throw new SecurityException("Access denied. Download limit exceeded.");
        }
        if (!ruleEngineService.isSpamming(request.getUserId())) {
            throw new SecurityException("Access denied. Spamming detected.");
        }

    }
}

