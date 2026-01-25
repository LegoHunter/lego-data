package net.bricklink.data.lego.ibatis.mapper;

import net.bricklink.data.lego.dto.BricklinkSaleItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface BricklinkSaleItemMapper {
    @Insert("""
            INSERT INTO bricklink_sale_item (bl_item_id, inventory_id, quantity, new_or_used, completeness, unit_price, country_code, description, has_extended_description, date_created, status) \
            VALUES (#{blItemId}, #{inventoryId}, #{quantity}, #{newOrUsed}, #{completeness}, #{unitPrice}, #{countryCode}, #{description}, #{hasExtendedDescription}, #{dateCreated}, 'C') \
            ON DUPLICATE KEY UPDATE \
                bl_sale_item_id = LAST_INSERT_ID(bl_sale_item_id),\
                quantity = #{quantity}, \
                new_or_used = #{newOrUsed}, \
                completeness = #{completeness}, \
                unit_price = #{unitPrice}, \
                country_code = #{countryCode}, \
                description = #{description}, \
                has_extended_description = #{hasExtendedDescription}, \
                status = 'C'\
            """)
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "blSaleItemId", before = false, resultType = Integer.class)
    Integer upsert(BricklinkSaleItem bricklinkSaleItem);


    @UpdateProvider(type = BricklinkSaleItemUpdateBuilder.class, method = "updateBricklinkSaleItemSold")
    void updateBricklinkSaleItemSold(@Param("blItemId") Long blItemId, @Param("newOrUsed") String newOrUsed, @Param("currentlyForSaleInventoryIds") List<Integer> currentlyForSaleInventoryIds);

    @Select("""
            select * \
            from bricklink_sale_item bsi \
            join bricklink_item bli on bli.bl_item_id = bsi.bl_item_id \
            join bricklink_inventory bi on bi.bl_item_number = bli.bl_item_number \
            where bsi.bl_item_id = #{blItemId} \
            and bsi.new_or_used = #{newOrUsed} \
            and ((bi.item_type = 'SET' and bsi.completeness = #{completeness}) OR (bi.item_type != 'SET')) \
            and not exists (select 1 from bricklink_inventory bi where bi.inventory_id = bsi.inventory_id) \
            order by bsi.unit_price\
            """)
    @ResultMap("bricklinkSaleItemResultMap")
    List<BricklinkSaleItem> getBrinklinkSaleItems(@Param("blItemId") Long blItemId, @Param("newOrUsed") String newOrUsed, @Param("completeness") String completeness);

    @Select("SELECT * from bricklink_sale_item bsi")
    @ResultMap("bricklinkSaleItemResultMap")
    List<BricklinkSaleItem> getAll();
}