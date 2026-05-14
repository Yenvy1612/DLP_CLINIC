-- V6: Tạo bảng dlp_logs để ghi nhận các vi phạm DLP (Data Loss Prevention).
-- Mỗi khi Filter phát hiện dữ liệu nhạy cảm hoặc hành vi bất thường,
-- hệ thống sẽ ghi 1 record vào bảng này để admin theo dõi.

CREATE TABLE dlp_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Thông tin user thực hiện hành vi
    user_id        BIGINT,                                        -- FK tới bảng users
    username       VARCHAR(150),                                  -- Lưu email/tên để dễ query mà không cần JOIN

    -- Thông tin hành vi
    action         VARCHAR(50)   NOT NULL,                        -- Loại hành vi: INPUT_SCAN, OUTPUT_SCAN, DOWNLOAD, EXPORT
    endpoint       VARCHAR(255),                                  -- API endpoint bị vi phạm, VD: /api/patients/export
    http_method    VARCHAR(10),                                   -- GET, POST, PUT, DELETE

    -- Nội dung vi phạm
    content_snippet TEXT,                                         -- 200 ký tự đầu của nội dung vi phạm (để review)
    violation_type VARCHAR(80),                                   -- CCCD_DETECTED, PHONE_DETECTED, SENSITIVE_WORD, RATE_LIMIT, OFF_HOURS

    -- Mức độ rủi ro
    risk_level     VARCHAR(20)   DEFAULT 'LOW',                   -- LOW, MEDIUM, HIGH, CRITICAL

    -- Kết quả xử lý
    blocked        BOOLEAN       DEFAULT FALSE,                   -- Request có bị chặn không?

    -- Thông tin kỹ thuật
    client_ip      VARCHAR(45),                                   -- Địa chỉ IP client (hỗ trợ IPv6)
    user_agent     VARCHAR(500),                                  -- Browser/client user agent

    -- Timestamp
    created_at     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    -- Foreign key: SET NULL khi user bị xóa (giữ log vi phạm)
    CONSTRAINT fk_dlp_logs_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index để tối ưu query
CREATE INDEX idx_dlp_logs_user      ON dlp_logs(user_id);        -- Tìm vi phạm theo user
CREATE INDEX idx_dlp_logs_risk      ON dlp_logs(risk_level);     -- Filter theo mức rủi ro
CREATE INDEX idx_dlp_logs_created   ON dlp_logs(created_at);     -- Sắp xếp theo thời gian
CREATE INDEX idx_dlp_logs_violation ON dlp_logs(violation_type); -- Filter theo loại vi phạm
