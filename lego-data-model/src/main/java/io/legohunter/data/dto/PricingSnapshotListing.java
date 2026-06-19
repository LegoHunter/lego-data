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
public class PricingSnapshotListing {
    private Long pricingSnapshotListingId;
    private Long pricingSnapshotId;
    private String externalListingId;
    private String sellerName;
    private String sellerCountryCode;
    private String itemConditionCode;
    private String completenessCode;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
    private String currencyCode;
    private String description;
    private String extendedDescription;
    private String sourceListingPayload;
    private ZonedDateTime createdAt;
}
