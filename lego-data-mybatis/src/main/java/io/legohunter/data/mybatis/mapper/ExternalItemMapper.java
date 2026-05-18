package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalItem;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ExternalItemMapper {

    String ALL_COLUMNS = """
             ei.external_item_id,
             ei.external_number,
             ei.external_unique_id,
             ei.external_name,
             ei.external_item_type,
             ei.external_url,
             ei.external_year_released,
             ei.external_category_id,
             c.category_name,
             c.parent_id,
             es.external_service_id,
             es.external_service_name,
             es.external_service_url,
             es.external_service_type_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " " +
          """        
          FROM external_item ei
          JOIN category c on c.external_category_id = ei.external_category_id
          JOIN external_service es on es.external_service_id = c.external_service_id AND c.external_category_id = ei.external_category_id
          WHERE es.external_service_name = #{externalServiceName}
          """)
    @ResultMap("externalItemResultMap")
    Set<ExternalItem> findAllByExternalService(String externalServiceName);

    @Select("SELECT " + ALL_COLUMNS + " " +
            """        
            FROM external_item ei
            JOIN category c on c.external_category_id = ei.external_category_id
            JOIN external_service es on es.external_service_id = c.external_service_id
            WHERE ei.external_item_id = #{externalItemId}
            """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalItemId(Integer externalItemId);

    @Select("SELECT " + ALL_COLUMNS + " " +
            """        
            FROM external_item ei
            JOIN category c on c.external_category_id = ei.external_category_id
            JOIN external_service es on es.external_service_id = c.external_service_id
            WHERE ei.external_unique_id = #{externalUniqueId}
            AND es.external_service_id = #{externalServiceId}
            """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalServiceAndUniqueId(Integer externalServiceId, Integer externalUniqueId);

    @Select("SELECT " + ALL_COLUMNS + " " +
            """        
            FROM external_item ei
            JOIN category c on c.external_category_id = ei.external_category_id
            JOIN external_service es on es.external_service_id = c.external_service_id
            WHERE ei.external_number = #{externalNumber}
            AND es.external_service_id = #{externalServiceId}
            """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalServiceAndNumber(Integer externalServiceId, String externalNumber);

    @Insert("""
            INSERT INTO external_item (external_number, external_unique_id, external_name, external_item_type, external_url, external_category_id, external_year_released, external_service_id) \
            VALUES (#{externalNumber}, #{uniqueId}, #{name}, #{itemType}, #{url}, #{categoryId}, #{yearReleased}, #{serviceId}) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "externalItemId")
    void insert(ExternalItem externalItem);

    @Update("""
          UPDATE external_item
          SET external_number = #{externalNumber}, \
              external_unique_id = #{uniqueId}, \
              external_name = #{name}, \
              external_item_type = #{itemType}, \
              external_url = #{url}, \
              external_category_id = #{categoryId}, \
              external_year_released = #{yearReleased} \
          WHERE external_item_id = #{externalItemId} \
          """)
    void update(ExternalItem externalItem);

    @Insert("""
    INSERT INTO external_item (
        external_number,
        external_unique_id,
        external_name,
        external_item_type,
        external_url,
        external_category_id,
        external_year_released,
        external_service_id
    )
    VALUES (
        #{externalNumber},
        #{uniqueId},
        #{name},
        #{itemType},
        #{url},
        #{categoryId},
        #{yearReleased},
        #{serviceId}
    )
    ON DUPLICATE KEY UPDATE
        external_unique_id = VALUES(external_unique_id),
        external_name = VALUES(external_name),
        external_item_type = VALUES(external_item_type),
        external_url = VALUES(external_url),
        external_category_id = VALUES(external_category_id),
        external_year_released = VALUES(external_year_released)
    """)
    @Options(useGeneratedKeys = true, keyProperty = "externalItemId")
    void upsert(ExternalItem externalItem);
}
