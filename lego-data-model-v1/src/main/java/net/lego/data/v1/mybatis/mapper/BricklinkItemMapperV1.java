package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.BricklinkItem;
import net.lego.data.v1.dto.Category;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface BricklinkItemMapperV1 {
    @Select("""
            SELECT item_id,\
                   bl_item_number,\
                   bl_item_id \
            FROM bricklink_item \
            """)
    @ResultMap("bricklinkItemResultMapV1")
    List<BricklinkItem> findAll();

    @Select("""
            SELECT item_id,\
                   bl_item_number,\
                   bl_item_id \
            FROM bricklink_item \
            WHERE item_id = #{itemId}\
            """)
    @ResultMap("bricklinkItemResultMapV1")
    Optional<BricklinkItem> findByItemId(Integer itemId);

    @Select("""
            SELECT item_id,\
                   bl_item_number,\
                   bl_item_id \
            FROM bricklink_item \
            WHERE bl_item_number = #{blItemNumber}\
            """)
    @ResultMap("bricklinkItemResultMapV1")
    Optional<BricklinkItem> findByBricklinkItemNumber(String blItemNumber);

    @Select("""
            SELECT item_id,\
                   bl_item_number,\
                   bl_item_id \
            FROM bricklink_item \
            WHERE bl_item_id = #{blItemId}\
            """)
    @ResultMap("bricklinkItemResultMapV1")
    Optional<BricklinkItem> findByBricklinkItemId(Long blItemId);
}
