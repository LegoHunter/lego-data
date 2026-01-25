package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.ExternalItem;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

public interface ExternalItemMapper {

    @Select("""
          SELECT ei.external_item_id, ei.external_number, ei.external_unique_id, ei.external_name, ei.external_item_type, ei.external_url, ei.external_service_id, ei.external_category_id, ei.external_year_released, esi.item_id 
          FROM external_item ei
          LEFT OUTER JOIN external_service_item esi on ei.external_item_id = esi.external_item_id          
          WHERE ei.external_item_id = #{itemId}
          """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalItemId(Integer externalItemId);

    @Select("""
          SELECT ei.external_item_id, ei.external_number, ei.external_unique_id, ei.external_name, ei.external_item_type, ei.external_url, ei.external_service_id, ei.external_category_id, ei.external_year_released, esi.item_id
          FROM external_item ei
          LEFT OUTER JOIN external_service_item esi on ei.external_item_id = esi.external_item_id
          WHERE esi.item_id = #{itemId}
          """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByItemId(Integer itemId);

    @Select("""
          SELECT ei.external_item_id, ei.external_number, ei.external_unique_id, ei.external_name, ei.external_item_type, ei.external_url, ei.external_service_id, ei.external_category_id, ei.external_year_released, esi.item_id
          FROM external_item ei
          LEFT OUTER JOIN external_service_item esi on ei.external_item_id = esi.external_item_id
          WHERE ei.external_unique_id = #{uniqueId}
          """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalUniqueId(Integer externalUniqueId);

    @Select("""
          SELECT ei.external_item_id, ei.external_number, ei.external_unique_id, ei.external_name, ei.external_item_type, ei.external_url, ei.external_service_id, ei.external_category_id, ei.external_year_released, esi.item_id
          FROM external_item ei
          LEFT OUTER JOIN external_service_item esi on ei.external_item_id = esi.external_item_id
          WHERE ei.external_number = #{number}
          """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalNumber(String externalNumber);

    @Select("""
          SELECT ei.external_item_id, ei.external_number, ei.external_unique_id, ei.external_name, ei.external_item_type, ei.external_url, ei.external_service_id, ei.external_category_id, ei.external_year_released
          FROM external_item ei
          JOIN external_service es on es.external_service_id = ei.external_service_id
          WHERE ei.external_number = #{externalNumber}
          AND es.external_service_name = #{externalServiceName}
          """)
    @ResultMap("externalItemResultMap")
    Optional<ExternalItem> findByExternalServiceNumber(String externalNumber, String externalServiceName);

    @Insert("""
            INSERT INTO external_item (external_number, external_unique_id, external_name, external_item_type, external_url, external_category_id, external_year_released, external_service_id) \
            VALUES (#{number}, #{uniqueId}, #{name}, #{itemType}, #{url}, #{categoryId}, #{yearReleased}, #{serviceId}) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "itemId")
    void insert(ExternalItem externalItem);

    @Update("""
          UPDATE external_item
          SET external_number = #{number}, \
              external_unique_id = #{uniqueId}, \
              external_name = #{name}, \
              external_item_type = #{itemType}, \
              external_url = #{url}, \
              external_category_id = #{categoryId}, \
              external_year_released = #{yearReleased} \
          WHERE external_item_id = #{itemId} \
          """)
    void update(ExternalItem externalItem);
}
