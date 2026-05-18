package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalService;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;

public interface ExternalServiceMapper {
    @Insert("""
            INSERT INTO external_service (external_service_id, external_service_name, external_service_url, external_service_type_id) \
            VALUES (#{externalServiceId}, #{externalServiceName}, #{externalServiceUrl}, #{externalServiceTypeId})\
            """)
    void insertExternalService(ExternalService externalService);

    @Update("""
            UPDATE external_service SET \
            external_service_name = #{externalServiceName}, \
            external_service_url  = #{externalServiceUrl}, \
            external_service_type_id  = #{externalServiceTypeId} \
            WHERE external_service_id = #{externalServiceId} \
            """)
    void updateExternalService(ExternalService externalService);

    @Select("""
            SELECT external_service_id, \
                   external_service_name, \
                   external_service_url, \
                   external_service_type_id \
            FROM external_service \
            """)
    @ResultMap("externalServiceResultMap")
    List<ExternalService> findAll();

    @Select("""
            SELECT external_service_id, \
                   external_service_name, \
                   external_service_url, \
                   external_service_type_id \
            FROM external_service \
            WHERE external_service_id = #{externalServiceId}\
            """)
    @ResultMap("externalServiceResultMap")
    Optional<ExternalService> findExternalServiceById(Integer externalServiceId);

    @Select("""
            SELECT external_service_id, \
                   external_service_name, \
                   external_service_url, \
                   external_service_type_id \
            FROM external_service \
            WHERE external_service_name = #{externalServiceName}\
            """)
    @ResultMap("externalServiceResultMap")
    Optional<ExternalService> findExternalServiceByName(String externalServiceName);
}