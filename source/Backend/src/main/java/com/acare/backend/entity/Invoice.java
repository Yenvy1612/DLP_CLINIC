package com.acare.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.enums.InvoiceStatus;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 30)
    private String invoiceNumber;

    @Column(name = "medical_record_id", nullable = false, unique = true)
    private Long medicalRecordId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", insertable = false, updatable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private User patient;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        normalizeMoney();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        normalizeMoney();
    }

    private void normalizeMoney() {
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
    }

    public Invoice prepareForCreate(Long resolvedPatientId, String generatedInvoiceNumber) {
        String resolvedInvoiceNumber = this.invoiceNumber;
        if (resolvedInvoiceNumber == null || resolvedInvoiceNumber.isBlank()) {
            resolvedInvoiceNumber = generatedInvoiceNumber;
        }

        return this.toBuilder()
                .patientId(resolvedPatientId)
                .invoiceNumber(resolvedInvoiceNumber)
                .status(this.status == null ? InvoiceStatus.DRAFT : this.status)
                .discountAmount(this.discountAmount == null ? BigDecimal.ZERO : this.discountAmount)
                .taxAmount(this.taxAmount == null ? BigDecimal.ZERO : this.taxAmount)
                .subtotal(this.subtotal == null ? BigDecimal.ZERO : this.subtotal)
                .totalAmount(this.totalAmount == null ? BigDecimal.ZERO : this.totalAmount)
                .build();
    }

    public Invoice markIssued(LocalDateTime at) {
        return this.toBuilder()
                .status(InvoiceStatus.ISSUED)
                .issuedAt(this.issuedAt != null ? this.issuedAt : at)
                .build();
    }

    public Invoice markPaid(LocalDateTime at) {
        return this.toBuilder()
                .status(InvoiceStatus.PAID)
                .paidAt(at)
                .build();
    }

    public Invoice recalculateTotals(BigDecimal nextSubtotal) {
        BigDecimal resolvedSubtotal = nextSubtotal == null ? BigDecimal.ZERO : nextSubtotal;
        BigDecimal resolvedDiscount = this.discountAmount == null ? BigDecimal.ZERO : this.discountAmount;
        BigDecimal resolvedTax = this.taxAmount == null ? BigDecimal.ZERO : this.taxAmount;

        BigDecimal nextTotal = resolvedSubtotal.subtract(resolvedDiscount).add(resolvedTax);
        if (nextTotal.compareTo(BigDecimal.ZERO) < 0) {
            nextTotal = BigDecimal.ZERO;
        }

        return this.toBuilder()
                .subtotal(resolvedSubtotal)
                .discountAmount(resolvedDiscount)
                .taxAmount(resolvedTax)
                .totalAmount(nextTotal)
                .build();
    }
}
