package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingCrawlWorkItemDuplicate {
    private Integer marketplaceListingId;
    private Integer workItemCount;
    private Integer pendingCount;
    private String workStatusCodes;
    private String pricingCrawlWorkItemIds;
}
