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
public class PricingCrawlWorkItem {
    private Long pricingCrawlWorkItemId;
    private Integer marketplaceListingId;
    private Integer externalCatalogItemId;
    private Integer sourceExternalServiceId;
    private String workStatusCode;
    private Integer attemptCount;
    private Integer maxAttempts;
    private ZonedDateTime nextAttemptAt;
    private ZonedDateTime claimedAt;
    private ZonedDateTime completedAt;
    private String sourceRequestUrl;
    private String sourceRequestParameters;
    private String lastErrorMessage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
