package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * A marketplace whose asking prices are crawled into the shared pricing schema (HOMELAB-31).
 * Table {@code marketplace_pricing.marketplace}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marketplace {
    private String marketplaceCode;
    private String displayName;
    private String baseUrl;
    private String defaultCurrencyCode;
    private Boolean crawlEnabled;
    private ZonedDateTime createdAt;
}
