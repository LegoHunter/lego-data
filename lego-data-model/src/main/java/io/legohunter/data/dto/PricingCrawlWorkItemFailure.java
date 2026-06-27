package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingCrawlWorkItemFailure {
    private Long pricingCrawlWorkItemId;
    private Integer marketplaceListingId;
    private String externalListingId;
    private Integer externalCatalogItemId;
    private String externalItemKey;
    private String externalUniqueKey;
    private Integer itemInventoryId;
    private String itemInventoryUuid;
    private String newOrUsed;
    private String completeness;
    private String workStatusCode;
    private Integer attemptCount;
    private Integer maxAttempts;
    private ZonedDateTime nextAttemptAt;
    private String sourceRequestUrl;
    private String sourceRequestParameters;
    private String lastErrorMessage;
    private ZonedDateTime updatedAt;
}
