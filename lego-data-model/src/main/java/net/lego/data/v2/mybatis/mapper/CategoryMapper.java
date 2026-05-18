package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface CategoryMapper {
    @Insert("""
            INSERT INTO category (external_service_id, external_category_id, category_name, parent_id) \
            VALUES (#{externalServiceId}, #{externalCategoryId}, #{categoryName}, #{parentId})\
            """)
    void insert(Category category);

    @Update("""
            UPDATE category SET category_name = #{categoryName}, parent_id = #{parentId} \
            WHERE external_category_id = #{externalCategoryId}\
            """)
    void update(Category category);

    @Select("""
            SELECT external_service_id,\
                   external_category_id,\
                   category_name, \
                   parent_id \
            FROM category\
            """)
    @ResultMap("categoryResultMap")
    List<Category> findAll();

    @Select("""
            SELECT external_service_id,
                   external_category_id,
                   category_name,
                   parent_id
            FROM category
            WHERE external_service_id = #{externalServiceId}
            AND external_category_id = #{externalCategoryId}
            """)
    @ResultMap("categoryResultMap")
    Optional<Category> findCategoryByExternalServiceAndCategoryId(Integer externalServiceId, Integer externalCategoryId);

    @Insert("""
                INSERT INTO category (external_service_id, 
                                      external_category_id, 
                                      category_name, 
                                      parent_id)
                VALUES (
                    #{externalServiceId},
                    #{externalCategoryId},
                    #{categoryName},
                    #{parentId}
                )
                ON DUPLICATE KEY UPDATE
                    category_name = VALUES(category_name),
                    parent_id = VALUES(parent_id)
            """)
    void upsert(Category category);
}