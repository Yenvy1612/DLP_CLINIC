package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.activity.ActivityLogResponse;
import com.acare.backend.service.ActivityLogService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityLogService activityLogService;

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLogResponse>> getRecentActivities() {
        return ResponseEntity.ok(activityLogService.getRecent().stream().map(ActivityLogResponse::from).toList());
    }

    @GetMapping("/recent/user/{userId}")
    public ResponseEntity<List<ActivityLogResponse>> getRecentActivitiesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getRecentForUser(userId).stream().map(ActivityLogResponse::from).toList());
    }

    @GetMapping("/recent/user/{userId}/count")
    public ResponseEntity<Long> getRecentActivitiesCountByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.countForUser(userId));
    }

    @GetMapping("/recent/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActivityLogResponse>> getRecentAdminActivities() {
        return ResponseEntity.ok(activityLogService.getRecentAdmin().stream().map(ActivityLogResponse::from).toList());
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

    @DeleteMapping("/recent/admin/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteRecentActivity(@PathVariable Long notificationId) {
        boolean deleted = activityLogService.deleteRecent(notificationId);
        if (!deleted) {
            return ResponseEntity.status(404).body(ApiResponse.fail(404, "Khong tim thay thong bao", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("DELETE ACTIVITY SUCCESSFULLY", null));
    }
    
}
