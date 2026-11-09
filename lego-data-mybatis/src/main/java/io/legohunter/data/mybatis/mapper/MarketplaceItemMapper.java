package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

/** How each canonical item is found on each marketplace (HOMELAB-31). */
public interface MarketplaceItemMapper {
    String ALL_COLUMNS = """
            marketplace_item_id,
            marketplace_code,
            canonical_item_id,
            marketplace_item_key,
            marketplace_unique_key,
            search_terms,
            match_method_code,
            resolved_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.marketplace_item WHERE marketplace_item_id = #{marketplaceItemId}")
    @ResultMap("marketplaceItemResultMap")
    Optional<MarketplaceItem> findByMarketplaceItemId(Integer marketplaceItemId);

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.marketplace_item
            WHERE marketplace_code = #{marketplaceCode}
              AND canonical_item_id = #{canonicalItemId}
            """)
    @ResultMap("marketplaceItemResultMap")
    Optional<MarketplaceItem> findByMarketplaceCodeAndCanonicalItemId(
            @Param("marketplaceCode") String marketplaceCode,
            @Param("canonicalItemId") Integer canonicalItemId,
            @Param("columns") String columns
    );

    default Optional<MarketplaceItem> findByMarketplaceCodeAndCanonicalItemId(String marketplaceCode, Integer canonicalItemId) {
        return findByMarketplaceCodeAndCanonicalItemId(marketplaceCode, canonicalItemId, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.marketplace_item (
                marketplace_code,
                canonical_item_id,
                marketplace_item_key,
                marketplace_unique_key,
                search_terms,
                match_method_code,
                resolved_at
            )
            VALUES (
                #{marketplaceCode},
                #{canonicalItemId},
                #{marketplaceItemKey},
                #{marketplaceUniqueKey},
                #{searchTerms},
                #{matchMethodCode},
                #{resolvedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "marketplace_item_id", keyProperty = "marketplaceItemId")
    void insert(MarketplaceItem marketplaceItem);

    @Update("""
            UPDATE ${marketplacePricingSchema}.marketplace_item
            SET marketplace_item_key = #{marketplaceItemKey},
                marketplace_unique_key = #{marketplaceUniqueKey},
                search_terms = #{searchTerms},
                match_method_code = #{matchMethodCode},
                resolved_at = #{resolvedAt}
            WHERE marketplace_item_id = #{marketplaceItemId}
            """)
    int update(MarketplaceItem marketplaceItem);
}
