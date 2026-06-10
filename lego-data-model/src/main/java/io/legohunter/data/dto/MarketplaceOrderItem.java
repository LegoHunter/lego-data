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
public class MarketplaceOrderItem {
    private Integer marketplaceOrderItemId;
    private Integer marketplaceOrderId;
    private Integer marketplaceListingId;
    private Integer itemInventoryId;
    private String externalOrderItemId;
    private String externalInventoryId;
    private String externalItemNo;
    private String externalItemType;
    private Integer colorId;
    private String colorName;
    private Integer quantity;
    private String conditionCode;
    private String completenessCode;
    private BigDecimal unitPrice;
    private BigDecimal finalUnitPrice;
    private String currencyCode;
    private BigDecimal itemWeight;
    private String remarks;
    private String description;
    private String payloadHash;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
