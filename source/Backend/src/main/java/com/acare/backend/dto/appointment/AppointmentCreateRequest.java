package com.acare.backend.dto.appointment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreateRequest {
    private Long patientId;
    private Long doctorId;
    private Long serviceId;
    private LocalDateTime startTime;
    private String reason;
    private String note;
}