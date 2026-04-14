package com.acare.backend.repository.projection;

import java.time.LocalDate;

public interface DoctorDailyDoneCountProjection {
    LocalDate getVisitDate();
    Long getTotalCount();
}
