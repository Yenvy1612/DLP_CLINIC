package com.acare.backend.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.service.DlpLogService;
import com.acare.backend.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller cho module DLP (Data Loss Prevention).
 *
 * Tất cả endpoint trong controller này YÊU CẦU role ADMIN.
 * (Được cấu hình trong SecurityConfig: .requestMatchers("/api/dlp/**").hasRole("ADMIN"))
 *
 * Chức năng:
 * 1. Xem danh sách DLP violations (phân trang, filter)
 * 2. Xem thống kê vi phạm (cho DLP Dashboard)
 * 3. Block/Unblock user vi phạm
 */
@RestController
@RequestMapping("/api/dlp")
@RequiredArgsConstructor
public class DlpController {

    private final DlpLogService dlpLogService;
    private final UserService userService;

    // ==================== DANH SÁCH DLP LOGS ====================

    /**
     * GET /api/dlp/logs
     * Lấy danh sách tất cả DLP violations, phân trang.
     *
     * Query params:
     * - page: số trang (mặc định 0)
     * - size: số record/trang (mặc định 20)
     *
     * Dùng cho bảng danh sách trên DLP Dashboard.
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<DlpLog>>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DlpLog> logs = dlpLogService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    /**
     * GET /api/dlp/logs/user/{userId}
     * Lấy danh sách violations của 1 user cụ thể.
     * Dùng khi admin click vào user để xem chi tiết vi phạm.
     */
    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<ApiResponse<Page<DlpLog>>> getLogsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DlpLog> logs = dlpLogService.findByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    /**
     * GET /api/dlp/logs/risk/{riskLevel}
     * Lấy violations theo mức rủi ro (LOW, MEDIUM, HIGH, CRITICAL).
     * Dùng cho filter dropdown trên dashboard.
     */
    @GetMapping("/logs/risk/{riskLevel}")
    public ResponseEntity<ApiResponse<Page<DlpLog>>> getLogsByRiskLevel(
            @PathVariable RiskLevel riskLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DlpLog> logs = dlpLogService.findByRiskLevel(riskLevel, pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    /**
     * GET /api/dlp/logs/type/{violationType}
     * Lấy violations theo loại (CCCD_DETECTED, SENSITIVE_WORD, ...).
     */
    @GetMapping("/logs/type/{violationType}")
    public ResponseEntity<ApiResponse<Page<DlpLog>>> getLogsByViolationType(
            @PathVariable String violationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DlpLog> logs = dlpLogService.findByViolationType(violationType, pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    // ==================== THỐNG KÊ (cho Dashboard) ====================

    /**
     * GET /api/dlp/logs/stats
     * Trả về thống kê tổng hợp cho DLP Dashboard.
     *
     * Query params:
     * - from: ngày bắt đầu (format: yyyy-MM-dd, mặc định 30 ngày trước)
     * - to:   ngày kết thúc (format: yyyy-MM-dd, mặc định hôm nay)
     *
     * Response chứa:
     * - totalViolations: tổng số vi phạm
     * - totalBlocked: số vi phạm bị chặn
     * - byRiskLevel: {LOW: 5, MEDIUM: 3, HIGH: 2, CRITICAL: 1}
     * - byViolationType: {CCCD_DETECTED: 4, SENSITIVE_WORD: 3, ...}
     */
    @GetMapping("/logs/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        // Mặc định: 30 ngày gần nhất
        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();

        Map<String, Object> stats = dlpLogService.getStats(from, to);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    // ==================== QUẢN LÝ USER (Block/Unblock) ====================

    /**
     * POST /api/dlp/users/{userId}/block
     * Khóa tài khoản user do vi phạm DLP.
     * Set user.enabled = false → user không thể đăng nhập.
     */
    @PostMapping("/users/{userId}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@PathVariable Long userId) {
        userService.toggleUserEnabled(userId, false);
        return ResponseEntity.ok(ApiResponse.ok("User " + userId + " đã bị khóa do vi phạm DLP."));
    }

    /**
     * POST /api/dlp/users/{userId}/unblock
     * Mở khóa tài khoản user.
     * Set user.enabled = true → user có thể đăng nhập lại.
     */
    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<ApiResponse<String>> unblockUser(@PathVariable Long userId) {
        userService.toggleUserEnabled(userId, true);
        return ResponseEntity.ok(ApiResponse.ok("User " + userId + " đã được mở khóa."));
    }
}
