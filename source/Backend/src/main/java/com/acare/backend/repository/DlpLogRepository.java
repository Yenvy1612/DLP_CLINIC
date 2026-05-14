package com.acare.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.enums.RiskLevel;

/**
 * Repository truy vấn bảng dlp_logs.
 *
 * Cung cấp các method phân trang, filter theo user/riskLevel/violationType,
 * và thống kê cho DLP Dashboard.
 */
public interface DlpLogRepository extends JpaRepository<DlpLog, Long> {

    // ==================== TÌM KIẾM THEO USER ====================

    /** Lấy danh sách vi phạm của 1 user cụ thể, phân trang, sắp xếp mới nhất trước */
    Page<DlpLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // ==================== FILTER THEO MỨC RỦI RO ====================

    /** Lấy tất cả vi phạm theo mức rủi ro (VD: chỉ xem CRITICAL) */
    Page<DlpLog> findByRiskLevelOrderByCreatedAtDesc(RiskLevel riskLevel, Pageable pageable);

    /** Đếm số vi phạm theo mức rủi ro (dùng cho dashboard card "Tổng CRITICAL: 5") */
    long countByRiskLevel(RiskLevel riskLevel);

    // ==================== FILTER THEO LOẠI VI PHẠM ====================

    /** Lấy vi phạm theo loại cụ thể (VD: CCCD_DETECTED) */
    Page<DlpLog> findByViolationTypeOrderByCreatedAtDesc(String violationType, Pageable pageable);

    // ==================== FILTER THEO KHOẢNG THỜI GIAN ====================

    /** Lấy vi phạm trong khoảng thời gian (dùng cho biểu đồ trend) */
    @Query("SELECT d FROM DlpLog d WHERE d.createdAt BETWEEN :from AND :to ORDER BY d.createdAt DESC")
    Page<DlpLog> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    /** Đếm vi phạm trong khoảng thời gian (dùng cho thống kê) */
    @Query("SELECT COUNT(d) FROM DlpLog d WHERE d.createdAt BETWEEN :from AND :to")
    long countByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // ==================== THỐNG KÊ TỔNG HỢP ====================

    /**
     * Đếm số vi phạm gom theo violationType.
     * Trả về danh sách [violationType, count] để vẽ biểu đồ tròn.
     * VD: [["CCCD_DETECTED", 15], ["SENSITIVE_WORD", 8], ...]
     */
    @Query("SELECT d.violationType, COUNT(d) FROM DlpLog d " +
            "WHERE d.createdAt BETWEEN :from AND :to " +
            "GROUP BY d.violationType ORDER BY COUNT(d) DESC")
    List<Object[]> countGroupByViolationType(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Đếm số vi phạm gom theo riskLevel.
     * Trả về danh sách [riskLevel, count] để vẽ biểu đồ cột.
     */
    @Query("SELECT d.riskLevel, COUNT(d) FROM DlpLog d " +
            "WHERE d.createdAt BETWEEN :from AND :to " +
            "GROUP BY d.riskLevel")
    List<Object[]> countGroupByRiskLevel(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /** Đếm số vi phạm bị block (blocked = true) */
    long countByBlockedTrue();

    /** Lấy tất cả log, phân trang, mới nhất trước */
    Page<DlpLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
