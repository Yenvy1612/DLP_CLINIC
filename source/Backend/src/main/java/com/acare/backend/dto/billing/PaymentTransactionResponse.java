package com.acare.backend.dto.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.PaymentTransaction;
import com.acare.backend.entity.enums.PaymentMethod;
import com.acare.backend.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {
    private Long id;
    private String paymentReference;
    private Long invoiceId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String gatewayTransactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentTransactionResponse from(PaymentTransaction paymentTransaction) {
        if (paymentTransaction == null) {
            return null;
        }

        PaymentMethod paymentMethod = paymentTransaction.getMethod();
        PaymentStatus paymentStatus = paymentTransaction.getStatus();

        return PaymentTransactionResponse.builder()
                .id(paymentTransaction.getId())
                .paymentReference(paymentTransaction.getPaymentReference())
                .invoiceId(paymentTransaction.getInvoiceId())
                .amount(paymentTransaction.getAmount())
                .method(paymentMethod != null ? paymentMethod.name() : null)
                .status(paymentStatus != null ? paymentStatus.name() : null)
                .gatewayTransactionId(paymentTransaction.getGatewayTransactionId())
                .paidAt(paymentTransaction.getPaidAt())
                .createdAt(paymentTransaction.getCreatedAt())
                .updatedAt(paymentTransaction.getUpdatedAt())
                .build();
    }
}
