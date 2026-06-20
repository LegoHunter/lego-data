package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingSnapshotListing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PricingSnapshotListingMapper {
    String ALL_COLUMNS = """
            pricing_snapshot_listing_id,
            pricing_snapshot_id,
            external_listing_id,
            seller_name,
            seller_country_code,
            item_condition_code,
            completeness_code,
            quantity_available,
            unit_price,
            currency_code,
            description,
            extended_description,
            source_listing_payload,
            created_at
            """;
    String QUALIFIED_COLUMNS = """
            psl.pricing_snapshot_listing_id,
            psl.pricing_snapshot_id,
            psl.external_listing_id,
            psl.seller_name,
            psl.seller_country_code,
            psl.item_condition_code,
            psl.completeness_code,
            psl.quantity_available,
            psl.unit_price,
            psl.currency_code,
            psl.description,
            psl.extended_description,
            psl.source_listing_payload,
            psl.created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot_listing")
    @ResultMap("pricingSnapshotListingResultMap")
    Set<PricingSnapshotListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot_listing WHERE pricing_snapshot_listing_id = #{pricingSnapshotListingId}")
    @ResultMap("pricingSnapshotListingResultMap")
    Optional<PricingSnapshotListing> findByPricingSnapshotListingId(Long pricingSnapshotListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_snapshot_listing WHERE pricing_snapshot_id = #{pricingSnapshotId}")
    @ResultMap("pricingSnapshotListingResultMap")
    Set<PricingSnapshotListing> findByPricingSnapshotId(Long pricingSnapshotId);

    @Select("""
            SELECT ${columns}
            FROM pricing_snapshot_listing
            WHERE pricing_snapshot_id = #{pricingSnapshotId}
              AND external_listing_id = #{externalListingId}
            """)
    @ResultMap("pricingSnapshotListingResultMap")
    Optional<PricingSnapshotListing> findByPricingSnapshotIdAndExternalListingId(
            @Param("pricingSnapshotId") Long pricingSnapshotId,
            @Param("externalListingId") String externalListingId,
            @Param("columns") String columns
    );

    default Optional<PricingSnapshotListing> findByPricingSnapshotIdAndExternalListingId(Long pricingSnapshotId, String externalListingId) {
        return findByPricingSnapshotIdAndExternalListingId(pricingSnapshotId, externalListingId, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM pricing_snapshot_listing psl
            JOIN pricing_snapshot ps
              ON ps.pricing_snapshot_id = psl.pricing_snapshot_id
            WHERE psl.pricing_snapshot_id = #{pricingSnapshotId}
              AND psl.item_condition_code = ps.item_condition_code
              AND (
                    psl.completeness_code = ps.completeness_code
                    OR (psl.completeness_code IS NULL AND ps.completeness_code IS NULL)
                  )
            ORDER BY psl.unit_price, psl.pricing_snapshot_listing_id
            """)
    @ResultMap("pricingSnapshotListingResultMap")
    List<PricingSnapshotListing> findExactComparablesByPricingSnapshotId(
            @Param("pricingSnapshotId") Long pricingSnapshotId,
            @Param("columns") String columns
    );

    default List<PricingSnapshotListing> findExactComparablesByPricingSnapshotId(Long pricingSnapshotId) {
        return findExactComparablesByPricingSnapshotId(pricingSnapshotId, QUALIFIED_COLUMNS);
    }

    @Insert("""
            INSERT INTO pricing_snapshot_listing (
                pricing_snapshot_id,
                external_listing_id,
                seller_name,
                seller_country_code,
                item_condition_code,
                completeness_code,
                quantity_available,
                unit_price,
                currency_code,
                description,
                extended_description,
                source_listing_payload,
                created_at
            )
            VALUES (
                #{pricingSnapshotId},
                #{externalListingId},
                #{sellerName},
                #{sellerCountryCode},
                #{itemConditionCode},
                #{completenessCode},
                #{quantityAvailable},
                #{unitPrice},
                #{currencyCode},
                #{description},
                #{extendedDescription},
                #{sourceListingPayload},
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_snapshot_listing_id", keyProperty = "pricingSnapshotListingId")
    void insert(PricingSnapshotListing pricingSnapshotListing);

    @Update("""
            UPDATE pricing_snapshot_listing
            SET pricing_snapshot_id = #{pricingSnapshotId},
                external_listing_id = #{externalListingId},
                seller_name = #{sellerName},
                seller_country_code = #{sellerCountryCode},
                item_condition_code = #{itemConditionCode},
                completeness_code = #{completenessCode},
                quantity_available = #{quantityAvailable},
                unit_price = #{unitPrice},
                currency_code = #{currencyCode},
                description = #{description},
                extended_description = #{extendedDescription},
                source_listing_payload = #{sourceListingPayload}
            WHERE pricing_snapshot_listing_id = #{pricingSnapshotListingId}
            """)
    int update(PricingSnapshotListing pricingSnapshotListing);

    @Delete("DELETE FROM pricing_snapshot_listing WHERE pricing_snapshot_listing_id = #{pricingSnapshotListingId}")
    int delete(Long pricingSnapshotListingId);

    @Insert("""
            INSERT INTO pricing_snapshot_listing (
                pricing_snapshot_listing_id,
                pricing_snapshot_id,
                external_listing_id,
                seller_name,
                seller_country_code,
                item_condition_code,
                completeness_code,
                quantity_available,
                unit_price,
                currency_code,
                description,
                extended_description,
                source_listing_payload,
                created_at
            )
            VALUES (
                #{pricingSnapshotListingId},
                #{pricingSnapshotId},
                #{externalListingId},
                #{sellerName},
                #{sellerCountryCode},
                #{itemConditionCode},
                #{completenessCode},
                #{quantityAvailable},
                #{unitPrice},
                #{currencyCode},
                #{description},
                #{extendedDescription},
                #{sourceListingPayload},
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                pricing_snapshot_id = VALUES(pricing_snapshot_id),
                external_listing_id = VALUES(external_listing_id),
                seller_name = VALUES(seller_name),
                seller_country_code = VALUES(seller_country_code),
                item_condition_code = VALUES(item_condition_code),
                completeness_code = VALUES(completeness_code),
                quantity_available = VALUES(quantity_available),
                unit_price = VALUES(unit_price),
                currency_code = VALUES(currency_code),
                description = VALUES(description),
                extended_description = VALUES(extended_description),
                source_listing_payload = VALUES(source_listing_payload)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_snapshot_listing_id", keyProperty = "pricingSnapshotListingId")
    void upsert(PricingSnapshotListing pricingSnapshotListing);
}
