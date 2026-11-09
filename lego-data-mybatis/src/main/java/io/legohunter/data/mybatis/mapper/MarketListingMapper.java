package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketListing;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/** Competitor lots captured in market snapshots (HOMELAB-31). Never updated. */
public interface MarketListingMapper {
    String ALL_COLUMNS = """
            market_listing_id,
            market_snapshot_id,
            external_listing_id,
            seller_key,
            seller_name,
            seller_country_code,
            seller_feedback_score,
            raw_condition_code,
            raw_completeness_code,
            condition_code,
            completeness_code,
            match_confidence,
            listing_format_code,
            quantity_available,
            item_price,
            shipping_price,
            total_price,
            currency_code,
            native_price,
            native_currency_code,
            description,
            source_listing_payload,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.market_listing WHERE market_listing_id = #{marketListingId}")
    @ResultMap("marketListingResultMap")
    Optional<MarketListing> findByMarketListingId(Long marketListingId);

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.market_listing
            WHERE market_snapshot_id = #{marketSnapshotId}
            ORDER BY market_listing_id
            """)
    @ResultMap("marketListingResultMap")
    List<MarketListing> findByMarketSnapshotId(@Param("marketSnapshotId") Long marketSnapshotId, @Param("columns") String columns);

    default List<MarketListing> findByMarketSnapshotId(Long marketSnapshotId) {
        return findByMarketSnapshotId(marketSnapshotId, ALL_COLUMNS);
    }

    /**
     * The lots in one snapshot that compete in one sub-condition market: same canonical condition and
     * completeness. Cheapest first.
     */
    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.market_listing
            WHERE market_snapshot_id = #{marketSnapshotId}
              AND condition_code = #{conditionCode}
              AND completeness_code = #{completenessCode}
            ORDER BY item_price, market_listing_id
            """)
    @ResultMap("marketListingResultMap")
    List<MarketListing> findComparablesByMarketSnapshotId(
            @Param("marketSnapshotId") Long marketSnapshotId,
            @Param("conditionCode") String conditionCode,
            @Param("completenessCode") String completenessCode,
            @Param("columns") String columns
    );

    default List<MarketListing> findComparablesByMarketSnapshotId(Long marketSnapshotId, String conditionCode, String completenessCode) {
        return findComparablesByMarketSnapshotId(marketSnapshotId, conditionCode, completenessCode, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.market_listing (
                market_snapshot_id,
                external_listing_id,
                seller_key,
                seller_name,
                seller_country_code,
                seller_feedback_score,
                raw_condition_code,
                raw_completeness_code,
                condition_code,
                completeness_code,
                match_confidence,
                listing_format_code,
                quantity_available,
                item_price,
                shipping_price,
                total_price,
                currency_code,
                native_price,
                native_currency_code,
                description,
                source_listing_payload
            )
            VALUES (
                #{marketSnapshotId},
                #{externalListingId},
                #{sellerKey},
                #{sellerName},
                #{sellerCountryCode},
                #{sellerFeedbackScore},
                #{rawConditionCode},
                #{rawCompletenessCode},
                #{conditionCode},
                #{completenessCode},
                #{matchConfidence},
                COALESCE(#{listingFormatCode}, 'FIXED_PRICE'),
                #{quantityAvailable},
                #{itemPrice},
                #{shippingPrice},
                #{totalPrice},
                #{currencyCode},
                #{nativePrice},
                #{nativeCurrencyCode},
                #{description},
                #{sourceListingPayload}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "market_listing_id", keyProperty = "marketListingId")
    void insert(MarketListing marketListing);
}
