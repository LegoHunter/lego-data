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
public class PricingDecision {
    private Long pricingDecisionId;
    private Integer marketplaceListingId;
    private Long pricingSnapshotId;
    /** marketplace_pricing.market_snapshot this row was made from; no foreign key (shared schema). */
    private Long marketSnapshotId;
    private ZonedDateTime marketSnapshotCapturedAt;
    private String algorithmVersion;
    private String decisionStatusCode;
    private String reasonCode;
    /** Reprice guard outcome: PASSED or BLOCKED; null for decisions made by the older decision job. */
    private String guardStatusCode;
    /** Why the guard blocked the decision (for example BELOW_MINIMUM_DELTA); null when it passed. */
    private String guardReasonCode;
    private String strategyCode;
    private BigDecimal computedPrice;
    private BigDecimal finalPrice;
    private BigDecimal previousPrice;
    private String currencyCode;
    private Integer comparableCount;
    private BigDecimal confidence;
    private String sourceSummaryJson;
    private String notes;
    private ZonedDateTime appliedAt;
    private ZonedDateTime createdAt;
}
