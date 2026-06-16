package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventorySaleIntent;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ItemInventorySaleIntentMapper {
    @Select("""
            SELECT sale_intent_code,
                   sale_intent_name,
                   sale_intent_description,
                   active,
                   sort_order
            FROM item_inventory_sale_intent
            """)
    @ResultMap("itemInventorySaleIntentResultMap")
    Set<ItemInventorySaleIntent> findAll();

    @Select("""
            SELECT sale_intent_code,
                   sale_intent_name,
                   sale_intent_description,
                   active,
                   sort_order
            FROM item_inventory_sale_intent
            WHERE sale_intent_code = #{saleIntentCode}
            """)
    @ResultMap("itemInventorySaleIntentResultMap")
    Optional<ItemInventorySaleIntent> findBySaleIntentCode(String saleIntentCode);

    @Insert("""
            INSERT INTO item_inventory_sale_intent (sale_intent_code,
                                                   sale_intent_name,
                                                   sale_intent_description,
                                                   active,
                                                   sort_order)
            VALUES (#{saleIntentCode},
                    #{saleIntentName},
                    #{saleIntentDescription},
                    #{active},
                    #{sortOrder})
            """)
    int insert(ItemInventorySaleIntent itemInventorySaleIntent);

    @Update("""
            UPDATE item_inventory_sale_intent
            SET sale_intent_name = #{saleIntentName},
                sale_intent_description = #{saleIntentDescription},
                active = #{active},
                sort_order = #{sortOrder}
            WHERE sale_intent_code = #{saleIntentCode}
            """)
    int update(ItemInventorySaleIntent itemInventorySaleIntent);

    @Delete("""
            DELETE FROM item_inventory_sale_intent
            WHERE sale_intent_code = #{saleIntentCode}
            """)
    int delete(String saleIntentCode);

    @Insert("""
            INSERT INTO item_inventory_sale_intent (sale_intent_code,
                                                   sale_intent_name,
                                                   sale_intent_description,
                                                   active,
                                                   sort_order)
            VALUES (#{saleIntentCode},
                    #{saleIntentName},
                    #{saleIntentDescription},
                    #{active},
                    #{sortOrder})
            ON DUPLICATE KEY UPDATE
                sale_intent_name = VALUES(sale_intent_name),
                sale_intent_description = VALUES(sale_intent_description),
                active = VALUES(active),
                sort_order = VALUES(sort_order)
            """)
    int upsert(ItemInventorySaleIntent itemInventorySaleIntent);
}
