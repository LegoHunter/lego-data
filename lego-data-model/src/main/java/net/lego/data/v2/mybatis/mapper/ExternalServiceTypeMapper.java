package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.ExternalServiceType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface ExternalServiceTypeMapper {
    @Insert("""
            INSERT INTO external_service_type (external_service_type_id, external_service_type_name, external_service_type_description) \
            VALUES (#{externalServiceTypeId}, #{externalServiceTypeName}, #{externalServiceTypeDescription})\
            """)
    void insertExternalServiceType(ExternalServiceType externalServiceType);

    @Update("""
            UPDATE external_service_type SET \
            external_service_type_name = #{externalServiceTypeName}, \
            external_service_type_description  = #{externalServiceTypeDescription} \
            WHERE external_service_type_id = #{externalServiceTypeId} \
            """)
    void updateExternalServiceType(ExternalServiceType externalServiceType);

    @Select("""
            SELECT external_service_id, \
                   external_service_type_name, \
                   external_service_type_description \
            FROM external_service_type \
            """)
    @ResultMap("externalServiceTypeResultMap")
    List<ExternalServiceType> findAll();

    @Select("""
            SELECT external_service_type_id, \
                   external_service_type_name, \
                   external_service_type_description \
            FROM external_service_type \
            WHERE external_service_type_id = #{externalServiceTypeId}\
            """)
    @ResultMap("externalServiceTypeResultMap")
    Optional<ExternalServiceType> findExternalServiceTypeById(Integer externalServiceTypeId);

    @Select("""
            SELECT external_service_type_id, \
                   external_service_type_name, \
                   external_service_type_description \
            FROM external_service_type \
            WHERE external_service_type_name = #{externalServiceTypeName}\
            """)
    @ResultMap("externalServiceTypeResultMap")
    Optional<ExternalServiceType> findExternalServiceTypeByName(String externalServiceTypeName);
}
