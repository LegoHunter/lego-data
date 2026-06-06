package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalServiceCapability;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.Set;

public interface ExternalServiceCapabilityMapper {
    String ALL_COLUMNS = """
            external_service_id,
            capability_code
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service_capability")
    @ResultMap("externalServiceCapabilityResultMap")
    Set<ExternalServiceCapability> findAll();

    @Select("""
            SELECT external_service_id,
                   capability_code
            FROM external_service_capability
            WHERE external_service_id = #{externalServiceId}
              AND capability_code = #{capabilityCode}
            """)
    @ResultMap("externalServiceCapabilityResultMap")
    Optional<ExternalServiceCapability> findByExternalServiceIdAndCapabilityCode(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("capabilityCode") String capabilityCode
    );

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service_capability WHERE external_service_id = #{externalServiceId}")
    @ResultMap("externalServiceCapabilityResultMap")
    Set<ExternalServiceCapability> findByExternalServiceId(Integer externalServiceId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_service_capability WHERE capability_code = #{capabilityCode}")
    @ResultMap("externalServiceCapabilityResultMap")
    Set<ExternalServiceCapability> findByCapabilityCode(String capabilityCode);

    @Insert("""
            INSERT INTO external_service_capability (external_service_id,
                                                     capability_code)
            VALUES (#{externalServiceId},
                    #{capabilityCode})
            """)
    int insert(ExternalServiceCapability externalServiceCapability);

    @Update("""
            UPDATE external_service_capability
            SET capability_code = #{capabilityCode}
            WHERE external_service_id = #{externalServiceId}
              AND capability_code = #{capabilityCode}
            """)
    int update(ExternalServiceCapability externalServiceCapability);

    @Delete("""
            DELETE FROM external_service_capability
            WHERE external_service_id = #{externalServiceId}
              AND capability_code = #{capabilityCode}
            """)
    int delete(@Param("externalServiceId") Integer externalServiceId, @Param("capabilityCode") String capabilityCode);

    @Insert("""
            INSERT INTO external_service_capability (external_service_id,
                                                     capability_code)
            VALUES (#{externalServiceId},
                    #{capabilityCode})
            ON DUPLICATE KEY UPDATE
                capability_code = VALUES(capability_code)
            """)
    int upsert(ExternalServiceCapability externalServiceCapability);
}
