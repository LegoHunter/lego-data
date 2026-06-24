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
public class PricingDecisionReview {
    private Integer marketplaceListingId;
    private String externalListingId;
    private String listingStatusCode;
    private BigDecimal currentUnitPrice;
    private String currencyCode;
    private Boolean fixedPrice;
    private Integer itemInventoryId;
    private String itemInventoryUuid;
    private String newOrUsed;
    private String completeness;
    private Integer externalCatalogItemId;
    private String externalItemKey;
    private String externalUniqueKey;
    private Long pricingDecisionId;
    private Long pricingSnapshotId;
    private String algorithmVersion;
    private String decisionStatusCode;
    private String reasonCode;
    private String strategyCode;
    private BigDecimal computedPrice;
    private BigDecimal finalPrice;
    private BigDecimal previousPrice;
    private Integer comparableCount;
    private BigDecimal confidence;
    private String notes;
    private ZonedDateTime decisionCreatedAt;
    private ZonedDateTime appliedAt;
    private ZonedDateTime snapshotCapturedAt;
    private Integer snapshotComparableCount;
}
