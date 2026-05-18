package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.CostType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface CostTypeMapper {
    @Insert("""
            INSERT INTO cost_type (cost_type_code, cost_type_name, cost_type_description) \
            VALUES (#{costTypeCode}, #{costTypeName}, #{costTypeDescription})\
            """)
    void insert(CostType cost_type);

    @Update("""
            UPDATE cost_type SET \
            cost_type_name = #{costTypeName}, \
            cost_type_description = #{costTypeDescription} \
            WHERE cost_type_code = #{costTypeCode} \
            """)
    void update(CostType cost_type);

    @Select("""
            SELECT cost_type_code, \
                   cost_type_name, \
                   cost_type_description \
            FROM cost_type \
            """)
    @ResultMap("costTypeResultMap")
    List<CostType> findAll();

    @Select("""
            SELECT cost_type_code, \
                   cost_type_name, \
                   cost_type_description \
            FROM cost_type \
            WHERE cost_type_code = #{costTypeCode}\
            """)
    @ResultMap("costTypeResultMap")
    Optional<CostType> findCostTypeByCode(String costTypeCode);
}
