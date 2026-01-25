package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.BricklinkInventory;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface BricklinkInventoryMapperV1 {
    String ALL_COLUMNS = """
            bl_inventory_id,
            uuid,
            box_id,
            box_index,
            inventory_id,
            bl_item_number,
            order_id,
            item_type,
            color_id,
            color_name,
            quantity,
            new_or_used,
            completeness,
            unit_price,
            bind_id,
            description,
            remarks,
            bulk,
            is_retain,
            is_stock_room,
            stock_room_id,
            date_created,
            my_cost,
            sale_rate,
            tier_quantity1,
            tier_quantity2,
            tier_quantity3,
            tier_price1,
            tier_price2,
            tier_price3,
            my_weight,
            sealed,
            fixed_price,
            for_sale,
            built_once,
            box_condition_id,
            instructions_condition_id,
            internal_comments,
            update_timestamp,
            last_synchronized_timestamp,
            extended_description,
            extra_description
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_inventory")
    @ResultMap("bricklinkInventoryResultMapV1")
    List<BricklinkInventory> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_inventory WHERE bl_inventory_id = #{blInventoryId}")
    @ResultMap("bricklinkInventoryResultMapV1")
    Optional<BricklinkInventory> findById(Integer blInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_inventoryWHERE uuid = #{uuid}")
    @ResultMap("bricklinkInventoryResultMapV1")
    Optional<BricklinkInventory> findByUuid(String uuid);
}
