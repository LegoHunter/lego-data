package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ExternalServiceMapper {
    String ALL_COLUMNS = """
            external_service_id,
            service_code,
            display_name,
            service_url,
            external_service_type_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service")
    @ResultMap("externalServiceResultMap")
    Set<ExternalService> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service WHERE external_service_id = #{externalServiceId}")
    @ResultMap("externalServiceResultMap")
    Optional<ExternalService> findByExternalServiceId(Integer externalServiceId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service WHERE service_code = #{serviceCode}")
    @ResultMap("externalServiceResultMap")
    Optional<ExternalService> findByServiceCode(String serviceCode);

    @Insert("""
            INSERT INTO external_service (external_service_id,
                                          service_code,
                                          display_name,
                                          service_url,
                                          external_service_type_id)
            VALUES (#{externalServiceId},
                    #{serviceCode},
                    #{displayName},
                    #{serviceUrl},
                    #{externalServiceTypeId})
            """)
    int insert(ExternalService externalService);

    @Update("""
            UPDATE external_service
            SET service_code = #{serviceCode},
                display_name = #{displayName},
                service_url = #{serviceUrl},
                external_service_type_id = #{externalServiceTypeId}
            WHERE external_service_id = #{externalServiceId}
            """)
    int update(ExternalService externalService);

    @Delete("DELETE FROM external_service WHERE external_service_id = #{externalServiceId}")
    int delete(Integer externalServiceId);

    @Insert("""
            INSERT INTO external_service (external_service_id,
                                          service_code,
                                          display_name,
                                          service_url,
                                          external_service_type_id)
            VALUES (#{externalServiceId},
                    #{serviceCode},
                    #{displayName},
                    #{serviceUrl},
                    #{externalServiceTypeId})
            ON DUPLICATE KEY UPDATE
                service_code = VALUES(service_code),
                display_name = VALUES(display_name),
                service_url = VALUES(service_url),
                external_service_type_id = VALUES(external_service_type_id)
            """)
    int upsert(ExternalService externalService);

    default void insertExternalService(ExternalService externalService) {
        insert(externalService);
    }

    default void updateExternalService(ExternalService externalService) {
        update(externalService);
    }

    default Optional<ExternalService> findExternalServiceById(Integer externalServiceId) {
        return findByExternalServiceId(externalServiceId);
    }

    default Optional<ExternalService> findExternalServiceByName(String serviceCode) {
        return findByServiceCode(serviceCode);
    }
}
