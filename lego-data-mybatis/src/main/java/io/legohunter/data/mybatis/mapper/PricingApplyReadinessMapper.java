package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingApplyReadiness;
import io.legohunter.data.dto.PricingApplyReadinessReview;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface PricingApplyReadinessMapper {
    String ALL_COLUMNS = """
            pricing_apply_readiness_id,
            marketplace_listing_id,
            pricing_decision_id,
            pricing_snapshot_id,
            readiness_status_code,
            block_reason_code,
            current_price,
            proposed_price,
            delta_amount,
            delta_percent,
            minimum_required_delta,
            currency_code,
            confidence,
            comparable_count,
            evaluated_at,
            created_at,
            updated_at
            """;

    String REVIEW_COLUMNS = """
            par.pricing_apply_readiness_id,
            par.marketplace_listing_id,
            ml.external_listing_id,
            ml.listing_status_code,
            ml.fixed_price,
            ii.item_inventory_id,
            ii.uuid AS item_inventory_uuid,
            eci.external_catalog_item_id,
            eci.external_item_key,
            eci.external_unique_key,
            par.pricing_decision_id,
            par.pricing_snapshot_id,
            pd.algorithm_version,
            pd.decision_status_code,
            pd.reason_code AS decision_reason_code,
            pd.strategy_code,
            par.readiness_status_code,
            par.block_reason_code,
            par.current_price,
            par.proposed_price,
            par.delta_amount,
            par.delta_percent,
            par.minimum_required_delta,
            par.currency_code,
            par.confidence,
            par.comparable_count,
            pd.created_at AS decision_created_at,
            ps.captured_at AS snapshot_captured_at,
            par.evaluated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness")
    @ResultMap("pricingApplyReadinessResultMap")
    Set<PricingApplyReadiness> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness WHERE pricing_apply_readiness_id = #{pricingApplyReadinessId}")
    @ResultMap("pricingApplyReadinessResultMap")
    Optional<PricingApplyReadiness> findByPricingApplyReadinessId(Long pricingApplyReadinessId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness WHERE pricing_decision_id = #{pricingDecisionId}")
    @ResultMap("pricingApplyReadinessResultMap")
    Optional<PricingApplyReadiness> findByPricingDecisionId(Long pricingDecisionId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("pricingApplyReadinessResultMap")
    Set<PricingApplyReadiness> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness WHERE readiness_status_code = #{readinessStatusCode}")
    @ResultMap("pricingApplyReadinessResultMap")
    Set<PricingApplyReadiness> findByReadinessStatusCode(String readinessStatusCode);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_apply_readiness WHERE block_reason_code = #{blockReasonCode}")
    @ResultMap("pricingApplyReadinessResultMap")
    Set<PricingApplyReadiness> findByBlockReasonCode(String blockReasonCode);

    @Select("""
            SELECT ${columns}
            FROM pricing_apply_readiness
            WHERE marketplace_listing_id = #{marketplaceListingId}
            ORDER BY evaluated_at DESC, pricing_apply_readiness_id DESC
            LIMIT 1
            """)
    @ResultMap("pricingApplyReadinessResultMap")
    Optional<PricingApplyReadiness> findLatestByMarketplaceListingId(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("columns") String columns
    );

    default Optional<PricingApplyReadiness> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return findLatestByMarketplaceListingId(marketplaceListingId, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM pricing_apply_readiness par
            JOIN (
                SELECT marketplace_listing_id,
                       MAX(pricing_decision_id) AS pricing_decision_id
                FROM pricing_decision
                GROUP BY marketplace_listing_id
            ) latest_decision
              ON latest_decision.marketplace_listing_id = par.marketplace_listing_id
             AND latest_decision.pricing_decision_id = par.pricing_decision_id
            JOIN marketplace_listing ml
              ON ml.marketplace_listing_id = par.marketplace_listing_id
            JOIN item_inventory ii
              ON ii.item_inventory_id = ml.item_inventory_id
            LEFT JOIN external_catalog_item eci
              ON eci.external_catalog_item_id = ml.external_catalog_item_id
            JOIN pricing_decision pd
              ON pd.pricing_decision_id = par.pricing_decision_id
            LEFT JOIN pricing_snapshot ps
              ON ps.pricing_snapshot_id = par.pricing_snapshot_id
            WHERE (#{readinessStatusCode} IS NULL OR par.readiness_status_code = #{readinessStatusCode})
              AND (#{blockReasonCode} IS NULL OR par.block_reason_code = #{blockReasonCode})
            ORDER BY par.evaluated_at DESC,
                     par.pricing_apply_readiness_id DESC
            LIMIT #{limit}
            """)
    @ResultMap("pricingApplyReadinessReviewResultMap")
    Set<PricingApplyReadinessReview> findLatestReviews(
            @Param("readinessStatusCode") String readinessStatusCode,
            @Param("blockReasonCode") String blockReasonCode,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<PricingApplyReadinessReview> findLatestReviews(String readinessStatusCode, String blockReasonCode, int limit) {
        return findLatestReviews(readinessStatusCode, blockReasonCode, limit, REVIEW_COLUMNS);
    }

    default Set<PricingApplyReadinessReview> findLatestReadyToApplyReviews(int limit) {
        return findLatestReviews("READY_TO_APPLY", null, limit);
    }

    @Select("SELECT COUNT(*) FROM pricing_apply_readiness WHERE readiness_status_code = #{readinessStatusCode}")
    long countByReadinessStatusCode(String readinessStatusCode);

    @Select("SELECT COUNT(*) FROM pricing_apply_readiness WHERE block_reason_code = #{blockReasonCode}")
    long countByBlockReasonCode(String blockReasonCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_apply_readiness par
            JOIN (
                SELECT marketplace_listing_id,
                       MAX(pricing_decision_id) AS pricing_decision_id
                FROM pricing_decision
                GROUP BY marketplace_listing_id
            ) latest_decision
              ON latest_decision.marketplace_listing_id = par.marketplace_listing_id
             AND latest_decision.pricing_decision_id = par.pricing_decision_id
            WHERE par.readiness_status_code = #{readinessStatusCode}
            """)
    long countLatestByReadinessStatusCode(String readinessStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_apply_readiness par
            JOIN (
                SELECT marketplace_listing_id,
                       MAX(pricing_decision_id) AS pricing_decision_id
                FROM pricing_decision
                GROUP BY marketplace_listing_id
            ) latest_decision
              ON latest_decision.marketplace_listing_id = par.marketplace_listing_id
             AND latest_decision.pricing_decision_id = par.pricing_decision_id
            WHERE par.block_reason_code = #{blockReasonCode}
            """)
    long countLatestByBlockReasonCode(String blockReasonCode);

    @Insert("""
            INSERT INTO pricing_apply_readiness (
                marketplace_listing_id,
                pricing_decision_id,
                pricing_snapshot_id,
                readiness_status_code,
                block_reason_code,
                current_price,
                proposed_price,
                delta_amount,
                delta_percent,
                minimum_required_delta,
                currency_code,
                confidence,
                comparable_count,
                evaluated_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceListingId},
                #{pricingDecisionId},
                #{pricingSnapshotId},
                #{readinessStatusCode},
                #{blockReasonCode},
                #{currentPrice},
                #{proposedPrice},
                #{deltaAmount},
                #{deltaPercent},
                #{minimumRequiredDelta},
                #{currencyCode},
                #{confidence},
                #{comparableCount},
                COALESCE(#{evaluatedAt}, CURRENT_TIMESTAMP),
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_apply_readiness_id", keyProperty = "pricingApplyReadinessId")
    void insert(PricingApplyReadiness pricingApplyReadiness);

    @Update("""
            UPDATE pricing_apply_readiness
            SET marketplace_listing_id = #{marketplaceListingId},
                pricing_decision_id = #{pricingDecisionId},
                pricing_snapshot_id = #{pricingSnapshotId},
                readiness_status_code = #{readinessStatusCode},
                block_reason_code = #{blockReasonCode},
                current_price = #{currentPrice},
                proposed_price = #{proposedPrice},
                delta_amount = #{deltaAmount},
                delta_percent = #{deltaPercent},
                minimum_required_delta = #{minimumRequiredDelta},
                currency_code = #{currencyCode},
                confidence = #{confidence},
                comparable_count = #{comparableCount},
                evaluated_at = #{evaluatedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE pricing_apply_readiness_id = #{pricingApplyReadinessId}
            """)
    int update(PricingApplyReadiness pricingApplyReadiness);

    @Insert("""
            INSERT INTO pricing_apply_readiness (
                pricing_apply_readiness_id,
                marketplace_listing_id,
                pricing_decision_id,
                pricing_snapshot_id,
                readiness_status_code,
                block_reason_code,
                current_price,
                proposed_price,
                delta_amount,
                delta_percent,
                minimum_required_delta,
                currency_code,
                confidence,
                comparable_count,
                evaluated_at,
                created_at,
                updated_at
            )
            VALUES (
                #{pricingApplyReadinessId},
                #{marketplaceListingId},
                #{pricingDecisionId},
                #{pricingSnapshotId},
                #{readinessStatusCode},
                #{blockReasonCode},
                #{currentPrice},
                #{proposedPrice},
                #{deltaAmount},
                #{deltaPercent},
                #{minimumRequiredDelta},
                #{currencyCode},
                #{confidence},
                #{comparableCount},
                COALESCE(#{evaluatedAt}, CURRENT_TIMESTAMP),
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_listing_id = VALUES(marketplace_listing_id),
                pricing_snapshot_id = VALUES(pricing_snapshot_id),
                readiness_status_code = VALUES(readiness_status_code),
                block_reason_code = VALUES(block_reason_code),
                current_price = VALUES(current_price),
                proposed_price = VALUES(proposed_price),
                delta_amount = VALUES(delta_amount),
                delta_percent = VALUES(delta_percent),
                minimum_required_delta = VALUES(minimum_required_delta),
                currency_code = VALUES(currency_code),
                confidence = VALUES(confidence),
                comparable_count = VALUES(comparable_count),
                evaluated_at = VALUES(evaluated_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_apply_readiness_id", keyProperty = "pricingApplyReadinessId")
    void upsert(PricingApplyReadiness pricingApplyReadiness);

    @Delete("DELETE FROM pricing_apply_readiness WHERE pricing_apply_readiness_id = #{pricingApplyReadinessId}")
    int delete(Long pricingApplyReadinessId);
}
