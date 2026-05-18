package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkItemInventory;

import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface BricklinkItemInventoryMapper {
    String ALL_COLUMNS = """
             bii.bricklink_item_inventory_id,
             bii.external_item_id,
             bii.item_inventory_id,
             bii.inventory_id,
             bii.item_type,
             bii.color_id,
             bii.color_name,
             bii.quantity,
             bii.unit_price,
             bii.bind_id,
             bii.description,
             bii.remarks,
             bii.bulk,
             bii.is_retain,
             bii.is_stock_room,
             bii.stock_room_id,
             bii.date_created,
             bii.my_cost,
             bii.sale_rate,
             bii.tier_quantity1,
             bii.tier_quantity2,
             bii.tier_quantity3,
             bii.tier_price1,
             bii.tier_price2,
             bii.tier_price3,
             bii.my_weight
            """;

    @Select("SELECT " + ALL_COLUMNS + " " +
            "FROM bricklink_item_inventory bii " +
            "JOIN item_inventory ii on ii.item_inventory_id = bii.item_inventory_id AND ii.item_inventory_id = #{itemInventoryId} " +
            "JOIN external_item_inventory eii on eii.external_item_id = bii.external_item_id AND eii.item_inventory_id = ii.item_inventory_id AND eii.external_item_id = #{externalItemId}")
    @ResultMap("bricklinkItemInventoryResultMap")
    Optional<BricklinkItemInventory> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_item_inventory bii WHERE bii.bricklink_item_inventory_id = #{bricklink_item_inventory_id}")
    @ResultMap("bricklinkItemInventoryResultMap")
    Optional<BricklinkItemInventory> findByBricklinkItemInventoryId(Integer bricklinkItemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " " +
            "FROM bricklink_item_inventory bii " +
            "JOIN item_inventory ii on ii.item_inventory_id = bii.item_inventory_id " +
            "JOIN external_item_inventory eii on eii.external_item_id = bii.external_item_id " +
            "WHERE ii.uuid = #{uuid}")
    @ResultMap("bricklinkItemInventoryResultMap")
    Optional<BricklinkItemInventory> findByUuid(String uuid);

    @Insert("""
            INSERT INTO bricklink_item_inventory (
                     bricklink_item_inventory_id,
                     external_item_id,
                     item_inventory_id,
                     inventory_id,
                     item_type,
                     color_id,
                     color_name,
                     quantity,
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
                     my_weight)
            VALUES (#{bricklinkItemInventoryId},
                    #{externalItemId},
                    #{itemInventoryId},
                    #{inventoryId},
                    #{itemType},
                    #{colorId},
                    #{colorName},
                    #{quantity},
                    #{unitPrice},
                    #{bindId},
                    #{description},
                    #{remarks},
                    #{bulk},
                    #{isRetain},
                    #{isStockRoom},
                    #{stockRoomId},
                    #{dateCreated},
                    #{myCost},
                    #{saleRate},
                    #{tierQuantity1},
                    #{tierQuantity2},
                    #{tierQuantity3},
                    #{tierPrice1},
                    #{tierPrice2},
                    #{tierPrice3},
                    #{myWeight})\
            """)
    @Options(useGeneratedKeys = true, keyProperty = "bricklinkItemInventoryId")
    void insert(BricklinkItemInventory bricklinkItemInventory);

    @Update("""
            UPDATE bricklink_item_inventory \
            SET bricklink_item_inventory_id = #{bricklinkItemInventoryId},
                external_item_id            = #{externalItemId},
                item_inventory_id           = #{itemInventoryId},
                inventory_id                = #{inventoryId},
                item_type                   = #{itemType},
                color_id                    = #{colorId},
                color_name                  = #{colorName},
                quantity                    = #{quantity},
                unit_price                  = #{unitPrice},
                bind_id                     = #{bindId},
                description                 = #{description},
                remarks                     = #{remarks},
                bulk                        = #{bulk},
                is_retain                   = #{isRetain},
                is_stock_room               = #{isStockRoom},
                stock_room_id               = #{stockRoomId},
                date_created                = #{dateCreated},
                my_cost                     = #{myCost},
                sale_rate                   = #{saleRate},
                tier_quantity1              = #{tierQuantity1},
                tier_quantity2              = #{tierQuantity2},
                tier_quantity3              = #{tierQuantity3},
                tier_price1                 = #{tierPrice1},
                tier_price2                 = #{tierPrice2},
                tier_price3                 = #{tierPrice3},
                my_weight                   = #{myWeight} \
            WHERE bricklink_item_inventory_id = #{bricklinkItemInventoryId}
            """)
    void update(BricklinkItemInventory bricklinkItemInventory);
}