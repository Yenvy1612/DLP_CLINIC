package com.acare.backend.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String gender;
    private String birthDate;
    private String address;
    private String idNumber;
    private Boolean enabled;
}
