package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.entity.ActivityLog;
import com.acare.backend.service.ActivityLogService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityLogService activityLogService;

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivities() {
        return ResponseEntity.ok(activityLogService.getRecent());
    }

    @GetMapping("/recent/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getRecentActivitiesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getRecentForUser(userId));
    }

    @GetMapping("/recent/user/{userId}/count")
    public ResponseEntity<Long> getRecentActivitiesCountByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.countForUser(userId));
    }

    @DeleteMapping("/recent/user/{userId}/{notificationId}")
    public ResponseEntity<ApiResponse<Object>> deleteUserNotification(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        boolean deleted = activityLogService.deleteForUser(userId, notificationId);
        if (!deleted) {
            return ResponseEntity.status(404).body(ApiResponse.fail(404, "Khong tim thay thong bao", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("DELETE NOTIFICATION SUCCESSFULLY", null));
    }
    
}
