package com.acare.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.repository.DlpLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ liên quan đến DLP Log.
 *
 * Được sử dụng bởi:
 * - DlpFilter: ghi log mỗi khi phát hiện vi phạm
 * - DlpController: cung cấp API cho admin xem log + thống kê
 */
@Service
@RequiredArgsConstructor
public class DlpLogService {

    private final DlpLogRepository dlpLogRepository;

    // ==================== GHI LOG ====================

    /**
     * Lưu 1 record vi phạm DLP vào database.
     * Được gọi từ DlpFilter mỗi khi phát hiện nội dung nhạy cảm
     * hoặc hành vi bất thường (rate limit, off-hours).
     */
    public DlpLog save(DlpLog dlpLog) {
        return dlpLogRepository.save(dlpLog);
    }

    // ==================== TRUY VẤN CÓ PHÂN TRANG ====================

    /** Lấy tất cả log, mới nhất trước, có phân trang (dùng cho bảng danh sách) */
    public Page<DlpLog> findAll(Pageable pageable) {
        return dlpLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /** Lấy log theo user cụ thể (khi admin click vào 1 user để xem chi tiết) */
    public Page<DlpLog> findByUserId(Long userId, Pageable pageable) {
        return dlpLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /** Lấy log theo mức rủi ro (VD: filter "chỉ xem CRITICAL") */
    public Page<DlpLog> findByRiskLevel(RiskLevel riskLevel, Pageable pageable) {
        return dlpLogRepository.findByRiskLevelOrderByCreatedAtDesc(riskLevel, pageable);
    }

    /** Lấy log theo loại vi phạm (VD: filter "chỉ xem CCCD_DETECTED") */
    public Page<DlpLog> findByViolationType(String violationType, Pageable pageable) {
        return dlpLogRepository.findByViolationTypeOrderByCreatedAtDesc(violationType, pageable);
    }

    /** Lấy log trong khoảng ngày (dùng cho date range picker trên dashboard) */
    public Page<DlpLog> findByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        LocalDateTime fromDT = from.atStartOfDay();               // 00:00:00 ngày bắt đầu
        LocalDateTime toDT = to.atTime(LocalTime.MAX);             // 23:59:59 ngày kết thúc
        return dlpLogRepository.findByDateRange(fromDT, toDT, pageable);
    }

    // ==================== THỐNG KÊ (cho Dashboard cards + biểu đồ) ====================

    /**
     * Trả về object thống kê tổng hợp cho DLP Dashboard.
     * Bao gồm: tổng vi phạm, số bị block, phân bổ theo riskLevel và violationType.
     *
     * @param from ngày bắt đầu thống kê
     * @param to   ngày kết thúc thống kê
     * @return Map chứa các số liệu thống kê
     */
    public Map<String, Object> getStats(LocalDate from, LocalDate to) {
        LocalDateTime fromDT = from.atStartOfDay();
        LocalDateTime toDT = to.atTime(LocalTime.MAX);

        Map<String, Object> stats = new HashMap<>();

        // Tổng số vi phạm trong khoảng thời gian
        stats.put("totalViolations", dlpLogRepository.countByDateRange(fromDT, toDT));

        // Số vi phạm bị block (toàn bộ, không phân biệt thời gian)
        stats.put("totalBlocked", dlpLogRepository.countByBlockedTrue());

        // Số vi phạm theo từng mức rủi ro (dùng cho card trên dashboard)
        Map<String, Long> byRiskLevel = new HashMap<>();
        for (RiskLevel level : RiskLevel.values()) {
            byRiskLevel.put(level.name(), dlpLogRepository.countByRiskLevel(level));
        }
        stats.put("byRiskLevel", byRiskLevel);

        // Phân bổ theo loại vi phạm (dùng cho biểu đồ tròn)
        List<Object[]> byType = dlpLogRepository.countGroupByViolationType(fromDT, toDT);
        Map<String, Long> violationTypeMap = new HashMap<>();
        for (Object[] row : byType) {
            violationTypeMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("byViolationType", violationTypeMap);

        return stats;
    }

    /** Đếm nhanh số vi phạm theo mức rủi ro (dùng cho badge/notification) */
    public long countByRiskLevel(RiskLevel riskLevel) {
        return dlpLogRepository.countByRiskLevel(riskLevel);
    }
}
