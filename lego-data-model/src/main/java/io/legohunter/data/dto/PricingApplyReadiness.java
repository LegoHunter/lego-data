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
public class PricingApplyReadiness {
    private Long pricingApplyReadinessId;
    private Integer marketplaceListingId;
    private Long pricingDecisionId;
    private Long pricingSnapshotId;
    /** marketplace_pricing.market_snapshot this row was made from; no foreign key (shared schema). */
    private Long marketSnapshotId;
    private ZonedDateTime marketSnapshotCapturedAt;
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
    private ZonedDateTime evaluatedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
