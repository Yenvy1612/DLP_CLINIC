package com.acare.backend.dto.appointment.statistics;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPatientAppointmentItemResponse {
    private Long appointmentId;
    private String appointmentCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String serviceName;
    private String reason;
    private String note;
}
