package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EbayMarketplaceListing {
    private Integer marketplaceListingId;
    private String ebayItemId;
    private String ebayCategoryId;
    private String conditionId;
    private String conditionDescriptorFields;
    private String listingFormat;
    private String duration;
    private String shippingPolicyId;
    private String paymentPolicyId;
    private String returnPolicyId;
}


