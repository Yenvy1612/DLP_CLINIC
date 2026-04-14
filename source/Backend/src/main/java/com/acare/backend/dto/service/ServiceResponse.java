package com.acare.backend.dto.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String department;
    private Long specialtyId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ServiceResponse from(Service service) {
        if (service == null) {
            return null;
        }

        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .price(service.getPrice())
                .description(service.getDescription())
                .department(service.getDepartment())
                .specialtyId(service.getSpecialtyId())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}
