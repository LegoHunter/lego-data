package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingCrawlWorkItemDuplicate;
import io.legohunter.data.dto.PricingCrawlWorkItemMaintenanceSummary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

public interface PricingCrawlWorkItemMapper {
    String ALL_COLUMNS = """
            pricing_crawl_work_item_id,
            marketplace_listing_id,
            external_catalog_item_id,
            source_external_service_id,
            work_status_code,
            attempt_count,
            max_attempts,
            next_attempt_at,
            claimed_at,
            completed_at,
            source_request_url,
            source_request_parameters,
            last_error_message,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_crawl_work_item")
    @ResultMap("pricingCrawlWorkItemResultMap")
    Set<PricingCrawlWorkItem> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_crawl_work_item WHERE pricing_crawl_work_item_id = #{pricingCrawlWorkItemId}")
    @ResultMap("pricingCrawlWorkItemResultMap")
    Optional<PricingCrawlWorkItem> findByPricingCrawlWorkItemId(Long pricingCrawlWorkItemId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_crawl_work_item WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("pricingCrawlWorkItemResultMap")
    Set<PricingCrawlWorkItem> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM pricing_crawl_work_item WHERE work_status_code = #{workStatusCode}")
    @ResultMap("pricingCrawlWorkItemResultMap")
    Set<PricingCrawlWorkItem> findByWorkStatusCode(String workStatusCode);

    @Select("SELECT COUNT(*) FROM pricing_crawl_work_item WHERE work_status_code = #{workStatusCode}")
    long countByWorkStatusCode(String workStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_crawl_work_item
            WHERE work_status_code = #{workStatusCode}
              AND next_attempt_at <= #{dueAt}
            """)
    long countDueByWorkStatusCode(
            @Param("workStatusCode") String workStatusCode,
            @Param("dueAt") ZonedDateTime dueAt
    );

    @Select("""
            SELECT COUNT(*)
            FROM pricing_crawl_work_item
            WHERE work_status_code = #{workStatusCode}
              AND COALESCE(attempt_count, 0) > 0
              AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
            """)
    long countRetryableByWorkStatusCode(String workStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM pricing_crawl_work_item
            WHERE work_status_code = #{claimedStatusCode}
              AND claimed_at <= #{claimedBefore}
              AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
            """)
    long countStaleClaimed(
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("claimedBefore") ZonedDateTime claimedBefore
    );

    @Select("""
            SELECT COUNT(*) AS work_item_count,
                   COUNT(DISTINCT marketplace_listing_id) AS distinct_marketplace_listing_count,
                   COUNT(*) - COUNT(DISTINCT marketplace_listing_id) AS duplicate_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code = #{pendingStatusCode} THEN 1 ELSE 0 END), 0) AS pending_work_item_count,
                   COUNT(DISTINCT CASE WHEN work_status_code = #{pendingStatusCode} THEN marketplace_listing_id END) AS distinct_pending_marketplace_listing_count,
                   COALESCE(SUM(CASE WHEN work_status_code = #{pendingStatusCode} THEN 1 ELSE 0 END), 0)
                       - COUNT(DISTINCT CASE WHEN work_status_code = #{pendingStatusCode} THEN marketplace_listing_id END) AS duplicate_pending_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code = #{pendingStatusCode} AND next_attempt_at <= #{dueAt} THEN 1 ELSE 0 END), 0) AS due_pending_work_item_count,
                   COALESCE(SUM(CASE
                       WHEN work_status_code = #{pendingStatusCode}
                        AND COALESCE(attempt_count, 0) > 0
                        AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
                       THEN 1 ELSE 0 END), 0) AS retryable_pending_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code = #{claimedStatusCode} THEN 1 ELSE 0 END), 0) AS claimed_work_item_count,
                   COALESCE(SUM(CASE
                       WHEN work_status_code = #{claimedStatusCode}
                        AND claimed_at <= #{claimedBefore}
                        AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
                       THEN 1 ELSE 0 END), 0) AS stale_claimed_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code = #{succeededStatusCode} THEN 1 ELSE 0 END), 0) AS succeeded_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code LIKE 'SKIPPED%' THEN 1 ELSE 0 END), 0) AS skipped_work_item_count,
                   COALESCE(SUM(CASE WHEN work_status_code LIKE 'FAILED%' THEN 1 ELSE 0 END), 0) AS failed_work_item_count
            FROM pricing_crawl_work_item
            """)
    @ResultMap("pricingCrawlWorkItemMaintenanceSummaryResultMap")
    PricingCrawlWorkItemMaintenanceSummary summarizeMaintenance(
            @Param("pendingStatusCode") String pendingStatusCode,
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("succeededStatusCode") String succeededStatusCode,
            @Param("dueAt") ZonedDateTime dueAt,
            @Param("claimedBefore") ZonedDateTime claimedBefore
    );

    @Select("""
            SELECT marketplace_listing_id,
                   COUNT(*) AS work_item_count,
                   SUM(CASE WHEN work_status_code = #{pendingStatusCode} THEN 1 ELSE 0 END) AS pending_count,
                   GROUP_CONCAT(work_status_code ORDER BY pricing_crawl_work_item_id SEPARATOR ',') AS work_status_codes,
                   GROUP_CONCAT(pricing_crawl_work_item_id ORDER BY pricing_crawl_work_item_id SEPARATOR ',') AS pricing_crawl_work_item_ids
            FROM pricing_crawl_work_item
            GROUP BY marketplace_listing_id
            HAVING COUNT(*) > 1
            ORDER BY work_item_count DESC,
                     marketplace_listing_id
            LIMIT #{limit}
            """)
    @ResultMap("pricingCrawlWorkItemDuplicateResultMap")
    Set<PricingCrawlWorkItemDuplicate> findDuplicateMarketplaceListingWorkItems(
            @Param("pendingStatusCode") String pendingStatusCode,
            @Param("limit") int limit
    );

    @Select("""
            SELECT ${columns}
            FROM pricing_crawl_work_item
            WHERE work_status_code = #{workStatusCode}
              AND next_attempt_at <= #{dueAt}
            ORDER BY next_attempt_at, pricing_crawl_work_item_id
            LIMIT #{limit}
            """)
    @ResultMap("pricingCrawlWorkItemResultMap")
    Set<PricingCrawlWorkItem> findDueByWorkStatusCode(
            @Param("workStatusCode") String workStatusCode,
            @Param("dueAt") ZonedDateTime dueAt,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<PricingCrawlWorkItem> findDueByWorkStatusCode(String workStatusCode, ZonedDateTime dueAt, int limit) {
        return findDueByWorkStatusCode(workStatusCode, dueAt, limit, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM pricing_crawl_work_item
            WHERE work_status_code = #{workStatusCode}
              AND next_attempt_at <= #{dueAt}
              AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
            ORDER BY next_attempt_at, pricing_crawl_work_item_id
            LIMIT #{limit}
            """)
    @ResultMap("pricingCrawlWorkItemResultMap")
    Set<PricingCrawlWorkItem> findClaimableByWorkStatusCode(
            @Param("workStatusCode") String workStatusCode,
            @Param("dueAt") ZonedDateTime dueAt,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<PricingCrawlWorkItem> findClaimableByWorkStatusCode(String workStatusCode, ZonedDateTime dueAt, int limit) {
        return findClaimableByWorkStatusCode(workStatusCode, dueAt, limit, ALL_COLUMNS);
    }

    @Update("""
            UPDATE pricing_crawl_work_item
            SET work_status_code = #{claimedStatusCode},
                attempt_count = COALESCE(attempt_count, 0) + 1,
                claimed_at = #{claimedAt},
                completed_at = NULL,
                last_error_message = NULL,
                updated_at = CURRENT_TIMESTAMP
            WHERE pricing_crawl_work_item_id = #{pricingCrawlWorkItemId}
              AND work_status_code = #{pendingStatusCode}
              AND next_attempt_at <= #{dueAt}
              AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
            """)
    int claim(
            @Param("pricingCrawlWorkItemId") Long pricingCrawlWorkItemId,
            @Param("pendingStatusCode") String pendingStatusCode,
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("dueAt") ZonedDateTime dueAt,
            @Param("claimedAt") ZonedDateTime claimedAt
    );

    @Update("""
            UPDATE pricing_crawl_work_item
            SET work_status_code = #{pendingStatusCode},
                next_attempt_at = #{nextAttemptAt},
                claimed_at = NULL,
                completed_at = NULL,
                last_error_message = #{lastErrorMessage},
                updated_at = CURRENT_TIMESTAMP
            WHERE work_status_code = #{claimedStatusCode}
              AND claimed_at <= #{claimedBefore}
              AND COALESCE(attempt_count, 0) < COALESCE(max_attempts, 3)
            """)
    int requeueStaleClaimed(
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("pendingStatusCode") String pendingStatusCode,
            @Param("claimedBefore") ZonedDateTime claimedBefore,
            @Param("nextAttemptAt") ZonedDateTime nextAttemptAt,
            @Param("lastErrorMessage") String lastErrorMessage
    );

    @Insert("""
            INSERT INTO pricing_crawl_work_item (
                marketplace_listing_id,
                external_catalog_item_id,
                source_external_service_id,
                work_status_code,
                attempt_count,
                max_attempts,
                next_attempt_at,
                claimed_at,
                completed_at,
                source_request_url,
                source_request_parameters,
                last_error_message,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceListingId},
                #{externalCatalogItemId},
                #{sourceExternalServiceId},
                #{workStatusCode},
                COALESCE(#{attemptCount}, 0),
                COALESCE(#{maxAttempts}, 3),
                #{nextAttemptAt},
                #{claimedAt},
                #{completedAt},
                #{sourceRequestUrl},
                #{sourceRequestParameters},
                #{lastErrorMessage},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_crawl_work_item_id", keyProperty = "pricingCrawlWorkItemId")
    void insert(PricingCrawlWorkItem pricingCrawlWorkItem);

    @Update("""
            UPDATE pricing_crawl_work_item
            SET marketplace_listing_id = #{marketplaceListingId},
                external_catalog_item_id = #{externalCatalogItemId},
                source_external_service_id = #{sourceExternalServiceId},
                work_status_code = #{workStatusCode},
                attempt_count = #{attemptCount},
                max_attempts = #{maxAttempts},
                next_attempt_at = #{nextAttemptAt},
                claimed_at = #{claimedAt},
                completed_at = #{completedAt},
                source_request_url = #{sourceRequestUrl},
                source_request_parameters = #{sourceRequestParameters},
                last_error_message = #{lastErrorMessage},
                updated_at = CURRENT_TIMESTAMP
            WHERE pricing_crawl_work_item_id = #{pricingCrawlWorkItemId}
            """)
    int update(PricingCrawlWorkItem pricingCrawlWorkItem);

    @Delete("DELETE FROM pricing_crawl_work_item WHERE pricing_crawl_work_item_id = #{pricingCrawlWorkItemId}")
    int delete(Long pricingCrawlWorkItemId);

    @Insert("""
            INSERT INTO pricing_crawl_work_item (
                pricing_crawl_work_item_id,
                marketplace_listing_id,
                external_catalog_item_id,
                source_external_service_id,
                work_status_code,
                attempt_count,
                max_attempts,
                next_attempt_at,
                claimed_at,
                completed_at,
                source_request_url,
                source_request_parameters,
                last_error_message,
                created_at,
                updated_at
            )
            VALUES (
                #{pricingCrawlWorkItemId},
                #{marketplaceListingId},
                #{externalCatalogItemId},
                #{sourceExternalServiceId},
                #{workStatusCode},
                COALESCE(#{attemptCount}, 0),
                COALESCE(#{maxAttempts}, 3),
                #{nextAttemptAt},
                #{claimedAt},
                #{completedAt},
                #{sourceRequestUrl},
                #{sourceRequestParameters},
                #{lastErrorMessage},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_listing_id = VALUES(marketplace_listing_id),
                external_catalog_item_id = VALUES(external_catalog_item_id),
                source_external_service_id = VALUES(source_external_service_id),
                work_status_code = VALUES(work_status_code),
                attempt_count = VALUES(attempt_count),
                max_attempts = VALUES(max_attempts),
                next_attempt_at = VALUES(next_attempt_at),
                claimed_at = VALUES(claimed_at),
                completed_at = VALUES(completed_at),
                source_request_url = VALUES(source_request_url),
                source_request_parameters = VALUES(source_request_parameters),
                last_error_message = VALUES(last_error_message),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyColumn = "pricing_crawl_work_item_id", keyProperty = "pricingCrawlWorkItemId")
    void upsert(PricingCrawlWorkItem pricingCrawlWorkItem);
}
