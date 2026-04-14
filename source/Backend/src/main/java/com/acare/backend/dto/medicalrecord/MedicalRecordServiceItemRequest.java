package com.acare.backend.dto.medicalrecord;

import java.math.BigDecimal;

import com.acare.backend.entity.MedicalRecordServiceItem;

import lombok.Data;

@Data
public class MedicalRecordServiceItemRequest {
    private Long serviceId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String note;

    public MedicalRecordServiceItem toEntity() {
        return MedicalRecordServiceItem.builder()
                .serviceId(serviceId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .note(note)
                .build();
    }
}
