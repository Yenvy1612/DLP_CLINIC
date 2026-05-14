package com.acare.backend.entity;

import java.time.LocalDateTime;

import com.acare.backend.entity.enums.RiskLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ghi nhận mỗi vi phạm DLP (Data Loss Prevention) trong hệ thống.
 *
 * Mỗi khi DlpFilter hoặc RuleEngine phát hiện hành vi bất thường,
 * một record DlpLog sẽ được tạo và lưu vào bảng dlp_logs.
 * Admin có thể xem danh sách này qua DLP Dashboard.
 */
@Entity
@Getter
@Setter
@Table(name = "dlp_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DlpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    // ==================== THÔNG TIN USER ====================

    /** ID user thực hiện request (null nếu user chưa đăng nhập) */
    @Column(name = "user_id")
    private Long userId;

    /** Email/tên user - lưu trực tiếp để query mà không cần JOIN bảng users */
    @Column(name = "username", length = 150)
    private String username;

    // ==================== THÔNG TIN HÀNH VI ====================

    /**
     * Loại hành vi bị phát hiện:
     * - INPUT_SCAN:  Dữ liệu nhạy cảm trong request body (POST/PUT)
     * - OUTPUT_SCAN: Dữ liệu nhạy cảm trong response body (GET)
     * - RATE_LIMIT:  Vượt giới hạn số request
     * - OFF_HOURS:   Truy cập ngoài giờ làm việc
     */
    @Column(name = "action", length = 50, nullable = false)
    private String action;

    /** API endpoint bị vi phạm, VD: "/api/patients/export" */
    @Column(name = "endpoint", length = 255)
    private String endpoint;

    /** HTTP method: GET, POST, PUT, DELETE */
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    // ==================== NỘI DUNG VI PHẠM ====================

    /** 200 ký tự đầu của nội dung vi phạm (để admin review mà không lộ quá nhiều) */
    @Column(name = "content_snippet", columnDefinition = "TEXT")
    private String contentSnippet;

    /**
     * Loại vi phạm cụ thể:
     * - CCCD_DETECTED:    Phát hiện số CCCD (12 chữ số, bắt đầu bằng 0)
     * - PHONE_DETECTED:   Phát hiện số điện thoại Việt Nam
     * - EMAIL_DETECTED:   Phát hiện email
     * - SENSITIVE_WORD:   Phát hiện từ khóa y tế nhạy cảm (HIV, Ung thư...)
     * - RATE_LIMIT:       Vượt giới hạn request/download
     * - OFF_HOURS:        Truy cập ngoài giờ làm việc
     */
    @Column(name = "violation_type", length = 80)
    private String violationType;

    // ==================== ĐÁNH GIÁ RỦI RO ====================

    /** Mức rủi ro: LOW, MEDIUM, HIGH, CRITICAL (xem enum RiskLevel) */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    @Builder.Default
    private RiskLevel riskLevel = RiskLevel.LOW;

    /** Request có bị chặn (block) không? true = đã chặn, false = chỉ log */
    @Column(name = "blocked")
    @Builder.Default
    private Boolean blocked = false;

    // ==================== THÔNG TIN KỸ THUẬT ====================

    /** Địa chỉ IP của client (hỗ trợ cả IPv4 lẫn IPv6, max 45 ký tự) */
    @Column(name = "client_ip", length = 45)
    private String clientIp;

    /** User-Agent header từ browser/client */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // ==================== TIMESTAMP ====================

    /** Thời điểm vi phạm xảy ra */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}