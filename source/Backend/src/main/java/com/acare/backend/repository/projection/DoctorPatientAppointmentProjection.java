package com.acare.backend.repository.projection;

import java.time.LocalDateTime;

import com.acare.backend.entity.enums.AppointmentStatus;

public interface DoctorPatientAppointmentProjection {
    Long getAppointmentId();
    String getAppointmentCode();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    AppointmentStatus getStatus();
    String getServiceName();
    String getReason();
    String getNote();
}
