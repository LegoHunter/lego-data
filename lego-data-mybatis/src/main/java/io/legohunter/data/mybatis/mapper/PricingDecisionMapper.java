package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingDecisionReview;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface PricingDecisionMapper {
    String ALL_COLUMNS = """
            pricing_decision_id,
            marketplace_listing_id,
            pricing_snapshot_id,
            algorithm_version,
            decision_status_code,
            reason_code,
            strategy_code,
            computed_price,
            final_price,
            previous_price,
            currency_code,
            comparable_count,
            confidence,
            source_summary_json,
            notes,
            applied_at,
            created_at
            """;

    String REVIEW_COLUMNS = """
            ml.marketplace_listing_id,
            ml.external_listing_id,
            ml.listing_status_code,
            ml.unit_price AS current_unit_price,
            ml.currency_code AS current_currency_code,
            ml.fixed_price,
            ii.item_inventory_id,
            ii.uuid AS item_inventory_uuid,
            ii.new_or_used,
            ii.completeness,
            eci.external_catalog_item_id,
            eci.external_item_key,
            eci.external_unique_key,
            pd.pricing_decision_id,
            pd.pricing_snapshot_id,
            pd.algorithm_version,
            pd.decision_status_code,
            pd.reason_code,
            pd.strategy_code,
            pd.computed_price,
            pd.final_price,
            pd.previous_price,
            pd.currency_code,
            pd.currency_code AS decision_currency_code,
            pd.comparable_count,
            pd.confidence,
            pd.notes,
            pd.created_at AS decision_created_at,
            pd.applied_at,
            ps.captured_at AS snapshot_captured_at,
            ps.comparable_count AS snapshot_comparable_count
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_decision")
    @ResultMap("pricingDecisionResultMap")
    Set<PricingDecision> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_decision WHERE pricing_decision_id = #{pricingDecisionId}")
    @ResultMap("pricingDecisionResultMap")
    Optional<PricingDecision> findByPricingDecisionId(Long pricingDecisionId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_decision WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("pricingDecisionResultMap")
    Set<PricingDecision> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_decision WHERE decision_status_code = #{decisionStatusCode}")
    @ResultMap("pricingDecisionResultMap")
    Set<PricingDecision> findByDecisionStatusCode(String decisionStatusCode);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_decision WHERE reason_code = #{reasonCode}")
    @ResultMap("pricingDecisionResultMap")
    Set<PricingDecision> findByReasonCode(String reasonCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_decision pd
            JOIN (
                SELECT marketplace_listing_id,
                       MAX(pricing_decision_id) AS pricing_decision_id
                FROM pricing_decision
                GROUP BY marketplace_listing_id
            ) latest
              ON latest.pricing_decision_id = pd.pricing_decision_id
            WHERE pd.decision_status_code = #{decisionStatusCode}
            """)
    long countLatestByDecisionStatusCode(String decisionStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_decision pd
            JOIN (
                SELECT marketplace_listing_id,
                       MAX(pricing_decision_id) AS pricing_decision_id
                FROM pricing_decision
                GROUP BY marketplace_listing_id
            ) latest
              ON latest.pricing_decision_id = pd.pricing_decision_id
            WHERE pd.decision_status_code = #{decisionStatusCode}
              AND pd.applied_at IS NULL
            """)
    long countLatestUnappliedByDecisionStatusCode(String decisionStatusCode);

    @Select("""
            SELECT ${columns}
            FROM pricing_decision
            WHERE marketplace_listing_id = #{marketplaceListingId}
            ORDER BY created_at DESC, pricing_decision_id DESC
            LIMIT 1
            """)
    @ResultMap("pricingDecisionResultMap")
    Optional<PricingDecision> findLatestByMarketplaceListingId(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("columns") String columns
    );

    default Optional<PricingDecision> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return findLatestByMarketplaceListingId(marketplaceListingId, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM marketplace_listing ml
            JOIN item_inventory ii
              ON ii.item_inventory_id = ml.item_inventory_id
            JOIN external_catalog_item eci
              ON eci.external_catalog_item_id = ml.external_catalog_item_id
            LEFT JOIN (
                SELECT pd.*
                FROM pricing_decision pd
                JOIN (
                    SELECT marketplace_listing_id,
                           MAX(pricing_decision_id) AS pricing_decision_id
                    FROM pricing_decision
                    GROUP BY marketplace_listing_id
                ) latest
                  ON latest.pricing_decision_id = pd.pricing_decision_id
            ) pd
              ON pd.marketplace_listing_id = ml.marketplace_listing_id
            LEFT JOIN pricing_snapshot ps
              ON ps.pricing_snapshot_id = pd.pricing_snapshot_id
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.listing_status_code = #{listingStatusCode}
            ORDER BY ml.marketplace_listing_id
            LIMIT #{limit}
            """)
    @ResultMap("pricingDecisionReviewResultMap")
    Set<PricingDecisionReview> findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<PricingDecisionReview> findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit,
                REVIEW_COLUMNS
        );
    }

    @Select("""
            SELECT ${columns}
            FROM marketplace_listing ml
            JOIN item_inventory ii
              ON ii.item_inventory_id = ml.item_inventory_id
            JOIN external_catalog_item eci
              ON eci.external_catalog_item_id = ml.external_catalog_item_id
            JOIN (
                SELECT pd.*
                FROM pricing_decision pd
                JOIN (
                    SELECT marketplace_listing_id,
                           MAX(pricing_decision_id) AS pricing_decision_id
                    FROM pricing_decision
                    GROUP BY marketplace_listing_id
                ) latest
                  ON latest.pricing_decision_id = pd.pricing_decision_id
            ) pd
              ON pd.marketplace_listing_id = ml.marketplace_listing_id
            LEFT JOIN pricing_snapshot ps
              ON ps.pricing_snapshot_id = pd.pricing_snapshot_id
            WHERE ml.listing_external_service_id = #{listingExternalServiceId}
              AND ml.listing_status_code = #{listingStatusCode}
              AND pd.decision_status_code = #{decisionStatusCode}
              AND pd.applied_at IS NULL
            ORDER BY pd.created_at DESC,
                     pd.pricing_decision_id DESC
            LIMIT #{limit}
            """)
    @ResultMap("pricingDecisionReviewResultMap")
    Set<PricingDecisionReview> findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
            @Param("listingExternalServiceId") Integer listingExternalServiceId,
            @Param("listingStatusCode") String listingStatusCode,
            @Param("decisionStatusCode") String decisionStatusCode,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<PricingDecisionReview> findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            String decisionStatusCode,
            int limit
    ) {
        return findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                decisionStatusCode,
                limit,
                REVIEW_COLUMNS
        );
    }

    @Insert("""
            INSERT INTO pricing_decision (
                marketplace_listing_id,
                pricing_snapshot_id,
                algorithm_version,
                decision_status_code,
                reason_code,
                strategy_code,
                computed_price,
                final_price,
                previous_price,
                currency_code,
                comparable_count,
                confidence,
                source_summary_json,
                notes,
                applied_at,
                created_at
            )
            VALUES (
                #{marketplaceListingId},
                #{pricingSnapshotId},
                #{algorithmVersion},
                #{decisionStatusCode},
                #{reasonCode},
                #{strategyCode},
                #{computedPrice},
                #{finalPrice},
                #{previousPrice},
                #{currencyCode},
                #{comparableCount},
                #{confidence},
                #{sourceSummaryJson},
                #{notes},
                #{appliedAt},
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_decision_id", keyProperty = "pricingDecisionId")
    void insert(PricingDecision pricingDecision);

    @Update("""
            UPDATE pricing_decision
            SET marketplace_listing_id = #{marketplaceListingId},
                pricing_snapshot_id = #{pricingSnapshotId},
                algorithm_version = #{algorithmVersion},
                decision_status_code = #{decisionStatusCode},
                reason_code = #{reasonCode},
                strategy_code = #{strategyCode},
                computed_price = #{computedPrice},
                final_price = #{finalPrice},
                previous_price = #{previousPrice},
                currency_code = #{currencyCode},
                comparable_count = #{comparableCount},
                confidence = #{confidence},
                source_summary_json = #{sourceSummaryJson},
                notes = #{notes},
                applied_at = #{appliedAt}
            WHERE pricing_decision_id = #{pricingDecisionId}
            """)
    int update(PricingDecision pricingDecision);

    @Delete("DELETE FROM pricing_decision WHERE pricing_decision_id = #{pricingDecisionId}")
    int delete(Long pricingDecisionId);

    @Insert("""
            INSERT INTO pricing_decision (
                pricing_decision_id,
                marketplace_listing_id,
                pricing_snapshot_id,
                algorithm_version,
                decision_status_code,
                reason_code,
                strategy_code,
                computed_price,
                final_price,
                previous_price,
                currency_code,
                comparable_count,
                confidence,
                source_summary_json,
                notes,
                applied_at,
                created_at
            )
            VALUES (
                #{pricingDecisionId},
                #{marketplaceListingId},
                #{pricingSnapshotId},
                #{algorithmVersion},
                #{decisionStatusCode},
                #{reasonCode},
                #{strategyCode},
                #{computedPrice},
                #{finalPrice},
                #{previousPrice},
                #{currencyCode},
                #{comparableCount},
                #{confidence},
                #{sourceSummaryJson},
                #{notes},
                #{appliedAt},
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_listing_id = VALUES(marketplace_listing_id),
                pricing_snapshot_id = VALUES(pricing_snapshot_id),
                algorithm_version = VALUES(algorithm_version),
                decision_status_code = VALUES(decision_status_code),
                reason_code = VALUES(reason_code),
                strategy_code = VALUES(strategy_code),
                computed_price = VALUES(computed_price),
                final_price = VALUES(final_price),
                previous_price = VALUES(previous_price),
                currency_code = VALUES(currency_code),
                comparable_count = VALUES(comparable_count),
                confidence = VALUES(confidence),
                source_summary_json = VALUES(source_summary_json),
                notes = VALUES(notes),
                applied_at = VALUES(applied_at)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_decision_id", keyProperty = "pricingDecisionId")
    void upsert(PricingDecision pricingDecision);
}
