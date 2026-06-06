package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ExternalCatalogItemMapper {
    String ALL_COLUMNS = """
            eci.external_catalog_item_id,
            eci.external_service_id,
            eci.external_item_key,
            eci.external_unique_key,
            eci.item_name,
            eci.item_type_code,
            eci.item_url,
            eci.year_released,
            es.service_code,
            es.display_name,
            es.service_url,
            es.external_service_type_id
            """;

    @Select("""
            SELECT ${columns}
            FROM external_catalog_item eci
            JOIN external_service es ON es.external_service_id = eci.external_service_id
            """)
    @ResultMap("externalCatalogItemResultMap")
    Set<ExternalCatalogItem> findAll(@Param("columns") String columns);

    default Set<ExternalCatalogItem> findAll() {
        return findAll(ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM external_catalog_item eci
            JOIN external_service es ON es.external_service_id = eci.external_service_id
            WHERE eci.external_catalog_item_id = #{externalCatalogItemId}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalCatalogItemId(@Param("externalCatalogItemId") Integer externalCatalogItemId, @Param("columns") String columns);

    default Optional<ExternalCatalogItem> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return findByExternalCatalogItemId(externalCatalogItemId, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM external_catalog_item eci
            JOIN external_service es ON es.external_service_id = eci.external_service_id
            WHERE eci.external_service_id = #{externalServiceId}
              AND eci.external_item_key = #{externalItemKey}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalItemKey(@Param("externalServiceId") Integer externalServiceId, @Param("externalItemKey") String externalItemKey, @Param("columns") String columns);

    default Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalItemKey(Integer externalServiceId, String externalItemKey) {
        return findByExternalServiceIdAndExternalItemKey(externalServiceId, externalItemKey, ALL_COLUMNS);
    }

    @Select("""
            SELECT ${columns}
            FROM external_catalog_item eci
            JOIN external_service es ON es.external_service_id = eci.external_service_id
            WHERE eci.external_service_id = #{externalServiceId}
              AND eci.external_unique_key = #{externalUniqueKey}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalUniqueKey(@Param("externalServiceId") Integer externalServiceId, @Param("externalUniqueKey") String externalUniqueKey, @Param("columns") String columns);

    default Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalUniqueKey(Integer externalServiceId, String externalUniqueKey) {
        return findByExternalServiceIdAndExternalUniqueKey(externalServiceId, externalUniqueKey, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO external_catalog_item (external_service_id,
                                               external_item_key,
                                               external_unique_key,
                                               item_name,
                                               item_type_code,
                                               item_url,
                                               year_released)
            VALUES (#{externalServiceId},
                    #{externalItemKey},
                    #{externalUniqueKey},
                    #{itemName},
                    #{itemTypeCode},
                    #{itemUrl},
                    #{yearReleased})
            """)
    @Options(useGeneratedKeys = true, keyColumn = "external_catalog_item_id", keyProperty = "externalCatalogItemId")
    int insert(ExternalCatalogItem externalCatalogItem);

    @Update("""
            UPDATE external_catalog_item
            SET external_service_id = #{externalServiceId},
                external_item_key = #{externalItemKey},
                external_unique_key = #{externalUniqueKey},
                item_name = #{itemName},
                item_type_code = #{itemTypeCode},
                item_url = #{itemUrl},
                year_released = #{yearReleased}
            WHERE external_catalog_item_id = #{externalCatalogItemId}
            """)
    int update(ExternalCatalogItem externalCatalogItem);

    @Delete("DELETE FROM external_catalog_item WHERE external_catalog_item_id = #{externalCatalogItemId}")
    int delete(Integer externalCatalogItemId);

    default int upsert(ExternalCatalogItem externalCatalogItem) {
        if (externalCatalogItem.getExternalCatalogItemId() != null && findByExternalCatalogItemId(externalCatalogItem.getExternalCatalogItemId()).isPresent()) {
            return update(externalCatalogItem);
        }
        return findByExternalServiceIdAndExternalItemKey(externalCatalogItem.getExternalServiceId(), externalCatalogItem.getExternalItemKey())
                .map(existing -> {
                    externalCatalogItem.setExternalCatalogItemId(existing.getExternalCatalogItemId());
                    return update(externalCatalogItem);
                })
                .orElseGet(() -> insert(externalCatalogItem));
    }
}
