package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalItemInventory;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface ExternalItemInventoryMapper {
    @Select("""
            SELECT external_item_id,
                   item_inventory_id,
                   fixed_price,
                   order_id,
                   extended_description,
                   extra_description,
                   internal_comments,
                   update_timestamp,
                   last_synchronized_timestamp
            FROM external_item_inventory
            WHERE external_item_id = #{externalItemId}
            AND item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("externalItemInventoryResultMap")
    Optional<ExternalItemInventory> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId);

    @Select("""
            SELECT external_item_id,
                   item_inventory_id,
                   fixed_price,
                   order_id,
                   extended_description,
                   extra_description,
                   internal_comments,
                   update_timestamp,
                   last_synchronized_timestamp
            FROM external_item_inventory
            WHERE external_item_id = #{externalItemId}
            """)
    @ResultMap("externalItemInventoryResultMap")
    List<ExternalItemInventory> findByExternalItemId(Integer externalItemId);

    @Select("""
            SELECT external_item_id,
                   item_inventory_id,
                   fixed_price,
                   order_id,
                   extended_description,
                   extra_description,
                   internal_comments,
                   update_timestamp,
                   last_synchronized_timestamp \
            FROM external_item_inventory \
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("externalItemInventoryResultMap")
    List<ExternalItemInventory> findByItemInventoryId(Integer itemInventoryId);

    @Insert("""
            INSERT INTO external_item_inventory (
                external_item_id,
                item_inventory_id,
                fixed_price,
                order_id,
                extended_description,
                extra_description,
                internal_comments,
                update_timestamp,
                last_synchronized_timestamp)
            VALUES (#{externalItemId}, #{itemInventoryId}, #{fixedPrice}, #{orderId}, #{extendedDescription}, #{extraDescription}, #{internalComments}, #{updateTimestamp}, #{lastSynchronizedTimestamp})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "itemId")
    void insert(ExternalItemInventory externalItemInventory);

    @Update("""
            UPDATE external_item_inventory
            SET external_item_id            = #{externalItemId},
                item_inventory_id           = #{itemInventoryId},
                fixed_price                 = #{fixedPrice},
                order_id                    = #{orderId},
                extended_description        = #{extendedDescription},
                extra_description           = #{extraDescription},
                internal_comments           = #{internalComments},
                update_timestamp            = #{updateTimestamp},
                last_synchronized_timestamp = #{lastSynchronizedTimestamp} \
            WHERE external_item_id = #{externalItemId}
            """)
    void update(ExternalItemInventory externalItemInventory);
}