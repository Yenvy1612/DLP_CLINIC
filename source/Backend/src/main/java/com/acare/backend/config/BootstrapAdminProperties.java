package com.acare.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.bootstrap.admin")
public class BootstrapAdminProperties {
    private boolean enabled = false;
    private String fullName;
    private String email;
    private String phone;
    private String idNumber;
    private String address;
    private String password;
}
