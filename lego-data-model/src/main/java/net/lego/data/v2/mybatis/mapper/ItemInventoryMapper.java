package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.ItemInventory;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface ItemInventoryMapper {
    String ALL_COLUMNS = """            
            item_inventory_id,
            uuid,
            box_number,
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
    List<ItemInventory> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory WHERE item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory WHERE uuid = #{uuid}")
    @ResultMap("itemInventoryResultMap")
    Optional<ItemInventory> findByUuid(String uuid);

    @Insert("""
            INSERT INTO item_inventory (uuid, box_number, description, active, for_sale, new_or_used, completeness, item_condition_id, box_condition_id, instructions_condition_id, sealed, built_once) \
            VALUES (#{uuid}, #{boxNumber}, #{description}, #{active}, #{forSale}, #{newOrUsed}, #{completeness}, #{itemConditionId}, #{boxConditionId}, #{instructionsConditionId}, #{sealed}, #{builtOnce}) \
            """)
    @Options(useGeneratedKeys = true, keyColumn = "item_inventory_id", keyProperty = "itemInventoryId")
    void insert(ItemInventory itemInventory);

    @Update("""
            UPDATE item_inventory SET
                   uuid= #{uuid},
                   box_number= #{boxNumber},
                   description= #{description},
                   active= #{active},
                   for_sale= #{forSale},
                   new_or_used= #{newOrUsed},
                   completeness= #{completeness},
                   item_condition_id= #{itemConditionId},
                   box_condition_id= #{boxConditionId},
                   instructions_condition_id= #{instructionsConditionId},
                   sealed= #{sealed},
                   built_once = #{builtOnce}
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int update(ItemInventory itemInventory);

    @Insert("""
            INSERT INTO item_inventory (
                    uuid,
                    box_number,
                    description,
                    active,
                    for_sale,
                    new_or_used,
                    completeness,
                    item_condition_id,
                    box_condition_id,
                    instructions_condition_id,
                    sealed,
                    built_once)
            VALUES (#{uuid},
                    #{boxNumber},
                    #{description},
                    #{active},
                    #{forSale},
                    #{newOrUsed},
                    #{completeness},
                    #{itemConditionId},
                    #{boxConditionId},
                    #{instructionsConditionId},
                    #{sealed},
                    #{builtOnce})
            ON DUPLICATE KEY UPDATE
                box_number = VALUES(box_number),
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
    @Options(useGeneratedKeys = true, keyProperty = "itemInventoryId")
    void upsert(ItemInventory itemInventory);
}