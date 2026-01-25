package net.lego.data.v1.mybatis.mapper;

import net.bricklink.data.lego.dto.BricklinkItem;
import net.bricklink.data.lego.ibatis.mapper.ItemUpdateBuilder;
import net.lego.data.v1.dto.Item;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ItemMapperV1 {
    @Select("""
            SELECT i.item_id,\
                   i.item_number,\
                   i.item_name,\
                   i.number_of_pieces,\
                   i.issue_year,\
                   i.issue_location,\
                   i.theme_id,\
                   i.item_type_code,\
                   i.notes, \
                   coalesce(c.category_id, 0) as "category_id", \
                   c.category_type, \
                   c.category_name \
            FROM item i\
            LEFT OUTER JOIN item_category ic on ic.item_id = i.item_id \
            LEFT OUTER JOIN category c on c.category_id = ic.category_id \
            """)
    @ResultMap("itemResultMap")
    List<Item> getAll();

    @Select("""
            SELECT i.item_id,\
                   i.item_number,\
                   i.item_name,\
                   i.number_of_pieces,\
                   i.issue_year,\
                   i.issue_location,\
                   i.theme_id,\
                   i.item_type_code,\
                   i.notes, \
                   coalesce(c.category_id, 0) as "category_id", \
                   c.category_type, \
                   c.category_name \
            FROM item i \
            LEFT OUTER JOIN item_category ic on ic.item_id = i.item_id \
            LEFT OUTER JOIN category c on c.category_id = ic.category_id \
            """)
    @ResultMap("itemResultMap")
    List<Item> findAll();

    @Select("""
            SELECT * \
            FROM item i \
            WHERE NOT EXISTS (SELECT 1 FROM bricklink_item bi WHERE bi.item_id = i.item_id)\
            """)
    @ResultMap("itemResultMap")
    List<Item> getAllWithNoBricklinkItem();

    @Insert("""
            INSERT INTO bricklink_item(bl_item_id, bl_item_number, item_id) \
            VALUES (#{blItemId}, #{blItemNumber}, #{itemId}) \
            ON DUPLICATE KEY UPDATE \
                bl_item_number = #{blItemNumber}, \
                item_id = #{itemId}\
            """)
    void insertBricklinkItem(BricklinkItem bricklinkItem);

    @Insert("""
            INSERT INTO item (item_number, item_name, number_of_pieces, issue_year, issue_location, theme_id, item_type_code, notes) \
            VALUES (#{itemNumber}, #{itemName}, #{numberOfPieces}, #{issueYear}, #{issueLocation}, #{themeId}, #{itemTypeCode}, #{notes})\
            """)
    @Options(useGeneratedKeys=true, keyProperty="itemId")
    void insertItem(Item item);

    @Select("""
            SELECT item_id,\
                   item_number,\
                   item_name,\
                   number_of_pieces,\
                   issue_year,\
                   issue_location,\
                   theme_id,\
                   item_type_code,\
                   notes \
            FROM item \
            WHERE item_id = #{itemId}\
            """)
    @ResultMap("itemResultMap")
    Item findItemById(int itemId);

    @Select("""
            SELECT item_id,\
                   item_number,\
                   item_name,\
                   number_of_pieces,\
                   issue_year,\
                   issue_location,\
                   theme_id,\
                   item_type_code,\
                   notes \
            FROM item \
            WHERE item_number = #{itemNumber}\
            """)
    @ResultMap("itemResultMap")
    List<Item> findItemByNumber(String itemNumber);

    @UpdateProvider(type= ItemUpdateBuilder.class, method="updateItem")
    void updateItem(Item item);
}
