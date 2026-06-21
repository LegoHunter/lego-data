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
public class PricingSnapshot {
    private Long pricingSnapshotId;
    private Long pricingCrawlWorkItemId;
    private Integer marketplaceListingId;
    private Integer externalCatalogItemId;
    private Integer sourceExternalServiceId;
    private String sourceItemKey;
    private String sourceUniqueKey;
    private String itemConditionCode;
    private String completenessCode;
    private String sourceRequestUrl;
    private String sourceRequestParameters;
    private String rawPayloadHash;
    private Integer comparableCount;
    private ZonedDateTime capturedAt;
    private ZonedDateTime createdAt;
}
