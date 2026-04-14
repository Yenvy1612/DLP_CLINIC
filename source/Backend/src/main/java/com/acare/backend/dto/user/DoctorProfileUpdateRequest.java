package com.acare.backend.dto.user;

import lombok.Data;

@Data
public class DoctorProfileUpdateRequest {
    private Long specialtyId;
    private Integer yearsExperience;
    private String clinicLocation;
    private String workingDays;
    private String shiftStart;
    private String shiftEnd;
}