package net.bricklink.data.lego.ibatis.mapper;

import net.bricklink.data.lego.dto.BricklinkInventory;
import org.apache.ibatis.jdbc.SQL;

import java.util.Optional;

public class BricklinkInventoryUpdateBuilder {
    public String updateBricklinkInventoryByUuidAndBlItemNumber(BricklinkInventory bricklinkInventory) {
        return new SQL() {{
            UPDATE("bricklink_inventory bi");
            Optional.ofNullable(bricklinkInventory.getBoxConditionCode())
                    .ifPresent(s -> SET("bi.box_condition_id = coalesce((select c.condition_id from `condition` c where c.condition_code = upper(#{boxConditionCode})),bi.box_condition_id)"));
            Optional.ofNullable(bricklinkInventory.getInstructionsConditionCode())
                    .ifPresent(s -> SET("bi.instructions_condition_id = coalesce((select c.condition_id from `condition` c where c.condition_code = upper(#{instructionsConditionCode})),bi.instructions_condition_id)"));
            Optional.ofNullable(bricklinkInventory.getBuiltOnce())
                    .ifPresent(bo -> {
                        SET("bi.built_once = #{builtOnce}");
                        if (Boolean.TRUE.equals(bo)) {
                            SET("bi.new_or_used = 'U'");
                        } else {
                            SET("bi.new_or_used = 'N'");
                        }
                    });
            Optional.ofNullable(bricklinkInventory.getSealed())
                    .ifPresent(s -> {
                        SET("bi.sealed = #{sealed}");
                        if (Boolean.TRUE.equals(s)) {
                            SET("bi.new_or_used = 'N'");
                            SET("bi.completeness = 'S'");
                        } else {
                            SET("bi.completeness = 'C'");
                        }
                    });
            SET("color_id = bi.color_id");
            WHERE("color_id = bi.color_id");
            WHERE("bi.uuid = #{uuid}");
            WHERE("bi.bl_item_number = #{blItemNo}");
        }}.toString();
    }
}
