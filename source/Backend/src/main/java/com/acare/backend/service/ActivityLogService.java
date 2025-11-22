package com.acare.backend.service;

import java.time.LocalDateTime;
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
        ActivityLog al = new ActivityLog(null, type, message, LocalDateTime.now());
        repo.save(al);
    }

    public List<ActivityLog> getRecent() {
        return repo.findTop10ByOrderByTimeDesc();
    }
}
