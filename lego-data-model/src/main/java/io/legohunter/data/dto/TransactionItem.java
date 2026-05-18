package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionItem {
    private Long transactionItemId;
    private Long transactionId;
    private String transactionTypeCode;
    private Integer itemInventoryId;
    private String notes;
}