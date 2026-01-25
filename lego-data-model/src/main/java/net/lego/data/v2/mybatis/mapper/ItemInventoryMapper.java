package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.ItemInventory;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface ItemInventoryMapper {
    String ALL_COLUMNS = """
                    
                item_inventory_id,
            uuid,
            item_id,
            box_number,
            quantity,
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
            INSERT INTO item_inventory (uuid, item_id, box_number, quantity, description, active, for_sale, new_or_used, completeness, item_condition_id, box_condition_id, instructions_condition_id, sealed, built_once) \
            VALUES (#{uuid}, #{itemId}, #{boxNumber}, #{quantity}, #{description}, #{active}, #{forSale}, #{newOrUsed}, #{completeness}, #{itemConditionId}, #{boxConditionId}, #{instructionsConditionId}, #{sealed}, #{builtOnce}) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "itemInventoryId")
    void insert(ItemInventory itemInventory);

    @Update("""
            UPDATE item_inventory SET
                   uuid= #{uuid},
                   item_id= #{itemId},
                   box_number= #{boxNumber},
                   quantity= #{quantity},
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
    void update(ItemInventory itemInventory);
}
