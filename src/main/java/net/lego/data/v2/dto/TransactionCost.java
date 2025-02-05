package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCost {
    private Long transactionCostId;
    private Long transactionId;
    private String costTypeCode;
    private Double amount;
}