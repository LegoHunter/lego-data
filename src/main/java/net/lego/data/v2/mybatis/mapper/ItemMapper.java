package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Item;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface ItemMapper {
    @Select("""
          SELECT item_id, \
                 item_number, \
                 item_name, \
                 notes, \
                 is_obsolete \
          FROM item \
          """)
    @ResultMap("itemResultMap")
    List<Item> findAll();

    @Select("""
          SELECT item_id, \
                 item_number, \
                 item_name, \
                 notes, \
                 is_obsolete \
          FROM item \
          WHERE item_id = #{itemId} \
            """)
    @ResultMap("itemResultMap")
    Optional<Item> findByItemId(Integer itemId);

    @Select("""
          SELECT item_id, \
                 item_number, \
                 item_name, \
                 notes, \
                 is_obsolete \
          FROM item \
          WHERE item_number = #{itemNumber} \
            """)
    @ResultMap("itemResultMap")
    Optional<Item> findByItemNumber(String itemNumber);

    @Insert("""
            INSERT INTO item (item_id, item_number, item_name, notes, is_obsolete) \
            VALUES (#{itemId}, #{itemNumber}, #{itemName}, #{notes}, #{isObsolete}) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "itemId")
    void insert(Item item);

    @Insert("""
            INSERT INTO item (item_id, item_number, item_name, notes, is_obsolete) \
            VALUES (#{itemId}, #{itemNumber}, #{itemName}, #{notes}, #{isObsolete}) \
            """)
    void migrate(Item item);

    @Update("""
          UPDATE item
          SET item_number = #{itemNumber}, \
              item_name = #{itemName}, \
              notes = #{notes}, \
              category_id = #{is_obsolete} \
          WHERE item_id = #{itemId} \
          """)
    void update(Item item);
}
