package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.WantListItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * What each environment needs priced (HOMELAB-31). Environment database users may write only this
 * table in the shared schema, and each environment only touches rows with its own environment code.
 */
public interface WantListItemMapper {
    String ALL_COLUMNS = """
            environment_code,
            canonical_item_id,
            condition_code,
            completeness_code,
            first_wanted_at,
            last_confirmed_at
            """;

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.want_list_item
            WHERE environment_code = #{environmentCode}
            ORDER BY canonical_item_id, condition_code, completeness_code
            """)
    @ResultMap("wantListItemResultMap")
    List<WantListItem> findByEnvironmentCode(@Param("environmentCode") String environmentCode, @Param("columns") String columns);

    default List<WantListItem> findByEnvironmentCode(String environmentCode) {
        return findByEnvironmentCode(environmentCode, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.want_list_item
            WHERE canonical_item_id = #{canonicalItemId}
            ORDER BY environment_code, condition_code, completeness_code
            """)
    @ResultMap("wantListItemResultMap")
    List<WantListItem> findByCanonicalItemId(@Param("canonicalItemId") Integer canonicalItemId, @Param("columns") String columns);

    default List<WantListItem> findByCanonicalItemId(Integer canonicalItemId) {
        return findByCanonicalItemId(canonicalItemId, ALL_COLUMNS);
    }

    /**
     * Adds the row, or refreshes {@code last_confirmed_at} when it already exists. {@code first_wanted_at}
     * keeps the value from the first insert.
     */
    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.want_list_item (
                environment_code,
                canonical_item_id,
                condition_code,
                completeness_code,
                first_wanted_at,
                last_confirmed_at
            )
            VALUES (
                #{environmentCode},
                #{canonicalItemId},
                #{conditionCode},
                #{completenessCode},
                COALESCE(#{firstWantedAt}, CURRENT_TIMESTAMP),
                COALESCE(#{lastConfirmedAt}, CURRENT_TIMESTAMP)
            )
            ON DUPLICATE KEY UPDATE
                last_confirmed_at = VALUES(last_confirmed_at)
            """)
    void upsert(WantListItem wantListItem);

    @Delete("""
            DELETE FROM ${marketplacePricingSchema}.want_list_item
            WHERE environment_code = #{environmentCode}
              AND canonical_item_id = #{canonicalItemId}
              AND condition_code = #{conditionCode}
              AND completeness_code = #{completenessCode}
            """)
    int delete(WantListItem wantListItem);

    /** Removes the rows one environment has not confirmed since {@code cutoff}: items it no longer sells. */
    @Delete("""
            DELETE FROM ${marketplacePricingSchema}.want_list_item
            WHERE environment_code = #{environmentCode}
              AND last_confirmed_at < #{cutoff}
            """)
    int deleteByEnvironmentCodeAndLastConfirmedAtBefore(
            @Param("environmentCode") String environmentCode,
            @Param("cutoff") ZonedDateTime cutoff
    );
}
