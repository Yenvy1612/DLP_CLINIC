package com.acare.backend.service;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.acare.backend.config.properties.RuleDefinitionProperties;
import com.acare.backend.entity.enums.RiskLevel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rule Engine: kiểm tra hành vi bất thường dựa trên LUẬT TĨNH (Rule-Based).
 *
 * Thay vì dùng AI/Machine Learning (phức tạp, tốn tài nguyên), hệ thống dùng
 * các quy tắc đơn giản nhưng hiệu quả để phát hiện bất thường.
 *
 * BA QUY TẮC CHÍNH:
 *
 * 1. TIME-BASED: Chặn truy cập ngoài giờ làm việc
 *    - VD: Bác sĩ tải dữ liệu lúc 2:00 AM → bất thường → chặn
 *
 * 2. RATE-LIMIT: Giới hạn số request trong cửa sổ thời gian
 *    - VD: User gửi 100 request trong 1 phút → spam/bot → chặn
 *    - Dùng Redis để đếm (key expire sau N giây)
 *
 * 3. VOLUME-BASED: Giới hạn số lần download/export
 *    - VD: User tải 50 file trong 1 phút → bất thường → chặn
 *    - Dùng Redis để đếm (key expire sau N giây)
 *
 * Redis được dùng thay vì ConcurrentHashMap vì:
 * - Persist qua restart
 * - Hỗ trợ TTL (Time-To-Live) tự động expire
 * - Scale được khi chạy nhiều instance
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final RuleDefinitionProperties props;
    private final StringRedisTemplate redisTemplate;

    // ==================== KEY PREFIX cho Redis ====================
    // Mỗi loại counter có prefix riêng để dễ quản lý

    /** Prefix cho rate limit counter: dlp:rate:{userId} */
    private static final String RATE_KEY_PREFIX = "dlp:rate:";

    /** Prefix cho volume (download) counter: dlp:volume:{userId} */
    private static final String VOLUME_KEY_PREFIX = "dlp:volume:";

    /** Prefix cho violation counter: dlp:violations:{userId} */
    private static final String VIOLATION_KEY_PREFIX = "dlp:violations:";

    // ==================== QUY TẮC 1: KIỂM TRA GIỜ LÀM VIỆC ====================

    /**
     * Kiểm tra xem thời điểm hiện tại có trong giờ làm việc không.
     *
     * @return true nếu đang trong giờ làm việc (hợp lệ), false nếu ngoài giờ
     *
     * VD: Nếu config starttime=7, endtime=22:
     *   - 8:00 AM  → true  (trong giờ)
     *   - 11:00 PM → false (ngoài giờ)
     *   - 2:00 AM  → false (ngoài giờ)
     */
    public boolean isWorkingHour() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(props.getTimeBasedStarttime(), 0);
        LocalTime end = LocalTime.of(props.getTimeBasedEndtime(), 0);
        return now.isAfter(start) && now.isBefore(end);
    }

    // ==================== QUY TẮC 2: RATE LIMIT (giới hạn request/phút) ====================

    /**
     * Kiểm tra user có đang spam request không.
     *
     * Logic dùng Redis:
     * 1. Key = "dlp:rate:{userId}", TTL = requestWindowSeconds giây
     * 2. Mỗi request → INCREMENT key
     * 3. Nếu count > requestLimit → spam detected
     *
     * Redis tự động xóa key sau TTL → counter tự reset mỗi window
     *
     * @param userId ID user cần kiểm tra
     * @return true nếu user đang spam (vi phạm), false nếu bình thường
     */
    public boolean isRateLimitExceeded(Long userId) {
        try {
            String key = RATE_KEY_PREFIX + userId;
            Long count = redisTemplate.opsForValue().increment(key);

            // Lần đầu tiên (count == 1) → set TTL cho key
            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(props.getRequestWindowSeconds()));
            }

            boolean exceeded = count != null && count > props.getRequestLimit();
            if (exceeded) {
                log.warn("[Rule Engine] Rate limit exceeded: userId={}, count={}/{} trong {}s",
                        userId, count, props.getRequestLimit(), props.getRequestWindowSeconds());
            }
            return exceeded;
        } catch (Exception e) {
            // Redis lỗi → cho qua (không block user vì lỗi infra)
            log.error("[Rule Engine] Redis error khi check rate limit: {}", e.getMessage());
            return false;
        }
    }

    // ==================== QUY TẮC 3: VOLUME LIMIT (giới hạn download) ====================

    /**
     * Kiểm tra user có vượt giới hạn download/export không.
     *
     * Tương tự rate limit nhưng dùng cho hành vi download cụ thể.
     * Counter tăng mỗi lần user gọi API export/download.
     *
     * @param userId ID user cần kiểm tra
     * @return true nếu vượt giới hạn (vi phạm), false nếu còn quota
     */
    public boolean isVolumeExceeded(Long userId) {
        try {
            String key = VOLUME_KEY_PREFIX + userId;
            Long count = redisTemplate.opsForValue().increment(key);

            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(props.getRequestWindowSeconds()));
            }

            boolean exceeded = count != null && count > props.getVolumeBased();
            if (exceeded) {
                log.warn("[Rule Engine] Volume limit exceeded: userId={}, count={}/{}",
                        userId, count, props.getVolumeBased());
            }
            return exceeded;
        } catch (Exception e) {
            log.error("[Rule Engine] Redis error khi check volume limit: {}", e.getMessage());
            return false;
        }
    }

    // ==================== ĐẾM VI PHẠM ĐỂ AUTO-BLOCK ====================

    /**
     * Tăng bộ đếm vi phạm của user.
     * Gọi mỗi khi DlpFilter phát hiện vi phạm.
     *
     * @param userId ID user vi phạm
     * @return số lần vi phạm hiện tại (sau khi tăng)
     */
    public long incrementViolationCount(Long userId) {
        try {
            String key = VIOLATION_KEY_PREFIX + userId;
            Long count = redisTemplate.opsForValue().increment(key);
            // Violation counter không expire (persist cho đến khi admin reset)
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("[Rule Engine] Redis error khi increment violation count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Kiểm tra user có nên bị auto-block không (dựa trên số vi phạm).
     *
     * @param userId ID user cần kiểm tra
     * @return true nếu số vi phạm >= ngưỡng autoBlockThreshold
     */
    public boolean shouldAutoBlock(Long userId) {
        try {
            String key = VIOLATION_KEY_PREFIX + userId;
            String countStr = redisTemplate.opsForValue().get(key);
            if (countStr == null) return false;
            return Long.parseLong(countStr) >= props.getAutoBlockThreshold();
        } catch (Exception e) {
            log.error("[Rule Engine] Redis error khi check auto-block: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Reset bộ đếm vi phạm của user (khi admin unblock).
     */
    public void resetViolationCount(Long userId) {
        try {
            redisTemplate.delete(VIOLATION_KEY_PREFIX + userId);
        } catch (Exception e) {
            log.error("[Rule Engine] Redis error khi reset violation count: {}", e.getMessage());
        }
    }

    // ==================== ĐÁNH GIÁ RỦI RO TỔNG HỢP ====================

    /**
     * Đánh giá mức rủi ro của 1 request dựa trên nhiều yếu tố.
     *
     * @param userId ID user
     * @param action loại hành vi (DOWNLOAD, EXPORT, VIEW, ...)
     * @return mức rủi ro: LOW, MEDIUM, HIGH, CRITICAL
     */
    public RiskLevel evaluateRisk(Long userId, String action) {
        int score = 0;

        // Ngoài giờ làm việc → +30 điểm
        if (!isWorkingHour()) {
            score += 30;
        }

        // Rate limit exceeded → +40 điểm
        if (isRateLimitExceeded(userId)) {
            score += 40;
        }

        // Volume exceeded → +50 điểm
        if ("DOWNLOAD".equals(action) || "EXPORT".equals(action)) {
            if (isVolumeExceeded(userId)) {
                score += 50;
            }
        }

        // Chuyển điểm → mức rủi ro
        if (score >= 70) return RiskLevel.CRITICAL;
        if (score >= 40) return RiskLevel.HIGH;
        if (score >= 20) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
