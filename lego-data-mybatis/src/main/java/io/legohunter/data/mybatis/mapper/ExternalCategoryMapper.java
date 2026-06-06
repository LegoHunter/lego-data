package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCategory;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ExternalCategoryMapper {
    String ALL_COLUMNS = """
            external_category_id,
            external_service_id,
            external_category_key,
            category_name,
            parent_external_category_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_category")
    @ResultMap("externalCategoryResultMap")
    Set<ExternalCategory> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM external_category WHERE external_category_id = #{externalCategoryId}")
    @ResultMap("externalCategoryResultMap")
    Optional<ExternalCategory> findByExternalCategoryId(Integer externalCategoryId);

    @Select("""
            SELECT external_category_id,
                   external_service_id,
                   external_category_key,
                   category_name,
                   parent_external_category_id
            FROM external_category
            WHERE external_service_id = #{externalServiceId}
              AND external_category_key = #{externalCategoryKey}
            """)
    @ResultMap("externalCategoryResultMap")
    Optional<ExternalCategory> findByExternalServiceIdAndExternalCategoryKey(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("externalCategoryKey") String externalCategoryKey
    );

    @Insert("""
            INSERT INTO external_category (external_service_id,
                                           external_category_key,
                                           category_name,
                                           parent_external_category_id)
            VALUES (#{externalServiceId},
                    #{externalCategoryKey},
                    #{categoryName},
                    #{parentExternalCategoryId})
            """)
    @Options(useGeneratedKeys = true, keyColumn = "external_category_id", keyProperty = "externalCategoryId")
    int insert(ExternalCategory externalCategory);

    @Update("""
            UPDATE external_category
            SET external_service_id = #{externalServiceId},
                external_category_key = #{externalCategoryKey},
                category_name = #{categoryName},
                parent_external_category_id = #{parentExternalCategoryId}
            WHERE external_category_id = #{externalCategoryId}
            """)
    int update(ExternalCategory externalCategory);

    @Delete("DELETE FROM external_category WHERE external_category_id = #{externalCategoryId}")
    int delete(Integer externalCategoryId);

    @Insert("""
            INSERT INTO external_category (external_category_id,
                                           external_service_id,
                                           external_category_key,
                                           category_name,
                                           parent_external_category_id)
            VALUES (#{externalCategoryId},
                    #{externalServiceId},
                    #{externalCategoryKey},
                    #{categoryName},
                    #{parentExternalCategoryId})
            ON DUPLICATE KEY UPDATE
                external_service_id = VALUES(external_service_id),
                external_category_key = VALUES(external_category_key),
                category_name = VALUES(category_name),
                parent_external_category_id = VALUES(parent_external_category_id)
            """)
    @Options(useGeneratedKeys = true, keyColumn = "external_category_id", keyProperty = "externalCategoryId")
    int upsert(ExternalCategory externalCategory);
}
