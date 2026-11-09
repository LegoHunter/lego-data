package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketSnapshot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Market snapshots (HOMELAB-31): one row per successful fetch, never updated. The rows for one item
 * and condition, in {@code captured_at} order, are its price history.
 */
public interface MarketSnapshotMapper {
    String ALL_COLUMNS = """
            market_snapshot_id,
            crawl_target_id,
            marketplace_code,
            canonical_item_id,
            query_condition_code,
            marketplace_item_key,
            marketplace_unique_key,
            request_url,
            request_parameters,
            raw_payload_hash,
            listing_count,
            captured_at,
            source_environment_code,
            source_pricing_snapshot_id,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.market_snapshot WHERE market_snapshot_id = #{marketSnapshotId}")
    @ResultMap("marketSnapshotResultMap")
    Optional<MarketSnapshot> findByMarketSnapshotId(Long marketSnapshotId);

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.market_snapshot
            WHERE marketplace_code = #{marketplaceCode}
              AND canonical_item_id = #{canonicalItemId}
              AND query_condition_code = #{queryConditionCode}
            ORDER BY captured_at DESC, market_snapshot_id DESC
            LIMIT 1
            """)
    @ResultMap("marketSnapshotResultMap")
    Optional<MarketSnapshot> findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            @Param("marketplaceCode") String marketplaceCode,
            @Param("canonicalItemId") Integer canonicalItemId,
            @Param("queryConditionCode") String queryConditionCode,
            @Param("columns") String columns
    );

    default Optional<MarketSnapshot> findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            String marketplaceCode,
            Integer canonicalItemId,
            String queryConditionCode
    ) {
        return findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
                marketplaceCode, canonicalItemId, queryConditionCode, ALL_COLUMNS);
    }

    /** Price history: every snapshot of one item and condition captured in [from, to), oldest first. */
    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.market_snapshot
            WHERE canonical_item_id = #{canonicalItemId}
              AND query_condition_code = #{queryConditionCode}
              AND captured_at >= #{from}
              AND captured_at < #{to}
            ORDER BY captured_at, market_snapshot_id
            """)
    @ResultMap("marketSnapshotResultMap")
    List<MarketSnapshot> findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
            @Param("canonicalItemId") Integer canonicalItemId,
            @Param("queryConditionCode") String queryConditionCode,
            @Param("from") ZonedDateTime from,
            @Param("to") ZonedDateTime to,
            @Param("columns") String columns
    );

    default List<MarketSnapshot> findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
            Integer canonicalItemId,
            String queryConditionCode,
            ZonedDateTime from,
            ZonedDateTime to
    ) {
        return findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
                canonicalItemId, queryConditionCode, from, to, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.market_snapshot (
                crawl_target_id,
                marketplace_code,
                canonical_item_id,
                query_condition_code,
                marketplace_item_key,
                marketplace_unique_key,
                request_url,
                request_parameters,
                raw_payload_hash,
                listing_count,
                captured_at,
                source_environment_code,
                source_pricing_snapshot_id
            )
            VALUES (
                #{crawlTargetId},
                #{marketplaceCode},
                #{canonicalItemId},
                #{queryConditionCode},
                #{marketplaceItemKey},
                #{marketplaceUniqueKey},
                #{requestUrl},
                #{requestParameters},
                #{rawPayloadHash},
                #{listingCount},
                COALESCE(#{capturedAt}, CURRENT_TIMESTAMP),
                #{sourceEnvironmentCode},
                #{sourcePricingSnapshotId}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "market_snapshot_id", keyProperty = "marketSnapshotId")
    void insert(MarketSnapshot marketSnapshot);
}
