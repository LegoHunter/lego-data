package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceOrderPayload {
    private Integer marketplaceOrderPayloadId;
    private Integer marketplaceOrderId;
    private Integer marketplaceOrderSyncRunId;
    private String payloadTypeCode;
    private String payloadHash;
    private String payloadJson;
    private ZonedDateTime capturedAt;
    private ZonedDateTime createdAt;
}
