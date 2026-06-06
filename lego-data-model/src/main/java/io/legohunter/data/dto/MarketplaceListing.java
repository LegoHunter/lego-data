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
public class MarketplaceListing {
    private Integer marketplaceListingId;
    private Integer itemInventoryId;
    private Integer listingExternalServiceId;
    private Integer externalCatalogItemId;
    private String externalListingId;
    private String externalListingUrl;
    private String listingStatusCode;
    private String title;
    private String description;
    private String privateNotes;
    private BigDecimal unitPrice;
    private String currencyCode;
    private Boolean fixedPrice;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime publishedAt;
    private ZonedDateTime endedAt;
    private ZonedDateTime lastSynchronizedAt;
}


