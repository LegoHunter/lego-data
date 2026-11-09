package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * One LEGO catalog item, identified by its BrickLink item type and number, independent of any environment or marketplace (HOMELAB-31).
 * Table {@code marketplace_pricing.canonical_item}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanonicalItem {
    private Integer canonicalItemId;
    private String itemTypeCode;
    private String itemNumber;
    private String itemName;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
