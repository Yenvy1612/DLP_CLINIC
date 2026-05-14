package com.acare.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Cấu hình async execution dùng cho AnomalyDetectionService.recordAsync().
 * Đảm bảo ghi log bảo mật không block luồng xử lý request chính.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "securityEventExecutor")
    public Executor securityEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("security-event-");
        executor.initialize();
        return executor;
    }
}
