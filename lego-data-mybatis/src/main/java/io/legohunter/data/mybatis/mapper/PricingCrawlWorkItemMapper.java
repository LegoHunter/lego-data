package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PricingCrawlWorkItem;
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
