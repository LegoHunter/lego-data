package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalServiceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalServiceTypeMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByIdFindByNameAndFindAll() {
        ExternalServiceType serviceType = ExternalServiceType.builder()
                .externalServiceTypeId(1)
                .externalServiceTypeName("Marketplace")
                .externalServiceTypeDescription("Marketplace APIs")
                .build();

        externalServiceTypeMapper.insertExternalServiceType(serviceType);
        serviceType.setExternalServiceTypeDescription("Marketplace integrations");
        externalServiceTypeMapper.updateExternalServiceType(serviceType);

        assertThat(externalServiceTypeMapper.findExternalServiceTypeById(1))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceTypeName()).isEqualTo("Marketplace"));
        assertThat(externalServiceTypeMapper.findExternalServiceTypeByName("Marketplace"))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceTypeDescription()).isEqualTo("Marketplace integrations"));
        assertThat(externalServiceTypeMapper.findAll()).extracting(ExternalServiceType::getExternalServiceTypeId).containsExactly(1);
    }
}
