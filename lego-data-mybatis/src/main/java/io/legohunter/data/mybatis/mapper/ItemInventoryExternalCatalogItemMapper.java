package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ItemInventoryExternalCatalogItemMapper {
    String ALL_COLUMNS = """
            iieci.item_inventory_id,
            iieci.external_catalog_item_id,
            iieci.is_primary,
            iieci.created_at,
            eci.external_catalog_item_id AS eci_external_catalog_item_id,
            eci.external_service_id AS eci_external_service_id,
            eci.external_item_key AS eci_external_item_key,
            eci.external_unique_key AS eci_external_unique_key,
            eci.item_name AS eci_item_name,
            eci.item_type_code AS eci_item_type_code,
            eci.item_url AS eci_item_url,
            eci.year_released AS eci_year_released,
            es.external_service_id AS eci_es_external_service_id,
            es.service_code AS eci_es_service_code,
            es.display_name AS eci_es_display_name,
            es.service_url AS eci_es_service_url,
            es.external_service_type_id AS eci_es_external_service_type_id,
            est.external_service_type_id AS eci_es_est_external_service_type_id,
            est.external_service_type_name AS eci_es_est_external_service_type_name,
            est.external_service_type_description AS eci_es_est_external_service_type_description
            """;

    String FROM_CLAUSE = """
            FROM item_inventory_external_catalog_item iieci
            JOIN external_catalog_item eci
                ON eci.external_catalog_item_id = iieci.external_catalog_item_id
            JOIN external_service es
                ON es.external_service_id = eci.external_service_id
            JOIN external_service_type est
                ON est.external_service_type_id = es.external_service_type_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE)
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Set<ItemInventoryExternalCatalogItem> findAll();

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE iieci.item_inventory_id = #{itemInventoryId}
              AND iieci.external_catalog_item_id = #{externalCatalogItemId}
            """)
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Optional<ItemInventoryExternalCatalogItem> findByItemInventoryIdAndExternalCatalogItemId(
            @Param("itemInventoryId") Integer itemInventoryId,
            @Param("externalCatalogItemId") Integer externalCatalogItemId,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ItemInventoryExternalCatalogItem> findByItemInventoryIdAndExternalCatalogItemId(Integer itemInventoryId, Integer externalCatalogItemId) {
        return findByItemInventoryIdAndExternalCatalogItemId(itemInventoryId, externalCatalogItemId, ALL_COLUMNS, FROM_CLAUSE);
    }

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE + " WHERE iieci.item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventoryExternalCatalogItemResultMap")
    Set<ItemInventoryExternalCatalogItem> findByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " " + FROM_CLAUSE + " WHERE iieci.external_catalog_item_id = #{externalCatalogItemId}")
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

    @Insert("""
            INSERT INTO item_inventory_external_catalog_item (item_inventory_id,
                                                              external_catalog_item_id,
                                                              is_primary,
                                                              created_at)
            VALUES (#{itemInventoryId},
                    #{externalCatalogItemId},
                    #{primary},
                    COALESCE(#{createdAt}, CURRENT_TIMESTAMP))
            ON DUPLICATE KEY UPDATE
                is_primary = VALUES(is_primary),
                created_at = COALESCE(#{createdAt}, created_at)
            """)
    int upsert(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem);
}
