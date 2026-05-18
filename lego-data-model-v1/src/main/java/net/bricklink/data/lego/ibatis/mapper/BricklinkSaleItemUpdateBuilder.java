package net.bricklink.data.lego.ibatis.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.stream.Collectors;

public class BricklinkSaleItemUpdateBuilder {
    public String updateBricklinkSaleItemSold(@Param("blItemId") Long blItemId, @Param("newOrUsed") String newOrUsed, @Param("currentlyForSaleInventoryIds") List<Integer> currentlyForSaleInventoryIds) {
        return new SQL() {{
            UPDATE("bricklink_sale_item bsi");
            SET("bsi.status = 'S'");
            WHERE("bsi.bl_item_id = #{blItemId}");
            WHERE("bsi.new_or_used = #{newOrUsed}");
            WHERE("bsi.inventory_id NOT IN ("+ currentlyForSaleInventoryIds.stream()
                                                      .map(String::valueOf)
                                                      .collect(Collectors.joining(",")) +")");
        }}.toString();
    }
}