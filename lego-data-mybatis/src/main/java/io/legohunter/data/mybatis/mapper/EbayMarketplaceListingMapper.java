package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.EbayMarketplaceListing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface EbayMarketplaceListingMapper {
    String ALL_COLUMNS = """
            marketplace_listing_id,
            ebay_item_id,
            ebay_category_id,
            condition_id,
            condition_descriptor_fields,
            listing_format,
            duration,
            shipping_policy_id,
            payment_policy_id,
            return_policy_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_marketplace_listing")
    @ResultMap("ebayMarketplaceListingResultMap")
    Set<EbayMarketplaceListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("ebayMarketplaceListingResultMap")
    Optional<EbayMarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_marketplace_listing WHERE ebay_item_id = #{ebayItemId}")
    @ResultMap("ebayMarketplaceListingResultMap")
    Optional<EbayMarketplaceListing> findByEbayItemId(String ebayItemId);

    @Insert("""
            INSERT INTO ebay_marketplace_listing (
                marketplace_listing_id,
                ebay_item_id,
                ebay_category_id,
                condition_id,
                condition_descriptor_fields,
                listing_format,
                duration,
                shipping_policy_id,
                payment_policy_id,
                return_policy_id
            )
            VALUES (
                #{marketplaceListingId},
                #{ebayItemId},
                #{ebayCategoryId},
                #{conditionId},
                #{conditionDescriptorFields},
                #{listingFormat},
                #{duration},
                #{shippingPolicyId},
                #{paymentPolicyId},
                #{returnPolicyId}
            )
            """)
    int insert(EbayMarketplaceListing ebayMarketplaceListing);

    @Update("""
            UPDATE ebay_marketplace_listing
            SET ebay_item_id = #{ebayItemId},
                ebay_category_id = #{ebayCategoryId},
                condition_id = #{conditionId},
                condition_descriptor_fields = #{conditionDescriptorFields},
                listing_format = #{listingFormat},
                duration = #{duration},
                shipping_policy_id = #{shippingPolicyId},
                payment_policy_id = #{paymentPolicyId},
                return_policy_id = #{returnPolicyId}
            WHERE marketplace_listing_id = #{marketplaceListingId}
            """)
    int update(EbayMarketplaceListing ebayMarketplaceListing);

    @Delete("DELETE FROM ebay_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    int delete(Integer marketplaceListingId);

    default int upsert(EbayMarketplaceListing ebayMarketplaceListing) {
        return findByMarketplaceListingId(ebayMarketplaceListing.getMarketplaceListingId())
                .map(existing -> update(ebayMarketplaceListing))
                .orElseGet(() -> insert(ebayMarketplaceListing));
    }
}
