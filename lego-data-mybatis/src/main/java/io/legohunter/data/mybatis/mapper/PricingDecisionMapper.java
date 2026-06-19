package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingDecision;
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
