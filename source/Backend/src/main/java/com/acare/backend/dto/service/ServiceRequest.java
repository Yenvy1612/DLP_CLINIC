package com.acare.backend.dto.service;

import java.math.BigDecimal;

import com.acare.backend.entity.Service;

import lombok.Data;

@Data
public class ServiceRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private String department;
    private Long specialtyId;
    private Boolean active;

    public Service toEntity() {
        return Service.builder()
                .name(name)
                .price(price)
                .description(description)
                .department(department)
                .specialtyId(specialtyId)
                .active(active)
                .build();
    }
}
