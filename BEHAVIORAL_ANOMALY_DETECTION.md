# 🔐 Behavioral Anomaly Detection — Tài liệu kỹ thuật

## 1. Tổng quan

Module **Behavioral Anomaly Detection** phát hiện và ngăn chặn các hành vi bất thường tại tầng HTTP request của backend. Hệ thống hoạt động **hoàn toàn độc lập** với hai module khác trong dự án:

| Module | Phụ trách | Tầng hoạt động |
|---|---|---|
| **Agent** (đồng nghiệp 1) | Đăng ký & tracking thiết bị vật lý | Device / DB |
| **DLP Logs** (đồng nghiệp 2) | Phát hiện dữ liệu nhạy cảm trong nội dung | AOP Method |
| **Behavioral Anomaly Detection** (module này) | Phân tích hành vi HTTP request | HTTP Filter / DB |

---

## 2. Kiến trúc

```
Mobile APK
    │
    │  HTTP Request
    ▼
┌─────────────────────────────────────────────┐
│  Spring Security Filter Chain               │
│                                             │
│  1. JWT Auth Filter (có sẵn)               │
│  2. BehaviorTrackingFilter  ← module này   │
│       │                                     │
│       ├─ evaluateRequest()                  │
│       │     └─ AnomalyDetectionService      │
│       │           ├─ Rate limit check       │
│       │           ├─ IP change check        │
│       │           └─ Ghi SecurityEvent DB   │
│       │                                     │
│       ├─ risk < 70  → tiếp tục request     │
│       └─ risk ≥ 70  → 429 + block          │
│           risk ≥ 90 → revoke JWT token     │
└─────────────────────────────────────────────┘
    │
    ▼ (request bình thường tiếp tục)
Controller → Service → DB
```

---

## 3. Các file được tạo mới

```
Backend/src/main/java/com/acare/backend/
├── entity/
│   └── SecurityEvent.java              ← Entity lưu sự kiện bất thường
├── repository/
│   └── SecurityEventRepository.java    ← Query theo user/IP/type/severity
├── service/
│   └── AnomalyDetectionService.java    ← Logic phân tích & ghi log
├── filter/
│   └── BehaviorTrackingFilter.java     ← HTTP Filter, chạy mọi request
├── controller/
│   └── SecurityEventController.java    ← Admin API xem dashboard
├── dto/security/
│   ├── SecurityEventResponse.java      ← Response DTO
│   └── SecurityDashboardResponse.java  ← Dashboard DTO
└── config/
    └── AsyncConfig.java                ← Thread pool cho async logging

Backend/src/main/resources/db/migration/
└── V6__add_security_events_table.sql   ← Flyway migration tạo bảng
```

---

## 4. Các hành vi bất thường được phát hiện

| Event Type | Mô tả | Severity | Ngưỡng |
|---|---|---|---|
| `ENDPOINT_RATE_LIMIT` | Quá nhiều request đến cùng endpoint | HIGH | > 30 req/phút |
| `IP_RATE_LIMIT` | Quá nhiều request từ cùng IP | MEDIUM | > 60 req/phút |
| `IP_CHANGE_DETECTED` | IP thay đổi trong session | MEDIUM | Bất kỳ thay đổi |
| `LOGIN_FAILURE` | Đăng nhập thất bại | MEDIUM → CRITICAL | > 5 lần/10 phút |
| `SUSPICIOUS_LOGIN_SUCCESS` | Đăng nhập thành công sau nhiều lần thất bại | HIGH | ≥ 3 lần thất bại trước |
| `OFF_HOURS_ACCESS` | Truy cập ngoài giờ làm việc | MEDIUM | Ngoài giờ config |
| `SESSION_REVOKED` | Session bị thu hồi tự động | CRITICAL | Risk score ≥ 90 |

---

## 5. Risk Score & Hành động tự động

```
Risk Score    Hành động
──────────    ─────────────────────────────────────────
0  – 69       LOGGED       — Ghi log, request đi qua bình thường
70 – 89       BLOCKED      — Trả về HTTP 429, request bị từ chối
90 – 100      TOKEN_REVOKED— Thu hồi JWT + block request
```

---

## 6. API Endpoints (Admin only)

Tất cả endpoint yêu cầu **role ADMIN** và JWT token hợp lệ.

