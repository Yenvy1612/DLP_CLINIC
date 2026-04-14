package com.acare.backend.dto.user;

import com.acare.backend.entity.DoctorProfile;
import com.acare.backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPublicResponse {
    private Long id;
    private String fullName;
    private String specialty;
    private Integer yearsExperience;
    private String clinicLocation;

    public static DoctorPublicResponse from(User user, DoctorProfile profile) {
        String specialty = profile != null && profile.getSpecialty() != null && !profile.getSpecialty().isBlank()
                ? profile.getSpecialty()
                : "Đang cập nhật";

        Integer years = profile != null && profile.getYearsExperience() != null
                ? Math.max(0, profile.getYearsExperience())
                : 0;

        String clinicLocation = profile != null ? profile.getClinicLocation() : "";

        return DoctorPublicResponse.builder()
                .id(user != null ? user.getId() : null)
                .fullName(user != null ? user.getFullName() : null)
                .specialty(specialty)
                .yearsExperience(years)
                .clinicLocation(clinicLocation)
                .build();
    }
}