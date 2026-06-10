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
public class MarketplaceOrderTransactionLink {
    private Integer marketplaceOrderTransactionLinkId;
    private Integer marketplaceOrderId;
    private Long transactionId;
    private Integer marketplaceOrderItemId;
    private Long transactionItemId;
    private String linkTypeCode;
    private String linkStatusCode;
    private ZonedDateTime linkedAt;
    private ZonedDateTime unlinkedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
