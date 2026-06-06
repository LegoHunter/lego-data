package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface BricklinkMarketplaceListingMapper {
    String ALL_COLUMNS = """
            marketplace_listing_id,
            bricklink_inventory_id,
            bricklink_inventory_status_code,
            bricklink_date_created,
            color_id,
            color_name,
            bind_id,
            bulk,
            is_retain,
            is_stock_room,
            stock_room_id,
            sale_rate,
            tier_quantity_1,
            tier_price_1,
            tier_quantity_2,
            tier_price_2,
            tier_quantity_3,
            tier_price_3,
            my_weight,
            remarks,
            last_remote_quantity
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Set<BricklinkMarketplaceListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Optional<BricklinkMarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing WHERE bricklink_inventory_id = #{bricklinkInventoryId}")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Optional<BricklinkMarketplaceListing> findByBricklinkInventoryId(Integer bricklinkInventoryId);

    @Insert("""
            INSERT INTO bricklink_marketplace_listing (
                marketplace_listing_id,
                bricklink_inventory_id,
                bricklink_inventory_status_code,
                bricklink_date_created,
                color_id,
                color_name,
                bind_id,
                bulk,
                is_retain,
                is_stock_room,
                stock_room_id,
                sale_rate,
                tier_quantity_1,
                tier_price_1,
                tier_quantity_2,
                tier_price_2,
                tier_quantity_3,
                tier_price_3,
                my_weight,
                remarks,
                last_remote_quantity
            )
            VALUES (
                #{marketplaceListingId},
                #{bricklinkInventoryId},
                #{bricklinkInventoryStatusCode},
                #{bricklinkDateCreated},
                #{colorId},
                #{colorName},
                #{bindId},
                #{bulk},
                #{isRetain},
                #{isStockRoom},
                #{stockRoomId},
                #{saleRate},
                #{tierQuantity1},
                #{tierPrice1},
                #{tierQuantity2},
                #{tierPrice2},
                #{tierQuantity3},
                #{tierPrice3},
                #{myWeight},
                #{remarks},
                #{lastRemoteQuantity}
            )
            """)
    int insert(BricklinkMarketplaceListing bricklinkMarketplaceListing);

    @Update("""
            UPDATE bricklink_marketplace_listing
            SET bricklink_inventory_id = #{bricklinkInventoryId},
                bricklink_inventory_status_code = #{bricklinkInventoryStatusCode},
                bricklink_date_created = #{bricklinkDateCreated},
                color_id = #{colorId},
                color_name = #{colorName},
                bind_id = #{bindId},
                bulk = #{bulk},
                is_retain = #{isRetain},
                is_stock_room = #{isStockRoom},
                stock_room_id = #{stockRoomId},
                sale_rate = #{saleRate},
                tier_quantity_1 = #{tierQuantity1},
                tier_price_1 = #{tierPrice1},
                tier_quantity_2 = #{tierQuantity2},
                tier_price_2 = #{tierPrice2},
                tier_quantity_3 = #{tierQuantity3},
                tier_price_3 = #{tierPrice3},
                my_weight = #{myWeight},
                remarks = #{remarks},
                last_remote_quantity = #{lastRemoteQuantity}
            WHERE marketplace_listing_id = #{marketplaceListingId}
            """)
    int update(BricklinkMarketplaceListing bricklinkMarketplaceListing);

    @Delete("DELETE FROM bricklink_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    int delete(Integer marketplaceListingId);

    @Insert("""
            INSERT INTO bricklink_marketplace_listing (
                marketplace_listing_id,
                bricklink_inventory_id,
                bricklink_inventory_status_code,
                bricklink_date_created,
                color_id,
                color_name,
                bind_id,
                bulk,
                is_retain,
                is_stock_room,
                stock_room_id,
                sale_rate,
                tier_quantity_1,
                tier_price_1,
                tier_quantity_2,
                tier_price_2,
                tier_quantity_3,
                tier_price_3,
                my_weight,
                remarks,
                last_remote_quantity
            )
            VALUES (
                #{marketplaceListingId},
                #{bricklinkInventoryId},
                #{bricklinkInventoryStatusCode},
                #{bricklinkDateCreated},
                #{colorId},
                #{colorName},
                #{bindId},
                #{bulk},
                #{isRetain},
                #{isStockRoom},
                #{stockRoomId},
                #{saleRate},
                #{tierQuantity1},
                #{tierPrice1},
                #{tierQuantity2},
                #{tierPrice2},
                #{tierQuantity3},
                #{tierPrice3},
                #{myWeight},
                #{remarks},
                #{lastRemoteQuantity}
            )
            ON DUPLICATE KEY UPDATE
                bricklink_inventory_id = VALUES(bricklink_inventory_id),
                bricklink_inventory_status_code = VALUES(bricklink_inventory_status_code),
                bricklink_date_created = VALUES(bricklink_date_created),
                color_id = VALUES(color_id),
                color_name = VALUES(color_name),
                bind_id = VALUES(bind_id),
                bulk = VALUES(bulk),
                is_retain = VALUES(is_retain),
                is_stock_room = VALUES(is_stock_room),
                stock_room_id = VALUES(stock_room_id),
                sale_rate = VALUES(sale_rate),
                tier_quantity_1 = VALUES(tier_quantity_1),
                tier_price_1 = VALUES(tier_price_1),
                tier_quantity_2 = VALUES(tier_quantity_2),
                tier_price_2 = VALUES(tier_price_2),
                tier_quantity_3 = VALUES(tier_quantity_3),
                tier_price_3 = VALUES(tier_price_3),
                my_weight = VALUES(my_weight),
                remarks = VALUES(remarks),
                last_remote_quantity = VALUES(last_remote_quantity)
            """)
    int upsert(BricklinkMarketplaceListing bricklinkMarketplaceListing);
}
