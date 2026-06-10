package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrderPayload;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceOrderPayloadMapper {
    String ALL_COLUMNS = """
            marketplace_order_payload_id,
            marketplace_order_id,
            marketplace_order_sync_run_id,
            payload_type_code,
            payload_hash,
            payload_json,
            captured_at,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_payload")
    @ResultMap("marketplaceOrderPayloadResultMap")
    Set<MarketplaceOrderPayload> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_payload WHERE marketplace_order_payload_id = #{marketplaceOrderPayloadId}")
    @ResultMap("marketplaceOrderPayloadResultMap")
    Optional<MarketplaceOrderPayload> findByMarketplaceOrderPayloadId(Integer marketplaceOrderPayloadId);

    @Insert("""
            INSERT INTO marketplace_order_payload (
                marketplace_order_id,
                marketplace_order_sync_run_id,
                payload_type_code,
                payload_hash,
                payload_json,
                captured_at,
                created_at
            )
            VALUES (
                #{marketplaceOrderId},
                #{marketplaceOrderSyncRunId},
                #{payloadTypeCode},
                #{payloadHash},
                #{payloadJson},
                #{capturedAt},
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderPayloadId")
    void insert(MarketplaceOrderPayload marketplaceOrderPayload);

    @Update("""
            UPDATE marketplace_order_payload
            SET
                marketplace_order_id = #{marketplaceOrderId},
                marketplace_order_sync_run_id = #{marketplaceOrderSyncRunId},
                payload_type_code = #{payloadTypeCode},
                payload_hash = #{payloadHash},
                payload_json = #{payloadJson},
                captured_at = #{capturedAt}
            WHERE marketplace_order_payload_id = #{marketplaceOrderPayloadId}
            """)
    int update(MarketplaceOrderPayload marketplaceOrderPayload);

    @Delete("DELETE FROM marketplace_order_payload WHERE marketplace_order_payload_id = #{marketplaceOrderPayloadId}")
    int delete(Integer marketplaceOrderPayloadId);

    @Insert("""
            INSERT INTO marketplace_order_payload (
                marketplace_order_payload_id,
                marketplace_order_id,
                marketplace_order_sync_run_id,
                payload_type_code,
                payload_hash,
                payload_json,
                captured_at,
                created_at
            )
            VALUES (
                #{marketplaceOrderPayloadId},
                #{marketplaceOrderId},
                #{marketplaceOrderSyncRunId},
                #{payloadTypeCode},
                #{payloadHash},
                #{payloadJson},
                #{capturedAt},
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_order_id = VALUES(marketplace_order_id),
                marketplace_order_sync_run_id = VALUES(marketplace_order_sync_run_id),
                payload_type_code = VALUES(payload_type_code),
                payload_hash = VALUES(payload_hash),
                payload_json = VALUES(payload_json),
                captured_at = VALUES(captured_at)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderPayloadId")
    void upsert(MarketplaceOrderPayload marketplaceOrderPayload);
}
