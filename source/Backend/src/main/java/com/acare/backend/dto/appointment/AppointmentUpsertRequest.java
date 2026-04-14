package com.acare.backend.dto.appointment;

import java.time.LocalDateTime;

import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.enums.AppointmentStatus;

import lombok.Data;

@Data
public class AppointmentUpsertRequest {
    private Long patientId;
    private Long doctorId;
    private Long serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private String reason;
    private String note;

    public Appointment toEntity() {
        return Appointment.builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .serviceId(serviceId)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .reason(reason)
                .note(note)
                .build();
    }
}
