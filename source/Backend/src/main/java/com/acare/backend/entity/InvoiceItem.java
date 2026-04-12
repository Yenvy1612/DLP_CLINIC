package com.acare.backend.entity;

import java.math.BigDecimal;

import com.acare.backend.entity.enums.InvoiceItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    @Builder.Default
    private InvoiceItemType itemType = InvoiceItemType.OTHER;

    @Column(name = "ref_id")
    private Long refId;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    protected void normalizeLineTotal() {
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }
        lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public InvoiceItem prepareForInvoice(Long invoiceId) {
        return this.toBuilder()
                .invoiceId(invoiceId)
                .itemType(this.itemType == null ? InvoiceItemType.OTHER : this.itemType)
                .build();
    }

    public static InvoiceItem fromMedicalRecordService(Long invoiceId, MedicalRecordServiceItem source, String description) {
        return InvoiceItem.builder()
                .invoiceId(invoiceId)
                .itemType(InvoiceItemType.SERVICE)
                .refId(source.getServiceId())
                .quantity(source.getQuantity())
                .unitPrice(source.getUnitPrice())
                .lineTotal(source.getLineTotal())
                .description(description)
                .build();
    }
}
