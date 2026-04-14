package com.acare.backend.dto.billing;

import java.math.BigDecimal;

import com.acare.backend.entity.InvoiceItem;
import com.acare.backend.entity.enums.InvoiceItemType;

import lombok.Data;

@Data
public class InvoiceItemRequest {
    private InvoiceItemType itemType;
    private Long refId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;

    public InvoiceItem toEntity() {
        return InvoiceItem.builder()
                .itemType(itemType)
                .refId(refId)
                .description(description)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }
}
