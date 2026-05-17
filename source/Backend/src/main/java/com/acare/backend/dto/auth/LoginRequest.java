package com.acare.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Khong duoc de trong username")
    private String username;

    @NotBlank(message = "Khong duoc de trong password")
    private String password;
}
