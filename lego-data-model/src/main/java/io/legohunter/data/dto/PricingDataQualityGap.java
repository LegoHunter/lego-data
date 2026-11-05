package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One marketplace listing whose inventory or catalog data is missing something the Pricing Plane
 * needs. The raw values are included so the gap can be fixed without another lookup; the
 * {@code missing*} flags say which gaps apply.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingDataQualityGap {
    private Integer marketplaceListingId;
    private String externalListingId;
    private String listingStatusCode;
    private Boolean fixedPrice;
    private Integer itemInventoryId;
    private String itemInventoryUuid;
    private String newOrUsed;
    private String completeness;
    private Integer boxConditionId;
    private String boxConditionCode;
    private Integer instructionsConditionId;
    private String instructionsConditionCode;
    private Integer externalCatalogItemId;
    private String externalItemKey;
    private String itemTypeCode;
    private String externalUniqueKey;
    /** Status of the listing's most recent crawl work item, or null when it was never crawled. */
    private String latestCrawlStatusCode;

    /** {@code new_or_used} is blank or not one of N, NEW, U, USED. */
    private boolean missingCondition;
    /** {@code completeness} is blank. */
    private boolean missingCompleteness;
    /** The listing has no BrickLink catalog item. */
    private boolean missingCatalogItem;
    /** The catalog item has no BrickLink item number. */
    private boolean missingItemNumber;
    /** The catalog item has no item type; the crawl falls back to its configured default type. */
    private boolean missingItemType;
    /** The catalog item has no BrickLink internal item id yet (never hydrated, or hydration failed). */
    private boolean missingInternalItemId;
    /** A used item with no box grade recorded; it is priced as if it has no box. */
    private boolean missingBoxGrade;
    /** A used item with no instructions grade recorded; it is priced as if it has no instructions. */
    private boolean missingInstructionsGrade;
}
