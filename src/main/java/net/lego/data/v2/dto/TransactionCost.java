package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;
import net.lego.data.v2.enums.CurrencyCode;

@Data
@Builder
public class TransactionCost {
    private Long transactionCostId;
    private Long transactionId;
    private String costTypeCode;
    private Double amount;
    private CurrencyCode currencyCode;
}