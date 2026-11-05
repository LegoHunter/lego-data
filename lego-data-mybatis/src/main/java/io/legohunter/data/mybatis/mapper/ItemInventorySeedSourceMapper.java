package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventorySeedSource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface ItemInventorySeedSourceMapper {
    String ALL_COLUMNS = """
            iiss.item_inventory_id,
            iiss.source_system,
            iiss.source_key,
            iiss.seed_run_id,
            iiss.seeded_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_seed_source iiss ORDER BY iiss.item_inventory_id")
    @ResultMap("itemInventorySeedSourceResultMap")
    List<ItemInventorySeedSource> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_seed_source iiss WHERE iiss.item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventorySeedSourceResultMap")
    Optional<ItemInventorySeedSource> findByItemInventoryId(@Param("itemInventoryId") Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM item_inventory_seed_source iiss
            WHERE iiss.source_system = #{sourceSystem}
              AND iiss.source_key = #{sourceKey}
            """)
    @ResultMap("itemInventorySeedSourceResultMap")
    Optional<ItemInventorySeedSource> findBySourceSystemAndSourceKey(
            @Param("sourceSystem") String sourceSystem,
            @Param("sourceKey") String sourceKey
    );

    @Select("SELECT " + ALL_COLUMNS + """
            FROM item_inventory_seed_source iiss
            WHERE iiss.seed_run_id = #{seedRunId}
            ORDER BY iiss.item_inventory_id
            """)
    @ResultMap("itemInventorySeedSourceResultMap")
    List<ItemInventorySeedSource> findBySeedRunId(@Param("seedRunId") String seedRunId);

    @Insert("""
            INSERT INTO item_inventory_seed_source (item_inventory_id,
                                                    source_system,
                                                    source_key,
                                                    seed_run_id,
                                                    seeded_at)
            VALUES (#{itemInventoryId},
                    #{sourceSystem},
                    #{sourceKey},
                    #{seedRunId},
                    COALESCE(#{seededAt}, CURRENT_TIMESTAMP))
            """)
    int insert(ItemInventorySeedSource itemInventorySeedSource);

    @Delete("DELETE FROM item_inventory_seed_source WHERE item_inventory_id = #{itemInventoryId}")
    int deleteByItemInventoryId(@Param("itemInventoryId") Integer itemInventoryId);
}
