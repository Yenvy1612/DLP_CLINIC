package com.acare.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Cookie cookie = new Cookie();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMinutes = 120;
        private long refreshExpirationDays = 14;
    }

    @Data
    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";
    }

    @Data
    public static class Cookie {
        private String accessTokenName = "access_token";
        private String refreshTokenName = "refresh_token";
        private String path = "/";
        private String sameSite = "Lax";
        private boolean secure = false;
        private String domain;
    }
}
