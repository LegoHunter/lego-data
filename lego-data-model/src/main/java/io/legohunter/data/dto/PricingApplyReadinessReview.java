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
public class PricingApplyReadinessReview {
    private Long pricingApplyReadinessId;
    private Integer marketplaceListingId;
    private String externalListingId;
    private String listingStatusCode;
    private Boolean fixedPrice;
    private Integer itemInventoryId;
    private String itemInventoryUuid;
    private Integer externalCatalogItemId;
    private String externalItemKey;
    private String externalUniqueKey;
    private Long pricingDecisionId;
    private Long pricingSnapshotId;
    private String algorithmVersion;
    private String decisionStatusCode;
    private String decisionReasonCode;
    private String strategyCode;
    private String readinessStatusCode;
    private String blockReasonCode;
    private BigDecimal currentPrice;
    private BigDecimal proposedPrice;
    private BigDecimal deltaAmount;
    private BigDecimal deltaPercent;
    private BigDecimal minimumRequiredDelta;
    private String currencyCode;
    private BigDecimal confidence;
    private Integer comparableCount;
    private ZonedDateTime decisionCreatedAt;
    private ZonedDateTime snapshotCapturedAt;
    private ZonedDateTime evaluatedAt;
}
