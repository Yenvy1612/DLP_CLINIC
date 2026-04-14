package com.acare.backend.dto.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryResponse {
    private long userCount;
    private String topDoctorName;
    private long topDoctorDoneCount;
    private String topServiceName;
    private long topServiceDoneCount;
    private BigDecimal monthRevenue;
    private int month;
    private int year;
}
