package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class Transactions {
    private Long transactionId;
    private ZonedDateTime transactionDateTime;
    private String notes;
    private Long fromPartyId;
    private Long toPartyId;
    private Integer transactionPlatformId;
}
