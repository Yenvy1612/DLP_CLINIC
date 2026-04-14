package com.acare.backend.dto.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.Invoice;

import lombok.Data;

@Data
public class InvoiceRequest {
    private Long medicalRecordId;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private LocalDateTime dueAt;
    private String notes;

    public Invoice toEntity() {
        return Invoice.builder()
                .medicalRecordId(medicalRecordId)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .dueAt(dueAt)
                .notes(notes)
                .build();
    }
}
