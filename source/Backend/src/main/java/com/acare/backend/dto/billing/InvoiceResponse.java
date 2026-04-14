package com.acare.backend.dto.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.Invoice;
import com.acare.backend.entity.enums.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long medicalRecordId;
    private Long patientId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime dueAt;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InvoiceResponse from(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceStatus invoiceStatus = invoice.getStatus();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .medicalRecordId(invoice.getMedicalRecordId())
                .patientId(invoice.getPatientId())
                .subtotal(invoice.getSubtotal())
                .discountAmount(invoice.getDiscountAmount())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoiceStatus != null ? invoiceStatus.name() : null)
                .issuedAt(invoice.getIssuedAt())
                .dueAt(invoice.getDueAt())
                .paidAt(invoice.getPaidAt())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
