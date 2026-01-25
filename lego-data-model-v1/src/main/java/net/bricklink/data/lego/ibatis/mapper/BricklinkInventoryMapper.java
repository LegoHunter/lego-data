package net.bricklink.data.lego.ibatis.mapper;

import net.bricklink.data.lego.dto.BricklinkInventory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BricklinkInventoryMapper {
    String INVENTORY_COLUMNS =
            """
            bi.bl_inventory_id,\
            bi.uuid,\
            bi.inventory_id,\
            bi.order_id,\
            bi.box_id,\
            bi.box_index,\
            bi.bl_item_number,\
            i.item_number,\
            i.item_name,\
            bli.bl_item_number,\
            bli.bl_item_id,\
            bi.item_type,\
            bi.quantity,\
            bi.new_or_used,\
            bi.completeness,\
            bi.unit_price,\
            bi.description,\
            bi.remarks,\
            bi.bulk,\
            bi.is_retain,\
            bi.is_stock_room,\
            bi.stock_room_id,\
            bi.date_created,\
            bi.my_cost,\
            bi.my_weight,\
            bi.sealed,\
            bi.built_once,\
            bi.for_sale,\
            bi.fixed_price,\
            bi.box_condition_id,\
            bi.instructions_condition_id,\
            bi.internal_comments,\
            bi.update_timestamp,\
            bi.last_synchronized_timestamp,\
            bi.internal_comments,\
            bi.extended_description,\
            bi.extra_description \
            """;

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.bl_inventory_id = #{blInventoryId}")
    @ResultMap("bricklinkInventoryWorkResultMap")
    BricklinkInventory get(Integer blInventoryId);

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.uuid = #{uuid}")
    @ResultMap("bricklinkInventoryWorkResultMap")
    BricklinkInventory getByUuid(String uuid);

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.inventory_id = #{inventoryId}")
    @ResultMap("bricklinkInventoryWorkResultMap")
    BricklinkInventory getByInventoryId(Long inventoryId);

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.box_id = #{boxId} " +
            "AND   bi.box_index = #{boxIndex}")
    @ResultMap("bricklinkInventoryWorkResultMap")
    Optional<BricklinkInventory> findByBoxIdAndBoxIndex(Integer boxId, Integer boxIndex);

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.bl_item_number = #{blItemNumber}")
    @ResultMap("bricklinkInventoryWorkResultMap")
    List<BricklinkInventory> findByBricklinkitemNumber(String blItemNumber);

    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id")
    @ResultMap("bricklinkInventoryWorkResultMap")
    List<BricklinkInventory> getAll();


    @Select("SELECT " + INVENTORY_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
            "JOIN item i ON i.item_id = bli.item_id " +
            "WHERE bi.for_sale = true " +
            "AND bi.order_id is null")
    @ResultMap("bricklinkInventoryWorkResultMap")
    List<BricklinkInventory> getAllForSale();

        @Select("SELECT " + INVENTORY_COLUMNS + " " +
                "FROM bricklink_inventory bi " +
                "JOIN bricklink_item bli ON bi.bl_item_number = bli.bl_item_number " +
                "JOIN item i ON i.item_id = bli.item_id " +
                "WHERE (bi.last_synchronized_timestamp < CURRENT_TIMESTAMP OR bi.last_synchronized_timestamp IS NULL) " +
                "AND bi.item_type IN ('SET','GEAR', 'BOOK') " +
                "AND bi.order_id IS NULL " +
                "AND bi.for_sale = true")
    @ResultMap("bricklinkInventoryWorkResultMap")
    List<BricklinkInventory> getInventoryWork();

    @Update("""
            UPDATE bricklink_inventory SET \
            inventory_id = #{inventoryId},\
            order_id = #{orderId},\
            quantity = #{quantity},\
            new_or_used = #{newOrUsed},\
            completeness = #{completeness},\
            unit_price = #{unitPrice},\
            description = #{description},\
            remarks = #{uuid},\
            is_stock_room = #{isStockRoom},\
            stock_room_id = #{stockRoomId},\
            date_created = #{dateCreated},\
            my_cost = #{myCost},\
            my_weight = #{myWeight},\
            sealed = #{sealed},\
            built_once = #{builtOnce},\
            box_condition_id = #{boxConditionId},\
            instructions_condition_id = #{instructionsConditionId},\
            internal_comments = #{internalComments},\
            last_synchronized_timestamp = #{lastSynchronizedTimestamp} \
            WHERE bl_inventory_id = #{blInventoryId}\
            """)
    void update(BricklinkInventory bricklinkInventory);

    @Update("""
            UPDATE bricklink_inventory SET \
            last_synchronized_timestamp = CURRENT_TIMESTAMP \
            WHERE bl_inventory_id = #{blInventoryId}\
            """)
    void setSynchronizedNow(Integer blInventoryId);

    @Update("""
            UPDATE bricklink_inventory SET \
            for_sale = false \
            WHERE bl_inventory_id = #{blInventoryId}\
            """)
    void setNotForSale(Integer blInventoryId);

    @Update("""
            UPDATE bricklink_inventory SET \
            unit_price = #{unitPrice} \
            WHERE bl_inventory_id = #{blInventoryId} \
            AND fixed_price = false\
            """)
    void setPrice(@Param("blInventoryId") Integer blInventoryId, @Param("unitPrice") double unitPrice);

    @UpdateProvider(type=BricklinkInventoryUpdateBuilder.class, method="updateBricklinkInventoryByUuidAndBlItemNumber")
    void updateFromImageKeywords(BricklinkInventory bricklinkInventory);

    @Update("""
            UPDATE bricklink_inventory SET \
            order_id = #{orderId} \
            WHERE bl_inventory_id = #{blInventoryId}\
            """)
    void updateOrder(@Param("blInventoryId") Integer blInventoryId, @Param("orderId") String orderId);

    @Insert("""
            insert into bricklink_inventory (uuid, box_id, box_index, bl_item_number, inventory_id, item_type, color_id, color_name,
                                             quantity, new_or_used, completeness, unit_price, bind_id, description, remarks, bulk,
                                             is_retain, is_stock_room, stock_room_id, my_cost, sale_rate, tier_quantity1,
                                             tier_quantity2, tier_quantity3, tier_price1, tier_price2, tier_price3, my_weight,
                                             sealed, order_id, fixed_price, for_sale, built_once, box_condition_id,
                                             instructions_condition_id, internal_comments, extended_description, extra_description)
            select md5(concat(#{boxId}, #{boxIndex}, #{blItemNo}))  uuid,
                   #{boxId}                    box_id,
                   #{boxIndex}                 box_index,
                   #{blItemNo},
                   null                       inventory_id,
                   'SET'                      item_type,
                   0                          color_id,
                   null                       color_name,
                   1                          quantity,
                   'U'               new_or_used,
                   'C'              completeness,
                   0.00                       unit_price,
                   null                       bind_id,
                   'Contact me for photos!'   description,
                   md5(concat(#{boxId}, #{boxIndex}, #{blItemNo})) remarks,
                   1                          bulk,
                   1                          is_retain,
                   1                          is_stock_room,
                   'A'                        stock_room_id,
                   0.00                       my_cost,
                   null                       sale_rate,
                   null                       tier_quantity1,
                   null                       tier_quantity2,
                   null                       tier_quantity3,
                   null                       tier_price1,
                   null                       tier_price2,
                   null                       tier_price3,
                   null                       my_weight,
                   0                          sealed,
                   null                       order_id,
                   0                          fixed_price,
                   1                          for_sale,
                   1                          built_once,
                   null                       box_condition_id,
                   null                       instructions_condition_id,
                   null                       internal_comments,
                   null                       extended_description,
                   null                       extra_description
            from inventory_index ii
            where ii.box_id = #{boxId}
              and ii.box_index = #{boxIndex}
              and not exists(select 1 from bricklink_inventory bi where bi.box_id = #{boxId} and bi.box_index = #{boxIndex})\
            """)
    void insert(BricklinkInventory bricklinkInventory);
}

















