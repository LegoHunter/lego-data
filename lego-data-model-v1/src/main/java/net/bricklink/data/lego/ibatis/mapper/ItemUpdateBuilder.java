package net.bricklink.data.lego.ibatis.mapper;

import net.lego.data.v1.dto.Item;
import org.apache.ibatis.jdbc.SQL;

import java.util.Optional;

public class ItemUpdateBuilder {
    public String updateItem(Item item) {
        return new SQL() {{
            UPDATE("item i");
            Optional.ofNullable(item.getItemName())
                    .ifPresent(s -> SET("i.item_name = coalesce(i.item_name, #{itemName})"));
            Optional.ofNullable(item.getIssueYear())
                    .ifPresent(s -> SET("i.issue_year = coalesce(i.issue_year, #{issueYear})"));
            WHERE("i.item_id = #{itemId}");
        }}.toString();
    }
}
