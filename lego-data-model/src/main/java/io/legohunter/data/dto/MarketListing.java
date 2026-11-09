package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * One competitor lot captured in a market snapshot (HOMELAB-31).
 * Table {@code marketplace_pricing.market_listing}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketListing {
    private Long marketListingId;
    private Long marketSnapshotId;
    private String externalListingId;
    private String sellerKey;
    private String sellerName;
    private String sellerCountryCode;
    private Integer sellerFeedbackScore;
    private String rawConditionCode;
    private String rawCompletenessCode;
    private String conditionCode;
    private String completenessCode;
    private BigDecimal matchConfidence;
    private String listingFormatCode;
    private Integer quantityAvailable;
    private BigDecimal itemPrice;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;
    private String currencyCode;
    private BigDecimal nativePrice;
    private String nativeCurrencyCode;
    private String description;
    private String sourceListingPayload;
    private ZonedDateTime createdAt;
}
