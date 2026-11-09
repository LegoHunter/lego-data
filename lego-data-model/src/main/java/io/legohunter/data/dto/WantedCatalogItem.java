package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One catalog item, condition and sub-condition an environment is pricing: what it puts on its want list
 * in the shared marketplace_pricing schema (HOMELAB-32). Codes are already canonical: N/U and S/C/B/X.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WantedCatalogItem {
    private String itemTypeCode;
    private String itemNumber;
    private String itemName;
    private String conditionCode;
    private String completenessCode;
    private Integer listingCount;
}
