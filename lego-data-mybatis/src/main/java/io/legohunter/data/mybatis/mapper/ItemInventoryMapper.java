package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ItemInventoryMapper {
    String ALL_COLUMNS = """
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
            built_once
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory")
    @ResultMap("itemInventoryResultMap")
    Set<ItemInventory> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory WHERE item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory WHERE uuid = #{uuid}")
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByUuid(String uuid);

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
                built_once
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
                #{builtOnce}
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
                built_once = #{builtOnce}
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int update(ItemInventory itemInventory);

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
                built_once
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
                #{builtOnce}
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
                built_once = VALUES(built_once)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "item_inventory_id", keyProperty = "itemInventoryId")
    int upsert(ItemInventory itemInventory);
}
