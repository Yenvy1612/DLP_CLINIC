package com.acare.backend.dto.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acare.backend.entity.PaymentTransaction;
import com.acare.backend.entity.enums.PaymentMethod;
import com.acare.backend.entity.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentTransactionRequest {
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String gatewayTransactionId;
    private String gatewayPayload;
    private LocalDateTime paidAt;

    public PaymentTransaction toEntity() {
        return PaymentTransaction.builder()
                .amount(amount)
                .method(method)
                .status(status)
                .gatewayTransactionId(gatewayTransactionId)
                .gatewayPayload(gatewayPayload)
                .paidAt(paidAt)
                .build();
    }
}
