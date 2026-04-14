package com.acare.backend.dto.medicalrecord;

import java.math.BigDecimal;

import com.acare.backend.entity.MedicalRecordServiceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordServiceItemResponse {
    private Long id;
    private Long medicalRecordId;
    private Long serviceId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String note;

    public static MedicalRecordServiceItemResponse from(MedicalRecordServiceItem item) {
        if (item == null) {
            return null;
        }

        return MedicalRecordServiceItemResponse.builder()
                .id(item.getId())
                .medicalRecordId(item.getMedicalRecordId())
                .serviceId(item.getServiceId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .note(item.getNote())
                .build();
    }
}
