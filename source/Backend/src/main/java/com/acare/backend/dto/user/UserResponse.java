package com.acare.backend.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.Gender;
import com.acare.backend.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String gender;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    private String address;
    private String idNumber;
    private Boolean enabled;

    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }

        UserRole userRole = user.getRole();
        Gender userGender = user.getGender();

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(userRole != null ? userRole.name() : null)
                .gender(userGender != null ? userGender.name() : null)
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .idNumber(user.getIdNumber())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
