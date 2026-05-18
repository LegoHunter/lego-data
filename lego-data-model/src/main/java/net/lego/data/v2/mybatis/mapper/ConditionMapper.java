package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Condition;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface ConditionMapper {
    @Insert("""
            INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) \
            VALUES (#{conditionId}, #{conditionCode}, #{conditionDescription}, #{conditionText})\
            """)
    void insert(Condition condition);

    @Update("""
            UPDATE `condition` SET condition_code = #{conditionCode}, condition_description = #{conditionDescription}, condition_text = #{conditionText} \
            WHERE condition_id = #{conditionId}\
            """)
    void update(Condition condition);

    @Select("""
            SELECT condition_id,\
                   condition_code,\
                   condition_description,\
                   condition_text \
            FROM `condition`\
            """)
    @ResultMap("conditionResultMap")
    List<Condition> findAll();

    @Select("""
            SELECT condition_id,\
                   condition_code,\
                   condition_description,\
                   condition_text \
            FROM `condition`\
            WHERE condition_id = #{conditionId}\
            """)
    @ResultMap("conditionResultMap")
    Optional<Condition> findConditionById(Integer conditionId);

    @Select("""
            SELECT condition_id,\
                   condition_code,\
                   condition_description,\
                   condition_text \
            FROM `condition`\
            WHERE condition_code = #{conditionCode}\
            """)
    @ResultMap("conditionResultMap")
    Optional<Condition> findByConditionCode(final String conditionCode);
}
