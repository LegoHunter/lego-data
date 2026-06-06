package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceListing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceListingMapper {
    String ALL_COLUMNS = """
            marketplace_listing_id,
            item_inventory_id,
            listing_external_service_id,
            external_catalog_item_id,
            external_listing_id,
            external_listing_url,
            listing_status_code,
            title,
            description,
            private_notes,
            unit_price,
            currency_code,
            fixed_price,
            created_at,
            updated_at,
            published_at,
            ended_at,
            last_synchronized_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing")
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("marketplaceListingResultMap")
    Optional<MarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing WHERE item_inventory_id = #{itemInventoryId}")
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findByItemInventoryId(Integer itemInventoryId);

    @Select("""
            SELECT marketplace_listing_id,
                   item_inventory_id,
                   listing_external_service_id,
                   external_catalog_item_id,
                   external_listing_id,
                   external_listing_url,
                   listing_status_code,
                   title,
                   description,
                   private_notes,
                   unit_price,
                   currency_code,
                   fixed_price,
                   created_at,
                   updated_at,
                   published_at,
                   ended_at,
                   last_synchronized_at
            FROM marketplace_listing
            WHERE listing_external_service_id = #{listingExternalServiceId}
              AND external_listing_id = #{externalListingId}
            """)
    @ResultMap("marketplaceListingResultMap")
    Optional<MarketplaceListing> findByListingExternalServiceIdAndExternalListingId(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("externalListingId") String externalListingId
    );

    @Insert("""
            INSERT INTO marketplace_listing (
                item_inventory_id,
                listing_external_service_id,
                external_catalog_item_id,
                external_listing_id,
                external_listing_url,
                listing_status_code,
                title,
                description,
                private_notes,
                unit_price,
                currency_code,
                fixed_price,
                created_at,
                updated_at,
                published_at,
                ended_at,
                last_synchronized_at
            )
            VALUES (
                #{itemInventoryId},
                #{listingExternalServiceId},
                #{externalCatalogItemId},
                #{externalListingId},
                #{externalListingUrl},
                #{listingStatusCode},
                #{title},
                #{description},
                #{privateNotes},
                #{unitPrice},
                #{currencyCode},
                #{fixedPrice},
                COALESCE(#{createdAt}, CURRENT_TIMESTAMP),
                COALESCE(#{updatedAt}, CURRENT_TIMESTAMP),
                #{publishedAt},
                #{endedAt},
                #{lastSynchronizedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "marketplace_listing_id", keyProperty = "marketplaceListingId")
    int insert(MarketplaceListing marketplaceListing);

    @Update("""
            UPDATE marketplace_listing
            SET item_inventory_id = #{itemInventoryId},
                listing_external_service_id = #{listingExternalServiceId},
                external_catalog_item_id = #{externalCatalogItemId},
                external_listing_id = #{externalListingId},
                external_listing_url = #{externalListingUrl},
                listing_status_code = #{listingStatusCode},
                title = #{title},
                description = #{description},
                private_notes = #{privateNotes},
                unit_price = #{unitPrice},
                currency_code = #{currencyCode},
                fixed_price = #{fixedPrice},
                updated_at = CURRENT_TIMESTAMP,
                published_at = #{publishedAt},
                ended_at = #{endedAt},
                last_synchronized_at = #{lastSynchronizedAt}
            WHERE marketplace_listing_id = #{marketplaceListingId}
            """)
    int update(MarketplaceListing marketplaceListing);

    @Delete("DELETE FROM marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    int delete(Integer marketplaceListingId);

    @Insert("""
            INSERT INTO marketplace_listing (
                marketplace_listing_id,
                item_inventory_id,
                listing_external_service_id,
                external_catalog_item_id,
                external_listing_id,
                external_listing_url,
                listing_status_code,
                title,
                description,
                private_notes,
                unit_price,
                currency_code,
                fixed_price,
                created_at,
                updated_at,
                published_at,
                ended_at,
                last_synchronized_at
            )
            VALUES (
                #{marketplaceListingId},
                #{itemInventoryId},
                #{listingExternalServiceId},
                #{externalCatalogItemId},
                #{externalListingId},
                #{externalListingUrl},
                #{listingStatusCode},
                #{title},
                #{description},
                #{privateNotes},
                #{unitPrice},
                #{currencyCode},
                #{fixedPrice},
                COALESCE(#{createdAt}, CURRENT_TIMESTAMP),
                COALESCE(#{updatedAt}, CURRENT_TIMESTAMP),
                #{publishedAt},
                #{endedAt},
                #{lastSynchronizedAt}
            )
            ON DUPLICATE KEY UPDATE
                item_inventory_id = VALUES(item_inventory_id),
                listing_external_service_id = VALUES(listing_external_service_id),
                external_catalog_item_id = VALUES(external_catalog_item_id),
                external_listing_id = VALUES(external_listing_id),
                external_listing_url = VALUES(external_listing_url),
                listing_status_code = VALUES(listing_status_code),
                title = VALUES(title),
                description = VALUES(description),
                private_notes = VALUES(private_notes),
                unit_price = VALUES(unit_price),
                currency_code = VALUES(currency_code),
                fixed_price = VALUES(fixed_price),
                updated_at = CURRENT_TIMESTAMP,
                published_at = VALUES(published_at),
                ended_at = VALUES(ended_at),
                last_synchronized_at = VALUES(last_synchronized_at)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "marketplace_listing_id", keyProperty = "marketplaceListingId")
    int upsert(MarketplaceListing marketplaceListing);
}
