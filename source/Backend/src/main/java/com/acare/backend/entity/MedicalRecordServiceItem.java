package com.acare.backend.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_record_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MedicalRecordServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_record_id", nullable = false)
    private Long medicalRecordId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", insertable = false, updatable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @Column(length = 255)
    private String note;

    @PrePersist
    protected void onCreate() {
        if (this.quantity == null || this.quantity < 1) {
            this.quantity = 1;
        }
        if (this.unitPrice == null) {
            this.unitPrice = BigDecimal.ZERO;
        }
        this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public MedicalRecordServiceItem prepareForRecord(Long medicalRecordId, BigDecimal defaultUnitPrice) {
        int resolvedQuantity = 1;
        Integer quantityValue = this.quantity;
        if (quantityValue != null) {
            resolvedQuantity = Math.max(quantityValue.intValue(), 1);
        }
        BigDecimal resolvedUnitPrice = this.unitPrice == null ? defaultUnitPrice : this.unitPrice;
        if (resolvedUnitPrice == null) {
            resolvedUnitPrice = BigDecimal.ZERO;
        }

        return this.toBuilder()
                .medicalRecordId(medicalRecordId)
                .quantity(resolvedQuantity)
                .unitPrice(resolvedUnitPrice)
                .lineTotal(resolvedUnitPrice.multiply(BigDecimal.valueOf(resolvedQuantity)))
                .build();
    }
}
