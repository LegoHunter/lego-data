package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionItemRevenue {
    private Long transactionItemRevenueId;
    private Long transactionItemId;
    private String currencyCode;
    private BigDecimal unitAmount;
    private Integer quantity;
    private BigDecimal totalAmount;
}
