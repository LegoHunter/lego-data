package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceOrder {
    private Integer marketplaceOrderId;
    private Integer lastSyncRunId;
    private String marketplaceCode;
    private String externalOrderId;
    private String orderDirection;
    private String externalStatusCode;
    private ZonedDateTime orderedAt;
    private ZonedDateTime statusChangedAt;
    private String buyerDisplayName;
    private String buyerEmail;
    private String paymentStatusCode;
    private String paymentMethod;
    private String paymentCurrencyCode;
    private ZonedDateTime paidAt;
    private String shippingMethod;
    private String shippingMethodId;
    private Boolean shippingAddressPresent;
    private Boolean trackingPresent;
    private BigDecimal subtotalAmount;
    private BigDecimal shippingAmount;
    private BigDecimal grandTotalAmount;
    private String currencyCode;
    private String displayCurrencyCode;
    private Integer totalCount;
    private Integer uniqueCount;
    private BigDecimal totalWeight;
    private Boolean invoiced;
    private Boolean filed;
    private Boolean sentDriveThru;
    private Boolean requireInsurance;
    private String payloadHash;
    private ZonedDateTime lastSeenAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
