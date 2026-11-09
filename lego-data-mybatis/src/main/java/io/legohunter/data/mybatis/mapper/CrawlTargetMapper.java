package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CrawlTarget;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The crawl queue (HOMELAB-31): one stable row per marketplace, item and query condition, updated in
 * place by each crawl.
 */
public interface CrawlTargetMapper {
    String STATUS_IDLE = "IDLE";
    String STATUS_CLAIMED = "CLAIMED";
    String STATUS_DISABLED = "DISABLED";

    String ALL_COLUMNS = """
            crawl_target_id,
            marketplace_code,
            canonical_item_id,
            query_condition_code,
            target_status_code,
            next_due_at,
            claimed_at,
            claim_expires_at,
            claimed_by,
            last_attempt_at,
            last_success_at,
            last_market_snapshot_id,
            consecutive_failure_count,
            last_error_message,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.crawl_target WHERE crawl_target_id = #{crawlTargetId}")
    @ResultMap("crawlTargetResultMap")
    Optional<CrawlTarget> findByCrawlTargetId(Long crawlTargetId);

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.crawl_target
            WHERE marketplace_code = #{marketplaceCode}
              AND canonical_item_id = #{canonicalItemId}
              AND query_condition_code = #{queryConditionCode}
            """)
    @ResultMap("crawlTargetResultMap")
    Optional<CrawlTarget> findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            @Param("marketplaceCode") String marketplaceCode,
            @Param("canonicalItemId") Integer canonicalItemId,
            @Param("queryConditionCode") String queryConditionCode,
            @Param("columns") String columns
    );

    default Optional<CrawlTarget> findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            String marketplaceCode,
            Integer canonicalItemId,
            String queryConditionCode
    ) {
        return findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
                marketplaceCode, canonicalItemId, queryConditionCode, ALL_COLUMNS);
    }

    /**
     * Targets a crawler may claim now: IDLE and due, or CLAIMED by a crawler whose claim has expired.
     * Oldest due first.
     */
    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.crawl_target
            WHERE marketplace_code = #{marketplaceCode}
              AND (
                    (target_status_code = 'IDLE' AND next_due_at <= #{now})
                    OR (target_status_code = 'CLAIMED' AND claim_expires_at <= #{now})
                  )
            ORDER BY next_due_at, crawl_target_id
            LIMIT #{limit}
            """)
    @ResultMap("crawlTargetResultMap")
    List<CrawlTarget> findDue(
            @Param("marketplaceCode") String marketplaceCode,
            @Param("now") ZonedDateTime now,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default List<CrawlTarget> findDue(String marketplaceCode, ZonedDateTime now, int limit) {
        return findDue(marketplaceCode, now, limit, ALL_COLUMNS);
    }

    /**
     * Claims one target if it is still claimable at {@code claimedAt}. Returns 0 when another crawler
     * claimed it first, so the caller can tell a lost race from a success.
     */
    @Update("""
            UPDATE ${marketplacePricingSchema}.crawl_target
            SET target_status_code = 'CLAIMED',
                claimed_at = #{claimedAt},
                claim_expires_at = #{claimExpiresAt},
                claimed_by = #{claimedBy}
            WHERE crawl_target_id = #{crawlTargetId}
              AND (
                    (target_status_code = 'IDLE' AND next_due_at <= #{claimedAt})
                    OR (target_status_code = 'CLAIMED' AND claim_expires_at <= #{claimedAt})
                  )
            """)
    int claim(
            @Param("crawlTargetId") Long crawlTargetId,
            @Param("claimedBy") String claimedBy,
            @Param("claimedAt") ZonedDateTime claimedAt,
            @Param("claimExpiresAt") ZonedDateTime claimExpiresAt
    );

    /**
     * Item + query condition pairs some environment wants that have no target on this marketplace yet.
     * Only {@code canonicalItemId} and {@code queryConditionCode} are set on the results.
     */
    @Select("""
            SELECT DISTINCT w.canonical_item_id, w.condition_code AS query_condition_code
            FROM ${marketplacePricingSchema}.want_list_item w
            WHERE NOT EXISTS (
                    SELECT 1
                    FROM ${marketplacePricingSchema}.crawl_target ct
                    WHERE ct.marketplace_code = #{marketplaceCode}
                      AND ct.canonical_item_id = w.canonical_item_id
                      AND ct.query_condition_code = w.condition_code)
            ORDER BY w.canonical_item_id, query_condition_code
            """)
    @ResultMap("crawlTargetResultMap")
    List<CrawlTarget> findWantedWithoutTarget(@Param("marketplaceCode") String marketplaceCode);

    /** DISABLED targets that some environment wants again. */
    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.crawl_target ct
            WHERE ct.marketplace_code = #{marketplaceCode}
              AND ct.target_status_code = 'DISABLED'
              AND EXISTS (
                    SELECT 1
                    FROM ${marketplacePricingSchema}.want_list_item w
                    WHERE w.canonical_item_id = ct.canonical_item_id
                      AND w.condition_code = ct.query_condition_code)
            ORDER BY ct.crawl_target_id
            """)
    @ResultMap("crawlTargetResultMap")
    List<CrawlTarget> findDisabledWanted(@Param("marketplaceCode") String marketplaceCode, @Param("columns") String columns);

    default List<CrawlTarget> findDisabledWanted(String marketplaceCode) {
        return findDisabledWanted(marketplaceCode, ALL_COLUMNS);
    }

    /**
     * Disables IDLE targets no environment wants any more. A CLAIMED target is left to finish; the next
     * run disables it.
     */
    @Update("""
            UPDATE ${marketplacePricingSchema}.crawl_target ct
            SET ct.target_status_code = 'DISABLED'
            WHERE ct.marketplace_code = #{marketplaceCode}
              AND ct.target_status_code = 'IDLE'
              AND NOT EXISTS (
                    SELECT 1
                    FROM ${marketplacePricingSchema}.want_list_item w
                    WHERE w.canonical_item_id = ct.canonical_item_id
                      AND w.condition_code = ct.query_condition_code)
            """)
    int disableUnwanted(@Param("marketplaceCode") String marketplaceCode);

    @Select("""
            SELECT COUNT(*)
            FROM ${marketplacePricingSchema}.crawl_target
            WHERE marketplace_code = #{marketplaceCode}
              AND target_status_code = #{targetStatusCode}
            """)
    long countByTargetStatusCode(@Param("marketplaceCode") String marketplaceCode, @Param("targetStatusCode") String targetStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM ${marketplacePricingSchema}.crawl_target
            WHERE marketplace_code = #{marketplaceCode}
              AND (
                    (target_status_code = 'IDLE' AND next_due_at <= #{now})
                    OR (target_status_code = 'CLAIMED' AND claim_expires_at <= #{now})
                  )
            """)
    long countDue(@Param("marketplaceCode") String marketplaceCode, @Param("now") ZonedDateTime now);

    /** Targets whose latest attempt failed (consecutive_failure_count above zero), excluding DISABLED ones. */
    @Select("""
            SELECT COUNT(*)
            FROM ${marketplacePricingSchema}.crawl_target
            WHERE marketplace_code = #{marketplaceCode}
              AND target_status_code <> 'DISABLED'
              AND consecutive_failure_count > 0
            """)
    long countFailing(@Param("marketplaceCode") String marketplaceCode);

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.crawl_target (
                marketplace_code,
                canonical_item_id,
                query_condition_code,
                target_status_code,
                next_due_at,
                claimed_at,
                claim_expires_at,
                claimed_by,
                last_attempt_at,
                last_success_at,
                last_market_snapshot_id,
                consecutive_failure_count,
                last_error_message
            )
            VALUES (
                #{marketplaceCode},
                #{canonicalItemId},
                #{queryConditionCode},
                COALESCE(#{targetStatusCode}, 'IDLE'),
                #{nextDueAt},
                #{claimedAt},
                #{claimExpiresAt},
                #{claimedBy},
                #{lastAttemptAt},
                #{lastSuccessAt},
                #{lastMarketSnapshotId},
                COALESCE(#{consecutiveFailureCount}, 0),
                #{lastErrorMessage}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "crawl_target_id", keyProperty = "crawlTargetId")
    void insert(CrawlTarget crawlTarget);

    @Update("""
            UPDATE ${marketplacePricingSchema}.crawl_target
            SET target_status_code = #{targetStatusCode},
                next_due_at = #{nextDueAt},
                claimed_at = #{claimedAt},
                claim_expires_at = #{claimExpiresAt},
                claimed_by = #{claimedBy},
                last_attempt_at = #{lastAttemptAt},
                last_success_at = #{lastSuccessAt},
                last_market_snapshot_id = #{lastMarketSnapshotId},
                consecutive_failure_count = #{consecutiveFailureCount},
                last_error_message = #{lastErrorMessage}
            WHERE crawl_target_id = #{crawlTargetId}
            """)
    int update(CrawlTarget crawlTarget);
}
