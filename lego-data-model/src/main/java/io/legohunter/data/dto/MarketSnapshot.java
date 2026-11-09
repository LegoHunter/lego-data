package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * One successful fetch of the lots for sale for one item and query condition on one marketplace (HOMELAB-31). The rows are the price history.
 * Table {@code marketplace_pricing.market_snapshot}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSnapshot {
    private Long marketSnapshotId;
    private Long crawlTargetId;
    private String marketplaceCode;
    private Integer canonicalItemId;
    private String queryConditionCode;
    private String marketplaceItemKey;
    private String marketplaceUniqueKey;
    private String requestUrl;
    private String requestParameters;
    private String rawPayloadHash;
    private Integer listingCount;
    private ZonedDateTime capturedAt;
    private String sourceEnvironmentCode;
    private Long sourcePricingSnapshotId;
    private ZonedDateTime createdAt;
}
