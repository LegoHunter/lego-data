package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.EbayListingItemSpecific;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface EbayListingItemSpecificMapper {
    String ALL_COLUMNS = """
            marketplace_listing_id,
            name,
            `value`
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_listing_item_specific")
    @ResultMap("ebayListingItemSpecificResultMap")
    Set<EbayListingItemSpecific> findAll();

    @Select("""
            SELECT marketplace_listing_id,
                   name,
                   `value`
            FROM ebay_listing_item_specific
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND name = #{name}
              AND `value` = #{value}
            """)
    @ResultMap("ebayListingItemSpecificResultMap")
    Optional<EbayListingItemSpecific> findByMarketplaceListingIdAndNameAndValue(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("name") String name,
            @Param("value") String value
    );

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_listing_item_specific WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("ebayListingItemSpecificResultMap")
    Set<EbayListingItemSpecific> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM ebay_listing_item_specific WHERE name = #{name}")
    @ResultMap("ebayListingItemSpecificResultMap")
    Set<EbayListingItemSpecific> findByName(String name);

    @Insert("""
            INSERT INTO ebay_listing_item_specific (marketplace_listing_id,
                                                   name,
                                                   `value`)
            VALUES (#{marketplaceListingId},
                    #{name},
                    #{value})
            """)
    int insert(EbayListingItemSpecific ebayListingItemSpecific);

    @Update("""
            UPDATE ebay_listing_item_specific
            SET `value` = #{value}
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND name = #{name}
              AND `value` = #{value}
            """)
    int update(EbayListingItemSpecific ebayListingItemSpecific);

    @Delete("""
            DELETE FROM ebay_listing_item_specific
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND name = #{name}
              AND `value` = #{value}
            """)
    int delete(@Param("marketplaceListingId") Integer marketplaceListingId, @Param("name") String name, @Param("value") String value);

    default int upsert(EbayListingItemSpecific ebayListingItemSpecific) {
        return findByMarketplaceListingIdAndNameAndValue(ebayListingItemSpecific.getMarketplaceListingId(), ebayListingItemSpecific.getName(), ebayListingItemSpecific.getValue())
                .map(existing -> update(ebayListingItemSpecific))
                .orElseGet(() -> insert(ebayListingItemSpecific));
    }
}

