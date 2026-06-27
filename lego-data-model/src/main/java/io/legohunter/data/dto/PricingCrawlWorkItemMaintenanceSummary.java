package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingCrawlWorkItemMaintenanceSummary {
    private Long workItemCount;
    private Long distinctMarketplaceListingCount;
    private Long duplicateWorkItemCount;
    private Long pendingWorkItemCount;
    private Long distinctPendingMarketplaceListingCount;
    private Long duplicatePendingWorkItemCount;
    private Long duePendingWorkItemCount;
    private Long retryablePendingWorkItemCount;
    private Long claimedWorkItemCount;
    private Long staleClaimedWorkItemCount;
    private Long succeededWorkItemCount;
    private Long skippedWorkItemCount;
    private Long failedWorkItemCount;
}
