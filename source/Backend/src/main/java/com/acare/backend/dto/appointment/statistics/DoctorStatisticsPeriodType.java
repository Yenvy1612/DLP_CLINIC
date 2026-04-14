package com.acare.backend.dto.appointment.statistics;

import com.acare.backend.exception.BadRequestException;

public enum DoctorStatisticsPeriodType {
    DAY,
    MONTH,
    QUARTER;

    public static DoctorStatisticsPeriodType from(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return MONTH;
        }

        try {
            return DoctorStatisticsPeriodType.valueOf(rawValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Loai bo loc thong ke khong hop le");
        }
    }
}
