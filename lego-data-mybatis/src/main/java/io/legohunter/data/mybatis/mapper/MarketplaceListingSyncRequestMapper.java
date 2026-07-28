package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceListingSyncRequest;
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

public interface MarketplaceListingSyncRequestMapper {
    String ALL_COLUMNS = """
            marketplace_listing_sync_request_id,
            marketplace_listing_id,
            listing_external_service_id,
            pricing_decision_id,
            pricing_apply_readiness_id,
            sync_request_type_code,
            sync_request_status_code,
            sync_reason_code,
            previous_unit_price,
            requested_unit_price,
            currency_code,
            remote_inventory_id,
            remote_visibility_scope_code,
            remote_visibility_container_id,
            remote_is_publicly_available,
            environment_code,
            created_by_job_name,
            last_error_message,
            attempt_count,
            max_attempts,
            next_attempt_at,
            claimed_at,
            applied_local_at,
            completed_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing_sync_request")
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing_sync_request WHERE marketplace_listing_sync_request_id = #{marketplaceListingSyncRequestId}")
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Optional<MarketplaceListingSyncRequest> findByMarketplaceListingSyncRequestId(Long marketplaceListingSyncRequestId);

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing_sync_request WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_listing_sync_request WHERE sync_request_status_code = #{syncRequestStatusCode}")
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findBySyncRequestStatusCode(String syncRequestStatusCode);

    @Select("""
            SELECT ${columns}
            FROM marketplace_listing_sync_request
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND pricing_decision_id = #{pricingDecisionId}
              AND sync_request_type_code = #{syncRequestTypeCode}
            """)
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Optional<MarketplaceListingSyncRequest> findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("pricingDecisionId") Long pricingDecisionId,
            @Param("syncRequestTypeCode") String syncRequestTypeCode,
            @Param("columns") String columns
    );

    default Optional<MarketplaceListingSyncRequest> findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(
            Integer marketplaceListingId,
            Long pricingDecisionId,
            String syncRequestTypeCode
    ) {
        return findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(
                marketplaceListingId,
                pricingDecisionId,
                syncRequestTypeCode,
                ALL_COLUMNS
        );
    }

    @Select("""
            <script>
            SELECT ${columns}
            FROM marketplace_listing_sync_request
            WHERE marketplace_listing_id = #{marketplaceListingId}
              AND sync_request_type_code = #{syncRequestTypeCode}
              AND sync_request_status_code IN
              <foreach item="syncRequestStatusCode" collection="syncRequestStatusCodes" open="(" separator="," close=")">
                #{syncRequestStatusCode}
              </foreach>
            ORDER BY marketplace_listing_sync_request_id
            </script>
            """)
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
            @Param("marketplaceListingId") Integer marketplaceListingId,
            @Param("syncRequestTypeCode") String syncRequestTypeCode,
            @Param("syncRequestStatusCodes") Set<String> syncRequestStatusCodes,
            @Param("columns") String columns
    );

    default Set<MarketplaceListingSyncRequest> findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
            Integer marketplaceListingId,
            String syncRequestTypeCode,
            Set<String> syncRequestStatusCodes
    ) {
        return findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
                marketplaceListingId,
                syncRequestTypeCode,
                syncRequestStatusCodes,
                ALL_COLUMNS
        );
    }

    @Select("""
            <script>
            SELECT ${columns}
            FROM marketplace_listing_sync_request
            WHERE sync_request_status_code = #{syncRequestStatusCode}
              AND next_attempt_at &lt;= #{asOf}
              AND attempt_count &lt; max_attempts
              AND sync_request_type_code IN
              <foreach item="syncRequestTypeCode" collection="syncRequestTypeCodes" open="(" separator="," close=")">
                #{syncRequestTypeCode}
              </foreach>
            ORDER BY next_attempt_at, marketplace_listing_sync_request_id
            LIMIT #{limit}
            </script>
            """)
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findClaimableByStatusCodeAndSyncRequestTypeCodes(
            @Param("syncRequestStatusCode") String syncRequestStatusCode,
            @Param("syncRequestTypeCodes") Set<String> syncRequestTypeCodes,
            @Param("asOf") ZonedDateTime asOf,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    @Select("""
            SELECT ${columns}
            FROM marketplace_listing_sync_request
            WHERE sync_request_status_code = #{syncRequestStatusCode}
              AND next_attempt_at <= #{asOf}
              AND attempt_count < max_attempts
            ORDER BY next_attempt_at, marketplace_listing_sync_request_id
            LIMIT #{limit}
            """)
    @ResultMap("marketplaceListingSyncRequestResultMap")
    Set<MarketplaceListingSyncRequest> findClaimableByStatusCode(
            @Param("syncRequestStatusCode") String syncRequestStatusCode,
            @Param("asOf") ZonedDateTime asOf,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default Set<MarketplaceListingSyncRequest> findClaimableByStatusCode(String syncRequestStatusCode, ZonedDateTime asOf, int limit) {
        return findClaimableByStatusCode(syncRequestStatusCode, asOf, limit, ALL_COLUMNS);
    }

    default Set<MarketplaceListingSyncRequest> findClaimableByStatusCodeAndSyncRequestTypeCodes(
            String syncRequestStatusCode,
            Set<String> syncRequestTypeCodes,
            ZonedDateTime asOf,
            int limit
    ) {
        return findClaimableByStatusCodeAndSyncRequestTypeCodes(
                syncRequestStatusCode,
                syncRequestTypeCodes,
                asOf,
                limit,
                ALL_COLUMNS
        );
    }

    @Select("SELECT COUNT(*) FROM marketplace_listing_sync_request WHERE sync_request_status_code = #{syncRequestStatusCode}")
    long countBySyncRequestStatusCode(String syncRequestStatusCode);

    @Select("""
            SELECT COUNT(*)
            FROM marketplace_listing_sync_request
            WHERE sync_request_status_code = #{syncRequestStatusCode}
              AND next_attempt_at <= #{asOf}
              AND attempt_count < max_attempts
            """)
    long countDueBySyncRequestStatusCode(@Param("syncRequestStatusCode") String syncRequestStatusCode, @Param("asOf") ZonedDateTime asOf);

    @Insert("""
            INSERT INTO marketplace_listing_sync_request (
                marketplace_listing_id,
                listing_external_service_id,
                pricing_decision_id,
                pricing_apply_readiness_id,
                sync_request_type_code,
                sync_request_status_code,
                sync_reason_code,
                previous_unit_price,
                requested_unit_price,
                currency_code,
                remote_inventory_id,
                remote_visibility_scope_code,
                remote_visibility_container_id,
                remote_is_publicly_available,
                environment_code,
                created_by_job_name,
                last_error_message,
                attempt_count,
                max_attempts,
                next_attempt_at,
                claimed_at,
                applied_local_at,
                completed_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceListingId},
                #{listingExternalServiceId},
                #{pricingDecisionId},
                #{pricingApplyReadinessId},
                #{syncRequestTypeCode},
                #{syncRequestStatusCode},
                #{syncReasonCode},
                #{previousUnitPrice},
                #{requestedUnitPrice},
                #{currencyCode},
                #{remoteInventoryId},
                #{remoteVisibilityScopeCode},
                #{remoteVisibilityContainerId},
                #{remoteIsPubliclyAvailable},
                #{environmentCode},
                #{createdByJobName},
                #{lastErrorMessage},
                COALESCE(#{attemptCount}, 0),
                COALESCE(#{maxAttempts}, 3),
                COALESCE(#{nextAttemptAt}, CURRENT_TIMESTAMP),
                #{claimedAt},
                #{appliedLocalAt},
                #{completedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "marketplace_listing_sync_request_id", keyProperty = "marketplaceListingSyncRequestId")
    void insert(MarketplaceListingSyncRequest syncRequest);

    @Update("""
            UPDATE marketplace_listing_sync_request
            SET marketplace_listing_id = #{marketplaceListingId},
                listing_external_service_id = #{listingExternalServiceId},
                pricing_decision_id = #{pricingDecisionId},
                pricing_apply_readiness_id = #{pricingApplyReadinessId},
                sync_request_type_code = #{syncRequestTypeCode},
                sync_request_status_code = #{syncRequestStatusCode},
                sync_reason_code = #{syncReasonCode},
                previous_unit_price = #{previousUnitPrice},
                requested_unit_price = #{requestedUnitPrice},
                currency_code = #{currencyCode},
                remote_inventory_id = #{remoteInventoryId},
                remote_visibility_scope_code = #{remoteVisibilityScopeCode},
                remote_visibility_container_id = #{remoteVisibilityContainerId},
                remote_is_publicly_available = #{remoteIsPubliclyAvailable},
                environment_code = #{environmentCode},
                created_by_job_name = #{createdByJobName},
                last_error_message = #{lastErrorMessage},
                attempt_count = #{attemptCount},
                max_attempts = #{maxAttempts},
                next_attempt_at = #{nextAttemptAt},
                claimed_at = #{claimedAt},
                applied_local_at = #{appliedLocalAt},
                completed_at = #{completedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_listing_sync_request_id = #{marketplaceListingSyncRequestId}
            """)
    int update(MarketplaceListingSyncRequest syncRequest);

    @Update("""
            UPDATE marketplace_listing_sync_request
            SET sync_request_status_code = #{claimedStatusCode},
                claimed_at = #{claimedAt},
                attempt_count = attempt_count + 1,
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_listing_sync_request_id = #{marketplaceListingSyncRequestId}
              AND sync_request_status_code = #{fromStatusCode}
              AND attempt_count < max_attempts
            """)
    int claim(
            @Param("marketplaceListingSyncRequestId") Long marketplaceListingSyncRequestId,
            @Param("fromStatusCode") String fromStatusCode,
            @Param("claimedStatusCode") String claimedStatusCode,
            @Param("claimedAt") ZonedDateTime claimedAt
    );

    @Insert("""
            INSERT INTO marketplace_listing_sync_request (
                marketplace_listing_sync_request_id,
                marketplace_listing_id,
                listing_external_service_id,
                pricing_decision_id,
                pricing_apply_readiness_id,
                sync_request_type_code,
                sync_request_status_code,
                sync_reason_code,
                previous_unit_price,
                requested_unit_price,
                currency_code,
                remote_inventory_id,
                remote_visibility_scope_code,
                remote_visibility_container_id,
                remote_is_publicly_available,
                environment_code,
                created_by_job_name,
                last_error_message,
                attempt_count,
                max_attempts,
                next_attempt_at,
                claimed_at,
                applied_local_at,
                completed_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceListingSyncRequestId},
                #{marketplaceListingId},
                #{listingExternalServiceId},
                #{pricingDecisionId},
                #{pricingApplyReadinessId},
                #{syncRequestTypeCode},
                #{syncRequestStatusCode},
                #{syncReasonCode},
                #{previousUnitPrice},
                #{requestedUnitPrice},
                #{currencyCode},
                #{remoteInventoryId},
                #{remoteVisibilityScopeCode},
                #{remoteVisibilityContainerId},
                #{remoteIsPubliclyAvailable},
                #{environmentCode},
                #{createdByJobName},
                #{lastErrorMessage},
                COALESCE(#{attemptCount}, 0),
                COALESCE(#{maxAttempts}, 3),
                COALESCE(#{nextAttemptAt}, CURRENT_TIMESTAMP),
                #{claimedAt},
                #{appliedLocalAt},
                #{completedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                pricing_apply_readiness_id = VALUES(pricing_apply_readiness_id),
                sync_request_status_code = VALUES(sync_request_status_code),
                sync_reason_code = VALUES(sync_reason_code),
                previous_unit_price = VALUES(previous_unit_price),
                requested_unit_price = VALUES(requested_unit_price),
                currency_code = VALUES(currency_code),
                remote_inventory_id = VALUES(remote_inventory_id),
                remote_visibility_scope_code = VALUES(remote_visibility_scope_code),
                remote_visibility_container_id = VALUES(remote_visibility_container_id),
                remote_is_publicly_available = VALUES(remote_is_publicly_available),
                environment_code = VALUES(environment_code),
                created_by_job_name = VALUES(created_by_job_name),
                last_error_message = VALUES(last_error_message),
                max_attempts = VALUES(max_attempts),
                next_attempt_at = VALUES(next_attempt_at),
                claimed_at = VALUES(claimed_at),
                applied_local_at = VALUES(applied_local_at),
                completed_at = VALUES(completed_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyColumn = "marketplace_listing_sync_request_id", keyProperty = "marketplaceListingSyncRequestId")
    void upsert(MarketplaceListingSyncRequest syncRequest);

    @Delete("DELETE FROM marketplace_listing_sync_request WHERE marketplace_listing_sync_request_id = #{marketplaceListingSyncRequestId}")
    int delete(Long marketplaceListingSyncRequestId);
}
