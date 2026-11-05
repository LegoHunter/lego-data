package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Counts of Pricing Plane data-quality gaps across all listings in the requested statuses.
 * A listing with several gaps is counted once in {@code listingsWithGaps} and once per gap.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingDataQualitySummary {
    private long listingsChecked;
    private long listingsWithGaps;
    private long missingCondition;
    private long missingCompleteness;
    private long missingCatalogItem;
    private long missingItemNumber;
    private long missingItemType;
    private long missingInternalItemId;
    private long missingBoxGrade;
    private long missingInstructionsGrade;
}
