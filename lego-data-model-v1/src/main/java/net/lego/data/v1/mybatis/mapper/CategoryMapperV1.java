package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.Category;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface CategoryMapperV1 {
    @Select("""
            SELECT category_id,\
                   category_type,\
                   category_name \
            FROM category\
            """)
    @ResultMap("categoryResultMapV1")
    List<Category> findAll();

    @Select("""
            SELECT category_id,\
                   category_type,\
                   category_name \
            FROM category \
            WHERE category_id = #{categoryId}\
            """)
    @ResultMap("categoryResultMapV1")
    Optional<Category> findCategoryById(Long categoryId);
}
