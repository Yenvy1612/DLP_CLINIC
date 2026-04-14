package com.acare.backend.dto.appointment.statistics;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDailyVisitPointResponse {
    private LocalDate date;
    private long doneAppointments;
}
