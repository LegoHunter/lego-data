package net.bricklink.data.lego.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class TransactionItem {
    private Integer itemTransactionId;
    private Integer itemId;
    private Integer transactionId;
    private String transactionTypeCode;
    private Double price;
    private String notes;
    private Integer boxConditionId;
    private Integer instructionsConditionId;
}
