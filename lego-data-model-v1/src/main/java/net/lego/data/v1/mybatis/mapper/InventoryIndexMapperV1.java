package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.InventoryIndex;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface InventoryIndexMapperV1 {
    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index")
    @ResultMap("inventoryIndexResultMapV1")
    List<InventoryIndex> getAll();

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index WHERE item_number = #{itemNumber}")
    @ResultMap("inventoryIndexResultMapV1")
    Optional<InventoryIndex> findByItemNumber(String itemNumber);

    @Select("""
            select box_id, box_index, item_number, box_name, box_number, sealed, quantity, descriptio, active, moved_to_box_id from inventory_index ii \
            where not exists (select 1 from item i where i.item_number = ii.item_number) \
            and ii.item_number not in ('no #','') \
            and ii.item_number is not null\
            """)
    @ResultMap("inventoryIndexResultMapV1")
    List<InventoryIndex> getAllWithNoItem();

    @Select("SELECT box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id FROM inventory_index where box_id = #{boxId}")
    @ResultMap("inventoryIndexResultMapV1")
    List<InventoryIndex> getAllForBox(int boxId);

    @Insert("""
            INSERT INTO inventory_index(box_id, box_index, item_number, box_name, box_number, sealed, quantity, description, active, moved_to_box_id) \
            VALUES (#{boxId}, #{boxIndex}, #{itemNumber}, #{boxName}, #{boxNumber}, #{sealed}, #{quantity}, #{description}, #{active}, #{moved_to_box_id})\
            """)
    void insert(InventoryIndex inventoryIndex);

    @Update("""
            UPDATE inventory_index \
            SET    sealed = #{sealed},\
                   quantity = #{quantity},\
                   description = #{description}, \
                   box_number = #{boxNumber}, \
                   itemNumber = #{itemNumber}, \
                   active = #{active}, \
                   moved_to_box_id = #{movedToBoxId} \
            WHERE  boxIndex = #{boxIndex} \
            AND    box_id = #{boxId} \
            AND    box_index = #{boxIndex} \
            """)
    void update(InventoryIndex inventoryIndex);
}


