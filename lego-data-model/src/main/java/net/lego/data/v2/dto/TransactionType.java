package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionType {
    private String transactionTypeCode;
    private String transactionTypeDescription;
    private int conversionFactor;
}
