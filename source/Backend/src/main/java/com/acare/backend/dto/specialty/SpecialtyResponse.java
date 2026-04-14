package com.acare.backend.dto.specialty;

import com.acare.backend.entity.Specialty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyResponse {
    private Long id;
    private String code;
    private String name;
    private Boolean active;

    public static SpecialtyResponse from(Specialty specialty) {
        if (specialty == null) {
            return null;
        }

        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .code(specialty.getCode())
                .name(specialty.getName())
                .active(specialty.getActive())
                .build();
    }
}
