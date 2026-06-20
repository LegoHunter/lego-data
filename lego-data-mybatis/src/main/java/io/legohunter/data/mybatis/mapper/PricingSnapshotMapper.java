package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingSnapshot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface PricingSnapshotMapper {
    String ALL_COLUMNS = """
            pricing_snapshot_id,
            pricing_crawl_work_item_id,
            marketplace_listing_id,
            external_catalog_item_id,
            source_external_service_id,
            source_item_key,
            source_unique_key,
            item_condition_code,
            completeness_code,
            source_request_url,
            source_request_parameters,
            raw_payload_hash,
            comparable_count,
            captured_at,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot")
    @ResultMap("pricingSnapshotResultMap")
    Set<PricingSnapshot> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot WHERE pricing_snapshot_id = #{pricingSnapshotId}")
    @ResultMap("pricingSnapshotResultMap")
    Optional<PricingSnapshot> findByPricingSnapshotId(Long pricingSnapshotId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("pricingSnapshotResultMap")
    Set<PricingSnapshot> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot WHERE external_catalog_item_id = #{externalCatalogItemId}")
    @ResultMap("pricingSnapshotResultMap")
    Set<PricingSnapshot> findByExternalCatalogItemId(Integer externalCatalogItemId);

    @Select("""
            SELECT ${columns}
            FROM pricing_snapshot
            WHERE marketplace_listing_id = #{marketplaceListingId}
            ORDER BY captured_at DESC, pricing_snapshot_id DESC
            LIMIT 1
            """)
    @ResultMap("pricingSnapshotResultMap")
    Optional<PricingSnapshot> findLatestByMarketplaceListingId(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("columns") String columns
    );

    default Optional<PricingSnapshot> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return findLatestByMarketplaceListingId(marketplaceListingId, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM pricing_snapshot
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND item_condition_code = #{itemConditionCode}
              AND (
                    completeness_code = #{completenessCode}
                    OR (completeness_code IS NULL AND #{completenessCode} IS NULL)
                  )
            ORDER BY captured_at DESC, pricing_snapshot_id DESC
            LIMIT 1
            """)
    @ResultMap("pricingSnapshotResultMap")
    Optional<PricingSnapshot> findLatestByMarketplaceListingIdAndConditionAndCompleteness(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("itemConditionCode") String itemConditionCode,
            @Param("completenessCode") String completenessCode,
            @Param("columns") String columns
    );

    default Optional<PricingSnapshot> findLatestByMarketplaceListingIdAndConditionAndCompleteness(
            Integer marketplaceListingId,
            String itemConditionCode,
            String completenessCode
    ) {
        return findLatestByMarketplaceListingIdAndConditionAndCompleteness(
                marketplaceListingId,
                itemConditionCode,
                completenessCode,
                ALL_COLUMNS
        );
    }

    @Select("""
            SELECT ${columns}
            FROM pricing_snapshot
            WHERE external_catalog_item_id = #{externalCatalogItemId}
              AND item_condition_code = #{itemConditionCode}
              AND (
                    completeness_code = #{completenessCode}
                    OR (completeness_code IS NULL AND #{completenessCode} IS NULL)
                  )
            ORDER BY captured_at DESC, pricing_snapshot_id DESC
            LIMIT 1
            """)
    @ResultMap("pricingSnapshotResultMap")
    Optional<PricingSnapshot> findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
            @Param("externalCatalogItemId") Integer externalCatalogItemId,
            @Param("itemConditionCode") String itemConditionCode,
            @Param("completenessCode") String completenessCode,
            @Param("columns") String columns
    );

    default Optional<PricingSnapshot> findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
            Integer externalCatalogItemId,
            String itemConditionCode,
            String completenessCode
    ) {
        return findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
                externalCatalogItemId,
                itemConditionCode,
                completenessCode,
                ALL_COLUMNS
        );
    }

    @Insert("""
            INSERT INTO pricing_snapshot (
                pricing_crawl_work_item_id,
                marketplace_listing_id,
                external_catalog_item_id,
                source_external_service_id,
                source_item_key,
                source_unique_key,
                item_condition_code,
                completeness_code,
                source_request_url,
                source_request_parameters,
                raw_payload_hash,
                comparable_count,
                captured_at,
                created_at
            )
            VALUES (
                #{pricingCrawlWorkItemId},
                #{marketplaceListingId},
                #{externalCatalogItemId},
                #{sourceExternalServiceId},
                #{sourceItemKey},
                #{sourceUniqueKey},
                #{itemConditionCode},
                #{completenessCode},
                #{sourceRequestUrl},
                #{sourceRequestParameters},
                #{rawPayloadHash},
                #{comparableCount},
                COALESCE(#{capturedAt}, CURRENT_TIMESTAMP),
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_snapshot_id", keyProperty = "pricingSnapshotId")
    void insert(PricingSnapshot pricingSnapshot);

    @Update("""
            UPDATE pricing_snapshot
            SET pricing_crawl_work_item_id = #{pricingCrawlWorkItemId},
                marketplace_listing_id = #{marketplaceListingId},
                external_catalog_item_id = #{externalCatalogItemId},
                source_external_service_id = #{sourceExternalServiceId},
                source_item_key = #{sourceItemKey},
                source_unique_key = #{sourceUniqueKey},
                item_condition_code = #{itemConditionCode},
                completeness_code = #{completenessCode},
                source_request_url = #{sourceRequestUrl},
                source_request_parameters = #{sourceRequestParameters},
                raw_payload_hash = #{rawPayloadHash},
                comparable_count = #{comparableCount},
                captured_at = #{capturedAt}
            WHERE pricing_snapshot_id = #{pricingSnapshotId}
            """)
    int update(PricingSnapshot pricingSnapshot);

    @Delete("DELETE FROM pricing_snapshot WHERE pricing_snapshot_id = #{pricingSnapshotId}")
    int delete(Long pricingSnapshotId);

    @Insert("""
            INSERT INTO pricing_snapshot (
                pricing_snapshot_id,
                pricing_crawl_work_item_id,
                marketplace_listing_id,
                external_catalog_item_id,
                source_external_service_id,
                source_item_key,
                source_unique_key,
                item_condition_code,
                completeness_code,
                source_request_url,
                source_request_parameters,
                raw_payload_hash,
                comparable_count,
                captured_at,
                created_at
            )
            VALUES (
                #{pricingSnapshotId},
                #{pricingCrawlWorkItemId},
                #{marketplaceListingId},
                #{externalCatalogItemId},
                #{sourceExternalServiceId},
                #{sourceItemKey},
                #{sourceUniqueKey},
                #{itemConditionCode},
                #{completenessCode},
                #{sourceRequestUrl},
                #{sourceRequestParameters},
                #{rawPayloadHash},
                #{comparableCount},
                COALESCE(#{capturedAt}, CURRENT_TIMESTAMP),
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                pricing_crawl_work_item_id = VALUES(pricing_crawl_work_item_id),
                marketplace_listing_id = VALUES(marketplace_listing_id),
                external_catalog_item_id = VALUES(external_catalog_item_id),
                source_external_service_id = VALUES(source_external_service_id),
                source_item_key = VALUES(source_item_key),
                source_unique_key = VALUES(source_unique_key),
                item_condition_code = VALUES(item_condition_code),
                completeness_code = VALUES(completeness_code),
                source_request_url = VALUES(source_request_url),
                source_request_parameters = VALUES(source_request_parameters),
                raw_payload_hash = VALUES(raw_payload_hash),
                comparable_count = VALUES(comparable_count),
                captured_at = VALUES(captured_at)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_snapshot_id", keyProperty = "pricingSnapshotId")
    void upsert(PricingSnapshot pricingSnapshot);
}
