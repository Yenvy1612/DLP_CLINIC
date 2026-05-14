package com.acare.backend.service;

import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.acare.backend.config.properties.RuleDefinitionProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleEngineService {
    private final RuleDefinitionProperties ruleDefinitionProperties;
    private final Long breakTime=12*1000L;
    private final ConcurrentHashMap<Long,Long> requestMap= new ConcurrentHashMap<>();
    public boolean isWorkingHour() {
        LocalTime now = LocalTime.now();
        return now.isAfter(LocalTime.of(ruleDefinitionProperties.getTimeBasedStarttime(), 0)) 
                        && now.isBefore(LocalTime.of(ruleDefinitionProperties.getTimeBasedEndtime(), 30));
    }
    
    public boolean isSpamming(Long userId) {
        
        Long lastRequestTime = requestMap.get(userId);
        if (lastRequestTime == null || System.currentTimeMillis() - lastRequestTime >= breakTime) {
            requestMap.put(userId, System.currentTimeMillis());
            return false;
        }

        return true;
    }
    public boolean isDownloadAllowed(Long count) {
        return count<=ruleDefinitionProperties.getVolumeBased();
    }

}

