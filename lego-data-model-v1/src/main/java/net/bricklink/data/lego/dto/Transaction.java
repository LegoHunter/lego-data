package net.bricklink.data.lego.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Transaction {
    private int transactionId;
    private LocalDate transactionDate;
    private String notes;
    private Double shippingAmount;
    private Integer fromPartyId;
    private Integer toPartyId;
    private List<TransactionItem> transactionItems;
    private Party fromParty;
    private Party toParty;
}
