package com.acare.backend.dto.appointment.statistics;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorStatisticsDashboardResponse {
    private String periodType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private long uniquePatientCount;
    private long doneAppointmentCount;
    private List<DoctorDailyVisitPointResponse> dailyVisits;
    private List<DoctorPatientSummaryRowResponse> patientRows;
}
