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
    private String algorithmVersion;
    private String decisionStatusCode;
    private String reasonCode;
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
