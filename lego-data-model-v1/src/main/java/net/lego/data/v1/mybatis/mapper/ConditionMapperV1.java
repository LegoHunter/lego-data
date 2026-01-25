package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.Condition;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface ConditionMapperV1 {
    @Select("""
            SELECT condition_id,\
                   condition_code,\
                   condition_description, \
                   condition_text \
            FROM `condition` \
            """)
    @ResultMap("conditionResultMapV1")
    List<Condition> findAll();

    @Select("""
            SELECT condition_id,\
                   condition_code,\
                   condition_description, \
                   condition_text \
            FROM `condition`\
            WHERE condition_id = #{conditionId}\
            """)
    @ResultMap("conditionResultMapV1")
    Optional<Condition> findConditionById(Long conditionId);
}
