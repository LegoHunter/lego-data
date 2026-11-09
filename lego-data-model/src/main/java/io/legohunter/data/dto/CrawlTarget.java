package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * The crawl queue row for one marketplace, item and query condition, updated in place by each crawl (HOMELAB-31).
 * Table {@code marketplace_pricing.crawl_target}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlTarget {
    private Long crawlTargetId;
    private String marketplaceCode;
    private Integer canonicalItemId;
    private String queryConditionCode;
    private String targetStatusCode;
    private ZonedDateTime nextDueAt;
    private ZonedDateTime claimedAt;
    private ZonedDateTime claimExpiresAt;
    private String claimedBy;
    private ZonedDateTime lastAttemptAt;
    private ZonedDateTime lastSuccessAt;
    private Long lastMarketSnapshotId;
    private Integer consecutiveFailureCount;
    private String lastErrorMessage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
