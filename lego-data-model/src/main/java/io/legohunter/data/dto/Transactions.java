package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Transactions {
    private Long transactionId;
    private LocalDate transactionDate;
    private String notes;
    private Long fromPartyId;
    private Long toPartyId;
    private Integer transactionPlatformId;
    private String transactionOrderId;
}
