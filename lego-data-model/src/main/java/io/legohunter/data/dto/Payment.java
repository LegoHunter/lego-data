package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Payment {
    private Long paymentId;
    private LocalDate paymentDate;
    private Long transactionId;
    private String currencyCode;
    private String sellerCurrencyCode;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private Integer paymentPlatformId;
    private String paymentPlatformTransactionId;
}
