package com.acare.backend.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AppointmentAvailabilityOption {
    private String time;
    private int availableDoctors;
    private boolean available;
}
