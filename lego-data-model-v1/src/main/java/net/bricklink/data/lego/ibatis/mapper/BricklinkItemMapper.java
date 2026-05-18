package net.bricklink.data.lego.ibatis.mapper;

import net.bricklink.data.lego.dto.BricklinkItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface BricklinkItemMapper {
    @Select("SELECT item_id, bl_item_id, bl_item_number FROM bricklink_item WHERE item_id = #{itemId}")
    @ResultMap("bricklinkItemResultMap")
    BricklinkItem getBricklinkItemForItemId(int itemId);

    @Select("SELECT item_id, bl_item_id, bl_item_number FROM bricklink_item WHERE bl_item_number = #{blItemNumber}")
    @ResultMap("bricklinkItemResultMap")
    Optional<BricklinkItem> getBricklinkItemForBricklinkItemNumber(String blItemNumber);
}
