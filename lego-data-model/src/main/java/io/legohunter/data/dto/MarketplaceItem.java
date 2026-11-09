package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * How to find one canonical item on one marketplace (HOMELAB-31).
 * Table {@code marketplace_pricing.marketplace_item}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItem {
    private Integer marketplaceItemId;
    private String marketplaceCode;
    private Integer canonicalItemId;
    private String marketplaceItemKey;
    private String marketplaceUniqueKey;
    private String searchTerms;
    private String matchMethodCode;
    private ZonedDateTime resolvedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
