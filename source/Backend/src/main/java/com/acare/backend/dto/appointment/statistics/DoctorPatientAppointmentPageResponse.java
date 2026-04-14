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
public class DoctorPatientAppointmentPageResponse {
    private Long patientId;
    private String patientName;
    private String periodType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<DoctorPatientAppointmentItemResponse> items;
}
