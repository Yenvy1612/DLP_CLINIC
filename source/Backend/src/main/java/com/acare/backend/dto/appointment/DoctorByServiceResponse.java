package com.acare.backend.dto.appointment;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorByServiceResponse {
    private Long id;
    private String fullName;
    private String phone;
    private Long specialtyId;
    private String biography;
    private String workingDays;
    private String clinicLocation;

    public static DoctorByServiceResponse fromMap(Map<String, Object> source) {
        if (source == null) {
            return null;
        }

        return DoctorByServiceResponse.builder()
                .id(toLong(source.get("id")))
                .fullName(toStringValue(source.get("fullName")))
                .phone(toStringValue(source.get("phone")))
                .specialtyId(toLong(source.get("specialtyId")))
                .biography(toStringValue(source.get("biography")))
                .workingDays(toStringValue(source.get("workingDays")))
                .clinicLocation(toStringValue(source.get("clinicLocation")))
                .build();
    }

    private static Long toLong(Object raw) {
        if (raw == null) {
            return null;
        }

        if (raw instanceof Number value) {
            return value.longValue();
        }

        try {
            return Long.valueOf(String.valueOf(raw));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String toStringValue(Object raw) {
        return raw == null ? null : String.valueOf(raw);
    }
}
