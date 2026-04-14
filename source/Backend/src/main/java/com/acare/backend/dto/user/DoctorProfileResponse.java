package com.acare.backend.dto.user;

import java.time.LocalTime;

import com.acare.backend.entity.DoctorProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfileResponse {
    private Long specialtyId;
    private String specialty;
    private Integer yearsExperience;
    private String clinicLocation;
    private String workingDays;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    public static DoctorProfileResponse from(DoctorProfile profile) {
        if (profile == null) {
            return null;
        }

        return DoctorProfileResponse.builder()
            .specialtyId(profile.getSpecialtyId())
            .specialty(profile.getSpecialty())
            .yearsExperience(profile.getYearsExperience())
            .clinicLocation(profile.getClinicLocation())
            .workingDays(profile.getWorkingDays())
            .shiftStart(profile.getShiftStart())
            .shiftEnd(profile.getShiftEnd())
            .build();
    }
}