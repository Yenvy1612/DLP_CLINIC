-- V6: Security Events table for behavioral anomaly detection
-- Hoàn toàn độc lập với dlp_logs (DLP) và agents (Agent tracking)

CREATE TABLE security_events (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    event_type      VARCHAR(60)  NOT NULL,
    severity        VARCHAR(20)  NOT NULL,
    ip_address      VARCHAR(60),
    request_uri     VARCHAR(300),
    http_method     VARCHAR(10),
    description     VARCHAR(1000),
    risk_score      INT,
    action_taken    VARCHAR(30),
    occurred_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT ck_security_events_severity
        CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT ck_security_events_action
        CHECK (action_taken IN ('LOGGED', 'WARNED', 'BLOCKED', 'TOKEN_REVOKED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sec_events_user     ON security_events(user_id);
CREATE INDEX idx_sec_events_ip       ON security_events(ip_address);
CREATE INDEX idx_sec_events_type     ON security_events(event_type);
CREATE INDEX idx_sec_events_severity ON security_events(severity);
CREATE INDEX idx_sec_events_time     ON security_events(occurred_at);
