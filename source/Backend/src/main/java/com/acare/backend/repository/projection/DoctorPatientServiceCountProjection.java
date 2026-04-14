package com.acare.backend.repository.projection;

public interface DoctorPatientServiceCountProjection {
    Long getPatientId();
    String getPatientName();
    String getServiceName();
    Long getTotalCount();
}
