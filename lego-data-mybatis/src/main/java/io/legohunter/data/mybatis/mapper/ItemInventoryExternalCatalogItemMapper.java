package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ItemInventoryExternalCatalogItemMapper {
    String ALL_COLUMNS = """
            item_inventory_id,
            external_catalog_item_id,
            is_primary,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_external_catalog_item")
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Set<ItemInventoryExternalCatalogItem> findAll();

    @Select("""
            SELECT item_inventory_id,
                   external_catalog_item_id,
                   is_primary,
                   created_at
            FROM item_inventory_external_catalog_item
            WHERE item_inventory_id = #{itemInventoryId}
              AND external_catalog_item_id = #{externalCatalogItemId}
            """)
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Optional<ItemInventoryExternalCatalogItem> findByItemInventoryIdAndExternalCatalogItemId(
            @Param("itemInventoryId") Integer itemInventoryId,
            @Param("externalCatalogItemId") Integer externalCatalogItemId
    );

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_external_catalog_item WHERE item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Set<ItemInventoryExternalCatalogItem> findByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_external_catalog_item WHERE external_catalog_item_id = #{externalCatalogItemId}")
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Set<ItemInventoryExternalCatalogItem> findByExternalCatalogItemId(Integer externalCatalogItemId);

    @Insert("""
            INSERT INTO item_inventory_external_catalog_item (item_inventory_id,
                                                              external_catalog_item_id,
                                                              is_primary,
                                                              created_at)
            VALUES (#{itemInventoryId},
                    #{externalCatalogItemId},
                    #{primary},
                    COALESCE(#{createdAt}, CURRENT_TIMESTAMP))
            """)
    int insert(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem);

    @Update("""
            UPDATE item_inventory_external_catalog_item
            SET is_primary = #{primary},
                created_at = COALESCE(#{createdAt}, created_at)
            WHERE item_inventory_id = #{itemInventoryId}
              AND external_catalog_item_id = #{externalCatalogItemId}
            """)
    int update(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem);

    @Delete("""
            DELETE FROM item_inventory_external_catalog_item
            WHERE item_inventory_id = #{itemInventoryId}
              AND external_catalog_item_id = #{externalCatalogItemId}
            """)
    int delete(@Param("itemInventoryId") Integer itemInventoryId, @Param("externalCatalogItemId") Integer externalCatalogItemId);

    default int upsert(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem) {
        return findByItemInventoryIdAndExternalCatalogItemId(itemInventoryExternalCatalogItem.getItemInventoryId(), itemInventoryExternalCatalogItem.getExternalCatalogItemId())
                .map(existing -> update(itemInventoryExternalCatalogItem))
                .orElseGet(() -> insert(itemInventoryExternalCatalogItem));
    }
}
