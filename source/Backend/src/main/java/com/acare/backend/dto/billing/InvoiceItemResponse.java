package com.acare.backend.dto.billing;

import java.math.BigDecimal;

import com.acare.backend.entity.InvoiceItem;
import com.acare.backend.entity.enums.InvoiceItemType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponse {
    private Long id;
    private Long invoiceId;
    private String itemType;
    private Long refId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public static InvoiceItemResponse from(InvoiceItem item) {
        if (item == null) {
            return null;
        }

        InvoiceItemType type = item.getItemType();

        return InvoiceItemResponse.builder()
                .id(item.getId())
                .invoiceId(item.getInvoiceId())
                .itemType(type != null ? type.name() : null)
                .refId(item.getRefId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
