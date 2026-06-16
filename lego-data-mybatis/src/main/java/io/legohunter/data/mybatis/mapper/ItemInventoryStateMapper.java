package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventoryState;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ItemInventoryStateMapper {
    @Select("""
            SELECT inventory_state_code,
                   inventory_state_name,
                   inventory_state_description,
                   active,
                   sort_order
            FROM item_inventory_state
            """)
    @ResultMap("itemInventoryStateResultMap")
    Set<ItemInventoryState> findAll();

    @Select("""
            SELECT inventory_state_code,
                   inventory_state_name,
                   inventory_state_description,
                   active,
                   sort_order
            FROM item_inventory_state
            WHERE inventory_state_code = #{inventoryStateCode}
            """)
    @ResultMap("itemInventoryStateResultMap")
    Optional<ItemInventoryState> findByInventoryStateCode(String inventoryStateCode);

    @Insert("""
            INSERT INTO item_inventory_state (inventory_state_code,
                                              inventory_state_name,
                                              inventory_state_description,
                                              active,
                                              sort_order)
            VALUES (#{inventoryStateCode},
                    #{inventoryStateName},
                    #{inventoryStateDescription},
                    #{active},
                    #{sortOrder})
            """)
    int insert(ItemInventoryState itemInventoryState);

    @Update("""
            UPDATE item_inventory_state
            SET inventory_state_name = #{inventoryStateName},
                inventory_state_description = #{inventoryStateDescription},
                active = #{active},
                sort_order = #{sortOrder}
            WHERE inventory_state_code = #{inventoryStateCode}
            """)
    int update(ItemInventoryState itemInventoryState);

    @Delete("""
            DELETE FROM item_inventory_state
            WHERE inventory_state_code = #{inventoryStateCode}
            """)
    int delete(String inventoryStateCode);

    @Insert("""
            INSERT INTO item_inventory_state (inventory_state_code,
                                              inventory_state_name,
                                              inventory_state_description,
                                              active,
                                              sort_order)
            VALUES (#{inventoryStateCode},
                    #{inventoryStateName},
                    #{inventoryStateDescription},
                    #{active},
                    #{sortOrder})
            ON DUPLICATE KEY UPDATE
                inventory_state_name = VALUES(inventory_state_name),
                inventory_state_description = VALUES(inventory_state_description),
                active = VALUES(active),
                sort_order = VALUES(sort_order)
            """)
    int upsert(ItemInventoryState itemInventoryState);
}
