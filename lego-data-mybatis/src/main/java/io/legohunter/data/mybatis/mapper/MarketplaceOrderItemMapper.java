package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrderItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceOrderItemMapper {
    String ALL_COLUMNS = """
            marketplace_order_item_id,
            marketplace_order_id,
            marketplace_listing_id,
            item_inventory_id,
            external_order_item_id,
            external_inventory_id,
            external_item_no,
            external_item_type,
            color_id,
            color_name,
            quantity,
            condition_code,
            completeness_code,
            unit_price,
            final_unit_price,
            currency_code,
            item_weight,
            remarks,
            description,
            payload_hash,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_item")
    @ResultMap("marketplaceOrderItemResultMap")
    Set<MarketplaceOrderItem> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_item WHERE marketplace_order_item_id = #{marketplaceOrderItemId}")
    @ResultMap("marketplaceOrderItemResultMap")
    Optional<MarketplaceOrderItem> findByMarketplaceOrderItemId(Integer marketplaceOrderItemId);

    @Insert("""
            INSERT INTO marketplace_order_item (
                marketplace_order_id,
                marketplace_listing_id,
                item_inventory_id,
                external_order_item_id,
                external_inventory_id,
                external_item_no,
                external_item_type,
                color_id,
                color_name,
                quantity,
                condition_code,
                completeness_code,
                unit_price,
                final_unit_price,
                currency_code,
                item_weight,
                remarks,
                description,
                payload_hash,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderId},
                #{marketplaceListingId},
                #{itemInventoryId},
                #{externalOrderItemId},
                #{externalInventoryId},
                #{externalItemNo},
                #{externalItemType},
                #{colorId},
                #{colorName},
                #{quantity},
                #{conditionCode},
                #{completenessCode},
                #{unitPrice},
                #{finalUnitPrice},
                #{currencyCode},
                #{itemWeight},
                #{remarks},
                #{description},
                #{payloadHash},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderItemId")
    void insert(MarketplaceOrderItem marketplaceOrderItem);

    @Update("""
            UPDATE marketplace_order_item
            SET
                marketplace_order_id = #{marketplaceOrderId},
                marketplace_listing_id = #{marketplaceListingId},
                item_inventory_id = #{itemInventoryId},
                external_order_item_id = #{externalOrderItemId},
                external_inventory_id = #{externalInventoryId},
                external_item_no = #{externalItemNo},
                external_item_type = #{externalItemType},
                color_id = #{colorId},
                color_name = #{colorName},
                quantity = #{quantity},
                condition_code = #{conditionCode},
                completeness_code = #{completenessCode},
                unit_price = #{unitPrice},
                final_unit_price = #{finalUnitPrice},
                currency_code = #{currencyCode},
                item_weight = #{itemWeight},
                remarks = #{remarks},
                description = #{description},
                payload_hash = #{payloadHash},
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_order_item_id = #{marketplaceOrderItemId}
            """)
    int update(MarketplaceOrderItem marketplaceOrderItem);

    @Delete("DELETE FROM marketplace_order_item WHERE marketplace_order_item_id = #{marketplaceOrderItemId}")
    int delete(Integer marketplaceOrderItemId);

    @Insert("""
            INSERT INTO marketplace_order_item (
                marketplace_order_item_id,
                marketplace_order_id,
                marketplace_listing_id,
                item_inventory_id,
                external_order_item_id,
                external_inventory_id,
                external_item_no,
                external_item_type,
                color_id,
                color_name,
                quantity,
                condition_code,
                completeness_code,
                unit_price,
                final_unit_price,
                currency_code,
                item_weight,
                remarks,
                description,
                payload_hash,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderItemId},
                #{marketplaceOrderId},
                #{marketplaceListingId},
                #{itemInventoryId},
                #{externalOrderItemId},
                #{externalInventoryId},
                #{externalItemNo},
                #{externalItemType},
                #{colorId},
                #{colorName},
                #{quantity},
                #{conditionCode},
                #{completenessCode},
                #{unitPrice},
                #{finalUnitPrice},
                #{currencyCode},
                #{itemWeight},
                #{remarks},
                #{description},
                #{payloadHash},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_order_id = VALUES(marketplace_order_id),
                marketplace_listing_id = VALUES(marketplace_listing_id),
                item_inventory_id = VALUES(item_inventory_id),
                external_order_item_id = VALUES(external_order_item_id),
                external_inventory_id = VALUES(external_inventory_id),
                external_item_no = VALUES(external_item_no),
                external_item_type = VALUES(external_item_type),
                color_id = VALUES(color_id),
                color_name = VALUES(color_name),
                quantity = VALUES(quantity),
                condition_code = VALUES(condition_code),
                completeness_code = VALUES(completeness_code),
                unit_price = VALUES(unit_price),
                final_unit_price = VALUES(final_unit_price),
                currency_code = VALUES(currency_code),
                item_weight = VALUES(item_weight),
                remarks = VALUES(remarks),
                description = VALUES(description),
                payload_hash = VALUES(payload_hash),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderItemId")
    void upsert(MarketplaceOrderItem marketplaceOrderItem);
}
