package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrderSyncRun;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceOrderSyncRunMapper {
    String ALL_COLUMNS = """
            marketplace_order_sync_run_id,
            marketplace_code,
            sync_job_name,
            sync_direction,
            sync_status_code,
            started_at,
            completed_at,
            orders_discovered,
            orders_fetched,
            orders_failed,
            error_message,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_sync_run")
    @ResultMap("marketplaceOrderSyncRunResultMap")
    Set<MarketplaceOrderSyncRun> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_sync_run WHERE marketplace_order_sync_run_id = #{marketplaceOrderSyncRunId}")
    @ResultMap("marketplaceOrderSyncRunResultMap")
    Optional<MarketplaceOrderSyncRun> findByMarketplaceOrderSyncRunId(Integer marketplaceOrderSyncRunId);

    @Insert("""
            INSERT INTO marketplace_order_sync_run (
                marketplace_code,
                sync_job_name,
                sync_direction,
                sync_status_code,
                started_at,
                completed_at,
                orders_discovered,
                orders_fetched,
                orders_failed,
                error_message,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceCode},
                #{syncJobName},
                #{syncDirection},
                #{syncStatusCode},
                #{startedAt},
                #{completedAt},
                #{ordersDiscovered},
                #{ordersFetched},
                #{ordersFailed},
                #{errorMessage},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderSyncRunId")
    void insert(MarketplaceOrderSyncRun marketplaceOrderSyncRun);

    @Update("""
            UPDATE marketplace_order_sync_run
            SET
                marketplace_code = #{marketplaceCode},
                sync_job_name = #{syncJobName},
                sync_direction = #{syncDirection},
                sync_status_code = #{syncStatusCode},
                started_at = #{startedAt},
                completed_at = #{completedAt},
                orders_discovered = #{ordersDiscovered},
                orders_fetched = #{ordersFetched},
                orders_failed = #{ordersFailed},
                error_message = #{errorMessage},
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_order_sync_run_id = #{marketplaceOrderSyncRunId}
            """)
    int update(MarketplaceOrderSyncRun marketplaceOrderSyncRun);

    @Delete("DELETE FROM marketplace_order_sync_run WHERE marketplace_order_sync_run_id = #{marketplaceOrderSyncRunId}")
    int delete(Integer marketplaceOrderSyncRunId);

    @Insert("""
            INSERT INTO marketplace_order_sync_run (
                marketplace_order_sync_run_id,
                marketplace_code,
                sync_job_name,
                sync_direction,
                sync_status_code,
                started_at,
                completed_at,
                orders_discovered,
                orders_fetched,
                orders_failed,
                error_message,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderSyncRunId},
                #{marketplaceCode},
                #{syncJobName},
                #{syncDirection},
                #{syncStatusCode},
                #{startedAt},
                #{completedAt},
                #{ordersDiscovered},
                #{ordersFetched},
                #{ordersFailed},
                #{errorMessage},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_code = VALUES(marketplace_code),
                sync_job_name = VALUES(sync_job_name),
                sync_direction = VALUES(sync_direction),
                sync_status_code = VALUES(sync_status_code),
                started_at = VALUES(started_at),
                completed_at = VALUES(completed_at),
                orders_discovered = VALUES(orders_discovered),
                orders_fetched = VALUES(orders_fetched),
                orders_failed = VALUES(orders_failed),
                error_message = VALUES(error_message),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderSyncRunId")
    void upsert(MarketplaceOrderSyncRun marketplaceOrderSyncRun);
}
