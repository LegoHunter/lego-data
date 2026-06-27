package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingHydrationGap {
    private Integer marketplaceListingId;
    private String externalListingId;
    private Integer externalCatalogItemId;
    private String externalItemKey;
    private String externalUniqueKey;
    private Integer itemInventoryId;
    private String itemInventoryUuid;
    private String newOrUsed;
    private String completeness;
}
