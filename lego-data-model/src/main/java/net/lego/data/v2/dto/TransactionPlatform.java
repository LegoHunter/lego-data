package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionPlatform {
    private Integer transactionPlatformId;
    private String transactionPlatformName;
}
