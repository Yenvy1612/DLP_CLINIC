package com.acare.backend.dto.appointment.statistics;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPatientSummaryRowResponse {
    private Long patientId;
    private String patientName;
    private long doneAppointments;
    private List<String> services;
}
