package com.acare.backend.dto.auth;

import java.time.LocalDate;

import com.acare.backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String idNumber;
    private Boolean enabled;

    public static UserProfileResponse from(User user) {
        return user == null ? null : user.toProfileResponse();
    }
}
