package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;
import io.legohunter.data.enums.CurrencyCode;

@Data
@Builder
public class TransactionCost {
    private Long transactionCostId;
    private Long transactionId;
    private String costTypeCode;
    private CurrencyCode currencyCode;
    private Double amount;
    private String notes;
}