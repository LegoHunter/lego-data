package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionItemCost {
    private Long transactionItemCostId;
    private Long transactionItemId;
    private String costTypeCode;
    private String currencyCode;
    private Double amount;
    private String notes;
}