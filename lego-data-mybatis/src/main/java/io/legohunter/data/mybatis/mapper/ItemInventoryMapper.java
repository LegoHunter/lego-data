package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
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

public interface ItemInventoryMapper {
    String ALL_COLUMNS = """
            ii.item_inventory_id,
            ii.uuid,
            ii.box_number,
            ii.purchase_price,
            ii.description,
            ii.active,
            ii.for_sale,
            ii.new_or_used,
            ii.completeness,
            ii.item_condition_id,
            ii.box_condition_id,
            ii.instructions_condition_id,
            ii.sealed,
            ii.built_once,
            ii.inventory_state_code,
            ii.inventory_state_changed_at,
            ii.sale_intent_code,
            ii.sale_intent_updated_at,
            ii.sale_intent_note,
            iieci.item_inventory_id AS iieci_item_inventory_id,
            iieci.external_catalog_item_id AS iieci_external_catalog_item_id,
            iieci.is_primary AS iieci_is_primary,
            iieci.created_at AS iieci_created_at,
            eci.external_catalog_item_id AS iieci_eci_external_catalog_item_id,
            eci.external_service_id AS iieci_eci_external_service_id,
            eci.external_item_key AS iieci_eci_external_item_key,
            eci.external_unique_key AS iieci_eci_external_unique_key,
            eci.item_name AS iieci_eci_item_name,
            eci.item_type_code AS iieci_eci_item_type_code,
            eci.item_url AS iieci_eci_item_url,
            eci.year_released AS iieci_eci_year_released,
            es.external_service_id AS iieci_eci_es_external_service_id,
            es.service_code AS iieci_eci_es_service_code,
            es.display_name AS iieci_eci_es_display_name,
            es.service_url AS iieci_eci_es_service_url,
            es.external_service_type_id AS iieci_eci_es_external_service_type_id,
            est.external_service_type_id AS iieci_eci_es_est_external_service_type_id,
            est.external_service_type_name AS iieci_eci_es_est_external_service_type_name,
            est.external_service_type_description AS iieci_eci_es_est_external_service_type_description
            """;

