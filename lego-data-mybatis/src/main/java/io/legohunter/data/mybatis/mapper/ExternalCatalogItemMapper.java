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
            es.external_service_id AS es_external_service_id,
            es.service_code AS es_service_code,
            es.display_name AS es_display_name,
            es.service_url AS es_service_url,
            es.external_service_type_id AS es_external_service_type_id,
            est.external_service_type_id AS es_est_external_service_type_id,
            est.external_service_type_name AS es_est_external_service_type_name,
            est.external_service_type_description AS es_est_external_service_type_description,
            esc.external_service_id AS es_esc_external_service_id,
            esc.capability_code AS es_esc_capability_code
            """;

    String FROM_CLAUSE = """
            FROM external_catalog_item eci
            JOIN external_service es
                ON es.external_service_id = eci.external_service_id
            JOIN external_service_type est
                ON est.external_service_type_id = es.external_service_type_id
            LEFT JOIN external_service_capability esc
                ON esc.external_service_id = es.external_service_id
            """;

    @Select("""
            SELECT ${columns}
            ${fromClause}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Set<ExternalCatalogItem> findAll(@Param("columns") String columns, @Param("fromClause") String fromClause);

    default Set<ExternalCatalogItem> findAll() {
        return findAll(ALL_COLUMNS, FROM_CLAUSE);
    }

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE eci.external_catalog_item_id = #{externalCatalogItemId}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalCatalogItemId(
            @Param("externalCatalogItemId") Integer externalCatalogItemId,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ExternalCatalogItem> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return findByExternalCatalogItemId(externalCatalogItemId, ALL_COLUMNS, FROM_CLAUSE);
    }

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE eci.external_service_id = #{externalServiceId}
              AND eci.external_item_key = #{externalItemKey}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalItemKey(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("externalItemKey") String externalItemKey,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalItemKey(Integer externalServiceId, String externalItemKey) {
        return findByExternalServiceIdAndExternalItemKey(externalServiceId, externalItemKey, ALL_COLUMNS, FROM_CLAUSE);
    }

    @Select("""
            SELECT ${columns}
            ${fromClause}
            WHERE eci.external_service_id = #{externalServiceId}
              AND eci.external_unique_key = #{externalUniqueKey}
            """)
    @ResultMap("externalCatalogItemResultMap")
    Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalUniqueKey(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("externalUniqueKey") String externalUniqueKey,
            @Param("columns") String columns,
            @Param("fromClause") String fromClause
    );

    default Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalUniqueKey(Integer externalServiceId, String externalUniqueKey) {
        return findByExternalServiceIdAndExternalUniqueKey(externalServiceId, externalUniqueKey, ALL_COLUMNS, FROM_CLAUSE);
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

    @Insert("""
            INSERT INTO external_catalog_item (external_catalog_item_id,
                                               external_service_id,
                                               external_item_key,
                                               external_unique_key,
                                               item_name,
                                               item_type_code,
                                               item_url,
                                               year_released)
            VALUES (#{externalCatalogItemId},
                    #{externalServiceId},
                    #{externalItemKey},
                    #{externalUniqueKey},
                    #{itemName},
                    #{itemTypeCode},
                    #{itemUrl},
                    #{yearReleased})
            ON DUPLICATE KEY UPDATE
                external_service_id = VALUES(external_service_id),
                external_item_key = VALUES(external_item_key),
                external_unique_key = VALUES(external_unique_key),
                item_name = VALUES(item_name),
                item_type_code = VALUES(item_type_code),
                item_url = VALUES(item_url),
                year_released = VALUES(year_released)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "external_catalog_item_id", keyProperty = "externalCatalogItemId")
    int upsert(ExternalCatalogItem externalCatalogItem);
}
