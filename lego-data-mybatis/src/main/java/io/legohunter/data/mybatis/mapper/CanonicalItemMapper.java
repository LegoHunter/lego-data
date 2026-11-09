package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CanonicalItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

/** Canonical items in the shared pricing schema (HOMELAB-31). */
public interface CanonicalItemMapper {
    String ALL_COLUMNS = """
            canonical_item_id,
            item_type_code,
            item_number,
            item_name,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.canonical_item WHERE canonical_item_id = #{canonicalItemId}")
    @ResultMap("canonicalItemResultMap")
    Optional<CanonicalItem> findByCanonicalItemId(Integer canonicalItemId);

    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.canonical_item
            WHERE item_type_code = #{itemTypeCode}
              AND item_number = #{itemNumber}
            """)
    @ResultMap("canonicalItemResultMap")
    Optional<CanonicalItem> findByItemTypeCodeAndItemNumber(
            @Param("itemTypeCode") String itemTypeCode,
            @Param("itemNumber") String itemNumber,
            @Param("columns") String columns
    );

    default Optional<CanonicalItem> findByItemTypeCodeAndItemNumber(String itemTypeCode, String itemNumber) {
        return findByItemTypeCodeAndItemNumber(itemTypeCode, itemNumber, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.canonical_item (item_type_code, item_number, item_name)
            VALUES (#{itemTypeCode}, #{itemNumber}, #{itemName})
            """)
    @Options(useGeneratedKeys = true, keyColumn = "canonical_item_id", keyProperty = "canonicalItemId")
    void insert(CanonicalItem canonicalItem);

    @Update("""
            UPDATE ${marketplacePricingSchema}.canonical_item
            SET item_type_code = #{itemTypeCode},
                item_number = #{itemNumber},
                item_name = #{itemName}
            WHERE canonical_item_id = #{canonicalItemId}
            """)
    int update(CanonicalItem canonicalItem);
}
