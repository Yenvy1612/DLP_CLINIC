package com.acare.backend.repository;

import com.acare.backend.entity.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {

    /** Đếm số sự kiện của một user trong khoảng thời gian */
    long countByUserIdAndEventTypeAndOccurredAtAfter(Long userId, String eventType, LocalDateTime since);

    /** Đếm số sự kiện theo IP + loại sự kiện trong khoảng thời gian */
    long countByIpAddressAndEventTypeAndOccurredAtAfter(String ipAddress, String eventType, LocalDateTime since);

    /** Đếm request từ IP trong khoảng thời gian */
    long countByIpAddressAndOccurredAtAfter(String ipAddress, LocalDateTime since);

    /** Đếm request đến một endpoint từ user trong khoảng thời gian */
    long countByUserIdAndRequestUriAndOccurredAtAfter(Long userId, String requestUri, LocalDateTime since);

    /** Lấy IP cuối cùng của một user */
    @Query("SELECT e.ipAddress FROM SecurityEvent e WHERE e.userId = :userId AND e.ipAddress IS NOT NULL ORDER BY e.occurredAt DESC LIMIT 1")
    String findLastKnownIp(@Param("userId") Long userId);

    /** Danh sách event theo user, mới nhất trước */
    Page<SecurityEvent> findByUserIdOrderByOccurredAtDesc(Long userId, Pageable pageable);

    /** Danh sách event theo severity */
    Page<SecurityEvent> findBySeverityOrderByOccurredAtDesc(String severity, Pageable pageable);

    /** Toàn bộ event, mới nhất trước */
    Page<SecurityEvent> findAllByOrderByOccurredAtDesc(Pageable pageable);

    /** Thống kê theo loại event trong 24h */
    @Query("SELECT e.eventType, COUNT(e) FROM SecurityEvent e WHERE e.occurredAt >= :since GROUP BY e.eventType ORDER BY COUNT(e) DESC")
    List<Object[]> countByEventTypeSince(@Param("since") LocalDateTime since);

    /** Event CRITICAL chưa xử lý gần đây */
    List<SecurityEvent> findBySeverityAndOccurredAtAfterOrderByOccurredAtDesc(
            String severity, LocalDateTime since);
}
