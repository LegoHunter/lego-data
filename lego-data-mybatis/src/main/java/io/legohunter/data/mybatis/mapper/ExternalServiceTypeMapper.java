package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalServiceType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ExternalServiceTypeMapper {
    @Select("""
            SELECT external_service_type_id,
                   external_service_type_name,
                   external_service_type_description
            FROM external_service_type
            """)
    @ResultMap("externalServiceTypeResultMap")
    Set<ExternalServiceType> findAll();

    @Select("""
            SELECT external_service_type_id,
                   external_service_type_name,
                   external_service_type_description
            FROM external_service_type
            WHERE external_service_type_id = #{externalServiceTypeId}
            """)
    @ResultMap("externalServiceTypeResultMap")
    Optional<ExternalServiceType> findByExternalServiceTypeId(Integer externalServiceTypeId);

    @Select("""
            SELECT external_service_type_id,
                   external_service_type_name,
                   external_service_type_description
            FROM external_service_type
            WHERE external_service_type_name = #{externalServiceTypeName}
            """)
    @ResultMap("externalServiceTypeResultMap")
    Optional<ExternalServiceType> findByExternalServiceTypeName(String externalServiceTypeName);

    @Insert("""
            INSERT INTO external_service_type (external_service_type_id,
                                               external_service_type_name,
                                               external_service_type_description)
            VALUES (#{externalServiceTypeId},
                    #{externalServiceTypeName},
                    #{externalServiceTypeDescription})
            """)
    int insert(ExternalServiceType externalServiceType);

    @Update("""
            UPDATE external_service_type
            SET external_service_type_name = #{externalServiceTypeName},
                external_service_type_description = #{externalServiceTypeDescription}
            WHERE external_service_type_id = #{externalServiceTypeId}
            """)
    int update(ExternalServiceType externalServiceType);

    @Delete("""
            DELETE FROM external_service_type
            WHERE external_service_type_id = #{externalServiceTypeId}
            """)
    int delete(Integer externalServiceTypeId);

    @Insert("""
            INSERT INTO external_service_type (external_service_type_id,
                                               external_service_type_name,
                                               external_service_type_description)
            VALUES (#{externalServiceTypeId},
                    #{externalServiceTypeName},
                    #{externalServiceTypeDescription})
            ON DUPLICATE KEY UPDATE
                external_service_type_name = VALUES(external_service_type_name),
                external_service_type_description = VALUES(external_service_type_description)
            """)
    int upsert(ExternalServiceType externalServiceType);

    default void insertExternalServiceType(ExternalServiceType externalServiceType) {
        insert(externalServiceType);
    }

    default void updateExternalServiceType(ExternalServiceType externalServiceType) {
        update(externalServiceType);
    }

    default Optional<ExternalServiceType> findExternalServiceTypeById(Integer externalServiceTypeId) {
        return findByExternalServiceTypeId(externalServiceTypeId);
    }

    default Optional<ExternalServiceType> findExternalServiceTypeByName(String externalServiceTypeName) {
        return findByExternalServiceTypeName(externalServiceTypeName);
    }
}
