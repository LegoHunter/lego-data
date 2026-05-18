package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.ExternalServiceItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

public interface ExternalServiceItemMapper {

    @Select("""
            SELECT external_item_id, item_inventory_id 
            FROM external_service_item
            WHERE external_item_id = #{externalItemId}
            AND item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("externalServiceItemResultMap")
    Optional<ExternalServiceItem> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId);

    @Select("""
            SELECT external_item_id, item_inventory_id
            FROM external_service_item
            WHERE external_item_id = #{externalItemId}
            """)
    @ResultMap("externalServiceItemResultMap")
    Optional<ExternalServiceItem> findByExternalItemId(Integer externalItemId);

    @Select("""
            SELECT external_item_id, item_id 
            FROM external_service_item
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("externalServiceItemResultMap")
    Optional<ExternalServiceItem> findByItemInventoryId(Integer itemInventoryId);

    @Insert("""
            INSERT INTO external_service_item (external_item_id, item_inventory_id)
            VALUES (#{externalItemId}, #{itemInventoryId})
            """)
    void insert(ExternalServiceItem externalServiceItem);

    @Delete("""
            DELETE FROM external_service_item 
            WHERE external_item_id = #{externalItemId}
            AND item_inventory_id = #{itemInventoryId}
            """)
    void delete(ExternalServiceItem externalServiceItem);
}
