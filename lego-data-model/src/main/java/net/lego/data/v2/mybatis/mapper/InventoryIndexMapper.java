package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.InventoryIndex;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface InventoryIndexMapper {
    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index")
    @ResultMap("inventoryIndexResultMap")
    List<InventoryIndex> getAll();

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index WHERE item_number = #{itemNumber}")
    @ResultMap("inventoryIndexResultMap")
    Optional<InventoryIndex> findByItemNumber(String itemNumber);

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index WHERE item_number = #{itemNumber} and box_id = #{boxId} and box_index = #{boxIndex}")
    @ResultMap("inventoryIndexResultMap")
    Optional<InventoryIndex> findByBoxIdAndBoxIndexAndItemNumber(@Param("itemNumber") String itemNumber, @Param("boxId") Integer boxId, @Param("boxIndex") Integer boxIndex);

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index WHERE box_id = #{boxId} and box_index = #{boxIndex}")
    @ResultMap("inventoryIndexResultMap")
    Optional<InventoryIndex> findByBoxIdAndBoxIndex(@Param("boxId") Integer boxId, @Param("boxIndex") Integer boxIndex);

    @Select("""
            select box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id from inventory_index ii
            where not exists (select 1 from item i where i.item_number = ii.item_number)
            and ii.item_number not in ('no #','')
            and ii.item_number is not null
            """)
    @ResultMap("inventoryIndexResultMap")
    List<InventoryIndex> getAllWithNoItem();

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index where box_id = #{boxId}")
    @ResultMap("inventoryIndexResultMap")
    List<InventoryIndex> getAllForBox(int boxId);

    @Insert("""
            INSERT INTO inventory_index(box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id)
            VALUES (#{boxId}, #{boxIndex}, #{itemNumber}, #{boxName}, #{boxNumber}, #{sealed}, #{quantity}, #{description}, #{active}, #{movedToBoxId})
            """)
    void insert(InventoryIndex inventoryIndex);

    @Update("""
            UPDATE inventory_index
            SET    sealed = #{sealed},
                   quantity = #{quantity},
                   description = #{description},
                   box_id = #{boxId},
                   box_number = #{boxNumber},
                   box_index = #{boxIndex},
                   box_name = #{boxName},
                   active = #{active},
                   moved_to_box_id = #{movedToBoxId}
            WHERE  item_number = #{itemNumber}
            AND    box_id = #{boxId}
            AND    box_index = #{boxIndex}
            """)
    void update(InventoryIndex inventoryIndex);
}


