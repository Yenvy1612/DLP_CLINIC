package com.acare.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Cấu hình Redis cho module DLP.
 *
 * Redis được dùng trong RuleEngineService để:
 * 1. Đếm số request của mỗi user trong cửa sổ thời gian (rate limiting)
 * 2. Đếm số lần download/export (volume limiting)
 * 3. Đếm số vi phạm để auto-block user
 *
 * Spring Boot tự động cấu hình RedisConnectionFactory từ properties:
 *   spring.data.redis.host=localhost
 *   spring.data.redis.port=6379
 *
 * Class này chỉ tạo StringRedisTemplate bean (dùng String key + String value).
 *
 * Lưu ý: Nếu Redis server không khả dụng, RuleEngineService sẽ fallback
 * sang ConcurrentHashMap (in-memory) để đảm bảo hệ thống vẫn hoạt động.
 */
@Configuration
public class RedisConfig {

    /**
     * StringRedisTemplate: template đơn giản, key và value đều là String.
     * Dùng cho rate limiting (key = "dlp:rate:{userId}", value = số đếm).
     *
     * Spring Boot đã auto-configure RedisConnectionFactory,
     * nên chỉ cần inject nó vào đây.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
