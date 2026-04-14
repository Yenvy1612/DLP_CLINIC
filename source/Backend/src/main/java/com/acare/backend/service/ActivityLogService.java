package com.acare.backend.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.acare.backend.entity.ActivityLog;
import com.acare.backend.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private static final String USER_TYPE_PREFIX = "USER_NOTICE_";
    private static final String ADMIN_TYPE = "ADMIN";
    
    /* cho phép thêm log và trả về 10 log gần nhất */
    private final ActivityLogRepository repo;

    public void add(String type, String message) {
        repo.save(ActivityLog.of(type, message));
    }

    public void addAdmin(String message) {
        repo.save(ActivityLog.of(ADMIN_TYPE, message));
    }

    public void addAdminIfCurrentUser(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (!isAdmin) {
            return;
        }

        addAdmin(message);
    }

    public void addNotification(String type, Long actorUserId, Long targetUserId, Long appointmentId, String message) {
        if (targetUserId == null) {
            return;
        }
        String tokenizedMessage = "[USER:" + targetUserId + "] " + message;
        repo.save(ActivityLog.notification(type, actorUserId, targetUserId, appointmentId, tokenizedMessage));
    }

    public void addForUser(String type, Long userId, String message) {
        if (userId == null) {
            return;
        }
        addNotification(USER_TYPE_PREFIX + userId, null, userId, null, message);
    }

    public List<ActivityLog> getRecent() {
        return repo.findTop10ByOrderByTimeDesc();
    }

    public List<ActivityLog> getRecentForUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<ActivityLog> byTargetUser = repo.findTop20ByTargetUserIdOrderByTimeDesc(userId);
        if (byTargetUser != null && !byTargetUser.isEmpty()) {
            return byTargetUser;
        }

        List<ActivityLog> typed = repo.findTop20ByTypeOrderByTimeDesc(USER_TYPE_PREFIX + userId);
        if (typed != null && !typed.isEmpty()) {
            return typed;
        }
        String token = "[USER:" + userId + "]";
        return repo.findTop20ByMessageContainingOrderByTimeDesc(token);
    }

    public List<ActivityLog> getRecentAdmin() {
        return repo.findTop20ByTypeOrderByTimeDesc(ADMIN_TYPE);
    }

    public long countForUser(Long userId) {
        if (userId == null) {
            return 0;
        }
        long byTargetUser = repo.countByTargetUserId(userId);
        if (byTargetUser > 0) {
            return byTargetUser;
        }
        return repo.countByType(USER_TYPE_PREFIX + userId);
    }

    public boolean deleteForUser(Long userId, Long notificationId) {
        if (userId == null || notificationId == null) {
            return false;
        }

        var ownedByTargetUser = repo.findByIdAndTargetUserId(notificationId, userId);
        if (ownedByTargetUser.isPresent()) {
            repo.deleteById(notificationId);
            return true;
        }

        String type = USER_TYPE_PREFIX + userId;
        var ownedByType = repo.findByIdAndType(notificationId, type);
        if (ownedByType.isPresent()) {
            repo.deleteById(notificationId);
            return true;
        }

        String token = "[USER:" + userId + "]";
        var fallback = repo.findById(notificationId)
                .filter(log -> log.getMessage() != null && log.getMessage().contains(token));
        if (fallback.isPresent()) {
            repo.deleteById(notificationId);
            return true;
        }

        return false;
    }

    public boolean deleteRecent(Long notificationId) {
        if (notificationId == null) {
            return false;
        }

        var adminLog = repo.findByIdAndType(notificationId, ADMIN_TYPE);
        if (adminLog.isPresent()) {
            repo.deleteById(notificationId);
            return true;
        }

        return false;
    }
}
