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
public class MarketplaceListingSyncRequest {
    private Long marketplaceListingSyncRequestId;
    private Integer marketplaceListingId;
    private Integer listingExternalServiceId;
    private Long pricingDecisionId;
    private Long pricingApplyReadinessId;
    private String syncRequestTypeCode;
    private String syncRequestStatusCode;
    private String syncReasonCode;
    private BigDecimal previousUnitPrice;
    private BigDecimal requestedUnitPrice;
    private String currencyCode;
    private String remoteInventoryId;
    private String remoteVisibilityScopeCode;
    private String remoteVisibilityContainerId;
    private Boolean remoteIsPubliclyAvailable;
    private String environmentCode;
    private String createdByJobName;
    private String lastErrorMessage;
    private Integer attemptCount;
    private Integer maxAttempts;
    private ZonedDateTime nextAttemptAt;
    private ZonedDateTime claimedAt;
    private ZonedDateTime appliedLocalAt;
    private ZonedDateTime completedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
