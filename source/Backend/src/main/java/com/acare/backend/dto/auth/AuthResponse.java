package com.acare.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String username;
    private String[] roles;
    private String originalRole;
    private Long id;
    private String tokenType;
    private long expiresInSeconds;
}
