package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.entity.ActivityLog;
import com.acare.backend.service.ActivityLogService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    /* Lấy các hoạt động của user */
    private final ActivityLogService activityLogService;

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivities() {
        return ResponseEntity.ok(activityLogService.getRecent());
    }
    
}
