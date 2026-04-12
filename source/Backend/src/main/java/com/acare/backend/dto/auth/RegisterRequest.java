package com.acare.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Ho ten khong duoc de trong")
    private String fullName;
    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;
    private String phone;
    @NotBlank(message = "Mat khau khong duoc de trong")
    @JsonAlias({"passwordHash"})
    private String password;
    @NotBlank(message = "Xac nhan mat khau khong duoc de trong")
    private String confirmPassword;
    private String gender;
    private String birthDate;
    private String address;
    private String idNumber;
}
