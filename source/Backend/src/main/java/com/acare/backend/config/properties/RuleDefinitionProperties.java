package com.acare.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties cấu hình cho DLP Rule Engine.
 *
 * Đọc từ application.properties với prefix "rule-definition".
 * Admin có thể thay đổi giá trị này để điều chỉnh độ nhạy của hệ thống DLP
 * mà không cần sửa code.
 *
 * Ví dụ trong application.properties:
 *   rule-definition.time-based-starttime=7
 *   rule-definition.time-based-endtime=22
 *   rule-definition.volume-based=50
 *   rule-definition.request-limit=10
 *   rule-definition.request-window-seconds=60
 *   rule-definition.auto-block-threshold=5
 */
@Configuration
@ConfigurationProperties(prefix = "rule-definition")
@Getter
@Setter
public class RuleDefinitionProperties {

    /**
     * Giờ BẮT ĐẦU được phép truy cập (24h format).
     * Truy cập trước giờ này → vi phạm OFF_HOURS.
     * Mặc định: 7 (7:00 sáng)
     */
    private Integer timeBasedStarttime = 7;

    /**
     * Giờ KẾT THÚC được phép truy cập (24h format).
     * Truy cập sau giờ này → vi phạm OFF_HOURS.
     * Mặc định: 22 (10:00 tối)
     */
    private Integer timeBasedEndtime = 22;

    /**
     * Số lần download/export TỐI ĐA trong 1 cửa sổ thời gian.
     * Vượt quá → vi phạm RATE_LIMIT.
     * Mặc định: 50 lần
     */
    private Integer volumeBased = 50;

    /**
     * Số request TỐI ĐA trong 1 cửa sổ thời gian (requestWindowSeconds).
     * Dùng để phát hiện spam/bot.
     * Mặc định: 10 request
     */
    private Integer requestLimit = 10;

    /**
     * Kích thước cửa sổ thời gian (giây) để đếm request.
     * Redis key sẽ expire sau khoảng thời gian này.
     * Mặc định: 60 giây (1 phút)
     */
    private Integer requestWindowSeconds = 60;

    /**
     * Ngưỡng vi phạm để TỰ ĐỘNG khóa user.
     * Nếu 1 user vi phạm quá N lần → auto block.
     * Mặc định: 5 lần vi phạm
     */
    private Integer autoBlockThreshold = 5;
}