    String FROM_CLAUSE = """
            FROM item_inventory ii
            LEFT JOIN item_inventory_external_catalog_item iieci
                ON iieci.item_inventory_id = ii.item_inventory_id
            LEFT JOIN external_catalog_item eci
                ON eci.external_catalog_item_id = iieci.external_catalog_item_id
            LEFT JOIN external_service es
                ON es.external_service_id = eci.external_service_id
            LEFT JOIN external_service_type est
                ON est.external_service_type_id = es.external_service_type_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE)
    @ResultMap("itemInventoryResultMap")
    Set<ItemInventory> findAll();

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE ii.item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByItemInventoryId(
            @Param("itemInventoryId") Integer itemInventoryId,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId) {
        return findByItemInventoryId(itemInventoryId, ALL_COLUMNS, FROM_CLAUSE);
    }

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE ii.uuid = #{uuid}
            """)
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByUuid(
            @Param("uuid") String uuid,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ItemInventory> findByUuid(String uuid) {
        return findByUuid(uuid, ALL_COLUMNS, FROM_CLAUSE);
    }

    @Insert("""
            INSERT INTO item_inventory (
                uuid,
                box_number,
                purchase_price,
                description,
                active,
                for_sale,
                new_or_used,
                completeness,
                item_condition_id,
                box_condition_id,
                instructions_condition_id,
                sealed,
                built_once,
                inventory_state_code,
                inventory_state_changed_at,
                sale_intent_code,
                sale_intent_updated_at,
                sale_intent_note
            )
            VALUES (
                #{uuid},
                #{boxNumber},
                #{purchasePrice},
                #{description},
                #{active},
                #{forSale},
                #{newOrUsed},
                #{completeness},
                #{itemConditionId},
                #{boxConditionId},
                #{instructionsConditionId},
                #{sealed},
                #{builtOnce},
                COALESCE(#{inventoryStateCode}, 'AVAILABLE'),
                COALESCE(#{inventoryStateChangedAt}, CURRENT_TIMESTAMP),
                COALESCE(#{saleIntentCode}, 'UNDECIDED'),
                COALESCE(#{saleIntentUpdatedAt}, CURRENT_TIMESTAMP),
                #{saleIntentNote}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "item_inventory_id", keyProperty = "itemInventoryId")
    int insert(ItemInventory itemInventory);

    @Update("""
            UPDATE item_inventory
            SET uuid = #{uuid},
                box_number = #{boxNumber},
                purchase_price = #{purchasePrice},
                description = #{description},
                active = #{active},
                for_sale = #{forSale},
                new_or_used = #{newOrUsed},
                completeness = #{completeness},
                item_condition_id = #{itemConditionId},
                box_condition_id = #{boxConditionId},
                instructions_condition_id = #{instructionsConditionId},
                sealed = #{sealed},
                built_once = #{builtOnce},
                inventory_state_code = COALESCE(#{inventoryStateCode}, inventory_state_code, 'AVAILABLE'),
                inventory_state_changed_at = COALESCE(#{inventoryStateChangedAt}, inventory_state_changed_at, CURRENT_TIMESTAMP),
                sale_intent_code = COALESCE(#{saleIntentCode}, sale_intent_code, 'UNDECIDED'),
                sale_intent_updated_at = COALESCE(#{saleIntentUpdatedAt}, sale_intent_updated_at, CURRENT_TIMESTAMP),
                sale_intent_note = #{saleIntentNote}
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int update(ItemInventory itemInventory);

    @Update("""
            UPDATE item_inventory
            SET inventory_state_code = #{inventoryStateCode},
                inventory_state_changed_at = COALESCE(#{inventoryStateChangedAt}, CURRENT_TIMESTAMP)
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int updateInventoryState(
            @Param("itemInventoryId") Integer itemInventoryId,
            @Param("inventoryStateCode") String inventoryStateCode,
            @Param("inventoryStateChangedAt") ZonedDateTime inventoryStateChangedAt
    );

    @Update("""
            UPDATE item_inventory
            SET sale_intent_code = #{saleIntentCode},
                sale_intent_updated_at = COALESCE(#{saleIntentUpdatedAt}, CURRENT_TIMESTAMP),
                sale_intent_note = #{saleIntentNote}
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int updateSaleIntent(
            @Param("itemInventoryId") Integer itemInventoryId,
            @Param("saleIntentCode") String saleIntentCode,
            @Param("saleIntentUpdatedAt") ZonedDateTime saleIntentUpdatedAt,
            @Param("saleIntentNote") String saleIntentNote
    );

    @Delete("DELETE FROM item_inventory WHERE item_inventory_id = #{itemInventoryId}")
    int delete(Integer itemInventoryId);

    @Insert("""
            INSERT INTO item_inventory (
                item_inventory_id,
                uuid,
                box_number,
                purchase_price,
                description,
                active,
                for_sale,
                new_or_used,
                completeness,
                item_condition_id,
                box_condition_id,
                instructions_condition_id,
                sealed,
                built_once,
                inventory_state_code,
                inventory_state_changed_at,
                sale_intent_code,
                sale_intent_updated_at,
                sale_intent_note
            )
            VALUES (
                #{itemInventoryId},
                #{uuid},
                #{boxNumber},
                #{purchasePrice},
                #{description},
                #{active},
                #{forSale},
                #{newOrUsed},
                #{completeness},
                #{itemConditionId},
                #{boxConditionId},
                #{instructionsConditionId},
                #{sealed},
                #{builtOnce},
                COALESCE(#{inventoryStateCode}, 'AVAILABLE'),
                COALESCE(#{inventoryStateChangedAt}, CURRENT_TIMESTAMP),
                COALESCE(#{saleIntentCode}, 'UNDECIDED'),
                COALESCE(#{saleIntentUpdatedAt}, CURRENT_TIMESTAMP),
                #{saleIntentNote}
            )
            ON DUPLICATE KEY UPDATE
                uuid = VALUES(uuid),
                box_number = VALUES(box_number),
                purchase_price = VALUES(purchase_price),
                description = VALUES(description),
                active = VALUES(active),
                for_sale = VALUES(for_sale),
                new_or_used = VALUES(new_or_used),
                completeness = VALUES(completeness),
                item_condition_id = VALUES(item_condition_id),
                box_condition_id = VALUES(box_condition_id),
                instructions_condition_id = VALUES(instructions_condition_id),
                sealed = VALUES(sealed),
                built_once = VALUES(built_once),
                inventory_state_code = COALESCE(VALUES(inventory_state_code), inventory_state_code, 'AVAILABLE'),
                inventory_state_changed_at = COALESCE(VALUES(inventory_state_changed_at), inventory_state_changed_at, CURRENT_TIMESTAMP),
                sale_intent_code = COALESCE(VALUES(sale_intent_code), sale_intent_code, 'UNDECIDED'),
                sale_intent_updated_at = COALESCE(VALUES(sale_intent_updated_at), sale_intent_updated_at, CURRENT_TIMESTAMP),
                sale_intent_note = VALUES(sale_intent_note)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "item_inventory_id", keyProperty = "itemInventoryId")
    int upsert(ItemInventory itemInventory);
}
