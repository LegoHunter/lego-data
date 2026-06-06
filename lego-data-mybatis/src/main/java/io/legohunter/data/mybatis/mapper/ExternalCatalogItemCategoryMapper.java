package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItemCategory;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ExternalCatalogItemCategoryMapper {
    String ALL_COLUMNS = """
            external_catalog_item_id,
            external_category_id,
            is_primary,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_catalog_item_category")
    @ResultMap("externalCatalogItemCategoryResultMap")
    Set<ExternalCatalogItemCategory> findAll();

    @Select("""
            SELECT external_catalog_item_id,
                   external_category_id,
                   is_primary,
                   created_at
            FROM external_catalog_item_category
            WHERE external_catalog_item_id = #{externalCatalogItemId}
              AND external_category_id = #{externalCategoryId}
            """)
    @ResultMap("externalCatalogItemCategoryResultMap")
    Optional<ExternalCatalogItemCategory> findByExternalCatalogItemIdAndExternalCategoryId(
            @Param("externalCatalogItemId") Integer externalCatalogItemId,
            @Param("externalCategoryId") Integer externalCategoryId
    );

    @Select("SELECT " + ALL_COLUMNS + " FROM external_catalog_item_category WHERE external_catalog_item_id = #{externalCatalogItemId}")
    @ResultMap("externalCatalogItemCategoryResultMap")
    Set<ExternalCatalogItemCategory> findByExternalCatalogItemId(Integer externalCatalogItemId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_catalog_item_category WHERE external_category_id = #{externalCategoryId}")
    @ResultMap("externalCatalogItemCategoryResultMap")
    Set<ExternalCatalogItemCategory> findByExternalCategoryId(Integer externalCategoryId);

    @Insert("""
            INSERT INTO external_catalog_item_category (external_catalog_item_id,
                                                        external_category_id,
                                                        is_primary,
                                                        created_at)
            VALUES (#{externalCatalogItemId},
                    #{externalCategoryId},
                    #{primary},
                    COALESCE(#{createdAt}, CURRENT_TIMESTAMP))
            """)
    int insert(ExternalCatalogItemCategory externalCatalogItemCategory);

    @Update("""
            UPDATE external_catalog_item_category
            SET is_primary = #{primary},
                created_at = COALESCE(#{createdAt}, created_at)
            WHERE external_catalog_item_id = #{externalCatalogItemId}
              AND external_category_id = #{externalCategoryId}
            """)
    int update(ExternalCatalogItemCategory externalCatalogItemCategory);

    @Delete("""
            DELETE FROM external_catalog_item_category
            WHERE external_catalog_item_id = #{externalCatalogItemId}
              AND external_category_id = #{externalCategoryId}
            """)
    int delete(@Param("externalCatalogItemId") Integer externalCatalogItemId, @Param("externalCategoryId") Integer externalCategoryId);

    default int upsert(ExternalCatalogItemCategory externalCatalogItemCategory) {
        return findByExternalCatalogItemIdAndExternalCategoryId(externalCatalogItemCategory.getExternalCatalogItemId(), externalCatalogItemCategory.getExternalCategoryId())
                .map(existing -> update(externalCatalogItemCategory))
                .orElseGet(() -> insert(externalCatalogItemCategory));
    }
}
