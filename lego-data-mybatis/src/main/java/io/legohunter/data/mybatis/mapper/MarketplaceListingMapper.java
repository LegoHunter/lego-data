package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingHydrationGap;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

public interface MarketplaceListingMapper {
    String ALL_COLUMNS = """
            ml.marketplace_listing_id,
            ml.item_inventory_id,
            ml.listing_external_service_id,
            ml.external_catalog_item_id,
            ml.external_listing_id,
            ml.external_listing_url,
            ml.listing_status_code,
            ml.title,
            ml.description,
            ml.private_notes,
            ml.unit_price,
            ml.currency_code,
            ml.fixed_price,
            ml.created_at,
            ml.updated_at,
            ml.published_at,
            ml.ended_at,
            ml.last_synchronized_at,
            eci.external_catalog_item_id AS eci_external_catalog_item_id,
            eci.external_service_id AS eci_external_service_id,
            eci.external_item_key AS eci_external_item_key,
            eci.external_unique_key AS eci_external_unique_key,
            eci.item_name AS eci_item_name,
            eci.item_type_code AS eci_item_type_code,
            eci.item_url AS eci_item_url,
            eci.year_released AS eci_year_released,
            es.external_service_id AS eci_es_external_service_id,
            es.service_code AS eci_es_service_code,
            es.display_name AS eci_es_display_name,
            es.service_url AS eci_es_service_url,
            es.external_service_type_id AS eci_es_external_service_type_id,
            est.external_service_type_id AS eci_es_est_external_service_type_id,
            est.external_service_type_name AS eci_es_est_external_service_type_name,
            est.external_service_type_description AS eci_es_est_external_service_type_description
            """;

    String FROM_CLAUSE = """
            FROM marketplace_listing ml
            LEFT JOIN external_catalog_item eci
                ON eci.external_catalog_item_id = ml.external_catalog_item_id
            LEFT JOIN external_service es
                ON es.external_service_id = eci.external_service_id
            LEFT JOIN external_service_type est
                ON est.external_service_type_id = es.external_service_type_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE)
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE + " WHERE ml.marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("marketplaceListingResultMap")
    Optional<MarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE + " WHERE ml.item_inventory_id = #{itemInventoryId}")
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findByItemInventoryId(Integer itemInventoryId);

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.listing_status_code = #{listingStatusCode}
              AND ml.external_catalog_item_id IS NOT NULL
            ORDER BY ml.marketplace_listing_id
            LIMIT #{limit}
            """)
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("limit") int limit,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Set<MarketplaceListing> findByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return findByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit,
                ALL_COLUMNS,
                FROM_CLAUSE
        );
    }

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.listing_status_code = #{listingStatusCode}
              AND ml.external_catalog_item_id IS NOT NULL
              AND NOT EXISTS (
                  SELECT 1
                  FROM pricing_crawl_work_item pcwi
                  WHERE pcwi.marketplace_listing_id = ml.marketplace_listing_id
                    AND (
                        pcwi.work_status_code IN (#{pendingStatusCode}, #{claimedStatusCode})
                        OR pcwi.next_attempt_at > #{asOf}
                    )
              )
            ORDER BY ml.marketplace_listing_id
            LIMIT #{limit}
            """)
    @ResultMap("marketplaceListingResultMap")
    Set<MarketplaceListing> findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("pendingStatusCode") String pendingStatusCode,
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("asOf") ZonedDateTime asOf,
            @Param("limit") int limit,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Set<MarketplaceListing> findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            String pendingStatusCode,
            String claimedStatusCode,
            ZonedDateTime asOf,
            int limit
    ) {
        return findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                pendingStatusCode,
                claimedStatusCode,
                asOf,
                limit,
                ALL_COLUMNS,
                FROM_CLAUSE
        );
    }

    Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("limit") int limit
    );

    Set<MarketplaceListing> findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("limit") int limit
    );

    @Select("""
            SELECT ml.marketplace_listing_id,
                   ml.external_listing_id,
                   eci.external_catalog_item_id,
                   eci.external_item_key,
                   eci.external_unique_key,
                   ii.item_inventory_id,
                   ii.uuid AS item_inventory_uuid,
                   ii.new_or_used,
                   ii.completeness
            FROM marketplace_listing ml
            JOIN external_catalog_item eci
              ON eci.external_catalog_item_id = ml.external_catalog_item_id
            JOIN item_inventory ii
              ON ii.item_inventory_id = ml.item_inventory_id
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.listing_status_code = #{listingStatusCode}
              AND (eci.external_unique_key IS NULL OR eci.external_unique_key = '')
            ORDER BY ml.marketplace_listing_id
            LIMIT #{limit}
            """)
    @Results(id = "pricingHydrationGapResultMap", value = {
            @Result(property = "marketplaceListingId", column = "marketplace_listing_id"),
            @Result(property = "externalListingId", column = "external_listing_id"),
            @Result(property = "externalCatalogItemId", column = "external_catalog_item_id"),
            @Result(property = "externalItemKey", column = "external_item_key"),
            @Result(property = "externalUniqueKey", column = "external_unique_key"),
            @Result(property = "itemInventoryId", column = "item_inventory_id"),
            @Result(property = "itemInventoryUuid", column = "item_inventory_uuid"),
            @Result(property = "newOrUsed", column = "new_or_used"),
            @Result(property = "completeness", column = "completeness")
    })
    Set<PricingHydrationGap> findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("limit") int limit
    );

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.external_listing_id = #{externalListingId}
            """)
    @ResultMap("marketplaceListingResultMap")
    Optional<MarketplaceListing> findByListingExternalServiceIdAndExternalListingId(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("externalListingId") String externalListingId,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<MarketplaceListing> findByListingExternalServiceIdAndExternalListingId(
            Integer listingExternalServiceId,
            String externalListingId
    ) {
        return findByListingExternalServiceIdAndExternalListingId(
                listingExternalServiceId,
                externalListingId,
                ALL_COLUMNS,
                FROM_CLAUSE
        );
    }

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
