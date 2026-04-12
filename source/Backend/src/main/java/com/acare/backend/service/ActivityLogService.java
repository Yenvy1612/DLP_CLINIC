package com.acare.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.ActivityLog;
import com.acare.backend.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    
    /* cho phép thêm log và trả về 10 log gần nhất */
    private final ActivityLogRepository repo;

    public void add(String type, String message) {
        repo.save(ActivityLog.of(type, message));
    }

    public List<ActivityLog> getRecent() {
        return repo.findTop10ByOrderByTimeDesc();
    }
}