### `GET /api/security/events`
Lấy danh sách sự kiện bảo mật, phân trang.

**Query params:**

| Param | Mặc định | Mô tả |
|---|---|---|
| `page` | 0 | Số trang |
| `size` | 20 | Số item/trang |
| `severity` | (tất cả) | Filter theo: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |

**Response mẫu:**
```json
{
  "status": 200,
  "success": true,
  "message": "Security events",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 42,
        "eventType": "LOGIN_FAILURE",
        "severity": "MEDIUM",
        "ipAddress": "192.168.1.10",
        "requestUri": "/api/auth/login",
        "httpMethod": "POST",
        "description": "Đăng nhập thất bại: tài khoản 'admin@clinic.com', lần thứ 3",
        "riskScore": 40,
        "actionTaken": "LOGGED",
        "occurredAt": "2026-05-14T09:30:00"
      }
    ],
    "totalElements": 150,
    "totalPages": 8
  }
}
```

### `GET /api/security/events/user/{userId}`
Lấy sự kiện bảo mật của một user cụ thể.

### `GET /api/security/dashboard`
Tổng quan bảo mật trong 24 giờ qua.

**Response mẫu:**
```json
{
  "status": 200,
  "success": true,
  "message": "Security dashboard",
  "data": {
    "totalEvents24h": 234,
    "criticalEvents24h": 3,
    "highEvents24h": 12,
    "revokedSessions24h": 2,
    "topEventTypes": {
      "LOGIN_FAILURE": 45,
      "ENDPOINT_RATE_LIMIT": 23,
      "IP_CHANGE_DETECTED": 8
    },
    "recentCriticalEvents": [...]
  }
}
```

---

## 7. Cấu hình ngưỡng

Mở `AnomalyDetectionService.java`, chỉnh các hằng số:

```java
private static final int RATE_LIMIT_PER_MINUTE = 30;    // request/phút/endpoint
private static final int IP_RATE_LIMIT_PER_MINUTE = 60; // request/phút/IP
private static final int MAX_LOGIN_FAILURES = 5;          // lần thất bại/10 phút
```

---

## 8. Schema DB (`security_events`)

```sql
CREATE TABLE security_events (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT,             -- null nếu chưa xác thực
    event_type   VARCHAR(60),        -- loại sự kiện
    severity     VARCHAR(20),        -- LOW/MEDIUM/HIGH/CRITICAL
    ip_address   VARCHAR(60),
    request_uri  VARCHAR(300),
    http_method  VARCHAR(10),
    description  VARCHAR(1000),
    risk_score   INT,                -- 0–100
    action_taken VARCHAR(30),        -- LOGGED/BLOCKED/TOKEN_REVOKED
    occurred_at  DATETIME(6)
);
```

> **Không có FK đến `users`** — event vẫn được lưu kể cả khi user không tồn tại (request chưa auth).

---

## 9. Sự độc lập với các module khác

### Không xung đột với Agent (đồng nghiệp 1)
- Agent: `AgentRepository`, `AgentService`, `/api/agents/**`, bảng `agents`
- Module này: `SecurityEventRepository`, `AnomalyDetectionService`, `/api/security/**`, bảng `security_events`
- **Không chia sẻ bean hoặc bảng DB nào**

### Không xung đột với DLP Logs (đồng nghiệp 2)
- DLP: `BehaviorAspect` (**AOP**), `RuleEngineService`, `DlpLog`, bảng `dlp_logs`
- Module này: `BehaviorTrackingFilter` (**HTTP Servlet Filter**), `SecurityEvent`, bảng `security_events`
- **Hai tầng khác nhau hoàn toàn**: AOP method-level ≠ HTTP servlet-level

---

## 10. Luồng xử lý trên Mobile APK

```
1. User mở app → APK gửi request HTTP về backend
2. BehaviorTrackingFilter nhận request (chạy sau JWT filter)
3. Tính risk score dựa trên lịch sử từ DB security_events
4. Score < 70  → request đi tiếp bình thường
5. Score 70–89 → ghi SecurityEvent + trả HTTP 429 về APK
6. Score ≥ 90  → thu hồi JWT token + ghi CRITICAL event + trả 429
7. Admin xem báo cáo tại GET /api/security/dashboard
```
