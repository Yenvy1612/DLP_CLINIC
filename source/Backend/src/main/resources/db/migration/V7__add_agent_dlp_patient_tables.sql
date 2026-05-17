-- V7: Add missing tables for Agent system, DLP logging, and Patient entity
-- Generated to fix Schema-validation errors on startup

-- ─────────────────────────────────────────────────────────────────────────────
-- Table: agents (Agent entity — Android/Windows device tracking)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS agents (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    device_id       VARCHAR(255)    NOT NULL UNIQUE,
    platform        VARCHAR(50)     NOT NULL COMMENT 'ANDROID / WINDOWS',
    hostname        VARCHAR(255)    NULL     COMMENT 'Windows hostname',
    device_name     VARCHAR(255)    NULL     COMMENT 'Android device name',
    os_version      VARCHAR(100)    NULL,
    agent_version   VARCHAR(50)     NULL,
    app_version     VARCHAR(50)     NULL,
    ip_address      VARCHAR(60)     NULL,
    trusted         BOOLEAN         NULL     DEFAULT FALSE,
    status          VARCHAR(20)     NULL     COMMENT 'ONLINE / OFFLINE / BLOCKED',
    registered_at   DATETIME        NULL,
    last_heartbeat  DATETIME        NULL,
    username        VARCHAR(255)    NULL,
    user_id         BIGINT          NULL,

    PRIMARY KEY (id),
    INDEX idx_agents_device_id  (device_id),
    INDEX idx_agents_user_id    (user_id),
    INDEX idx_agents_status     (status),

    CONSTRAINT fk_agents_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────────────────────────────────────
-- Table: dlp_logs (DlpLog entity — Data Loss Prevention events)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS dlp_logs (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    device_id        VARCHAR(255)    NULL,
    source_type      VARCHAR(50)     NULL     COMMENT 'WEB / ANDROID_AGENT / WINDOWS_AGENT',
    platform         VARCHAR(30)     NULL     COMMENT 'ANDROID / WINDOWS / WEB',
    event_type       VARCHAR(100)    NULL     COMMENT 'FORM_DLP_MATCHED / COPY_PATIENT_DATA / EXPORT_BLOCKED',
    action           VARCHAR(50)     NULL     COMMENT 'COPY / EXPORT / SUBMIT_FORM / VIEW',
    violation_type   VARCHAR(100)    NULL     COMMENT 'CCCD / PHONE / EMAIL / KEYWORD:HIV',
    severity         VARCHAR(20)     NULL     COMMENT 'LOW / MEDIUM / HIGH / CRITICAL',
    details          TEXT            NULL,
    user_id          BIGINT          NULL,
    content_snippet  VARCHAR(500)    NULL,
    timestamp        DATETIME        NULL,

    PRIMARY KEY (id),
    INDEX idx_dlp_logs_device_id    (device_id),
    INDEX idx_dlp_logs_user_id      (user_id),
    INDEX idx_dlp_logs_event_type   (event_type),
    INDEX idx_dlp_logs_timestamp    (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────────────────────────────────────
-- Table: patients (Patient entity — standalone patient record with AES encryption)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS patients (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    full_name        VARCHAR(255)    NULL,
    cccd             TEXT            NULL     COMMENT 'AES encrypted CCCD/CMND',
    phone            VARCHAR(20)     NULL,
    email            VARCHAR(255)    NULL,
    medical_history  TEXT            NULL,
    dob              DATE            NULL,

    PRIMARY KEY (id),
    INDEX idx_patients_phone    (phone),
    INDEX idx_patients_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
