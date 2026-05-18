package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPlatform {
    private Integer paymentPlatformId;
    private String paymentPlatformName;
    private String paymentPlatformUrl;
}
