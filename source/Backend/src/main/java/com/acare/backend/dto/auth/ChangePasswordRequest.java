package com.acare.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Khong duoc de trong currentPassword")
    private String currentPassword;

    @NotBlank(message = "Khong duoc de trong newPassword")
    @Size(min = 8, message = "newPassword phai co it nhat 8 ky tu")
    private String newPassword;

    @NotBlank(message = "Khong duoc de trong confirmPassword")
    private String confirmPassword;
}
