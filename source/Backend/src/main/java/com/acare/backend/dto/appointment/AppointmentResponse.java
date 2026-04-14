package com.acare.backend.dto.appointment;

import java.time.LocalDateTime;

import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private String appointmentCode;
    private Long patientId;
    private Long doctorId;
    private Long serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String reason;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AppointmentResponse from(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentStatus appointmentStatus = appointment.getStatus();

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentCode(appointment.getAppointmentCode())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .serviceId(appointment.getServiceId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointmentStatus != null ? appointmentStatus.name() : null)
                .reason(appointment.getReason())
                .note(appointment.getNote())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
