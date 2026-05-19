package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalServiceMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByIdFindByNameAndFindAll() {
        externalServiceTypeMapper.insertExternalServiceType(ExternalServiceType.builder()
                .externalServiceTypeId(2)
                .externalServiceTypeName("MARKETPLACE")
                .externalServiceTypeDescription("Marketplace")
                .build());
        ExternalService service = externalService(2, "BRICKLINK");

        externalServiceMapper.insertExternalService(service);
        service.setExternalServiceUrl("https://bricklink.test");
        externalServiceMapper.updateExternalService(service);

        assertThat(externalServiceMapper.findExternalServiceById(2))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceUrl()).isEqualTo("https://bricklink.test"));
        assertThat(externalServiceMapper.findExternalServiceByName("BRICKLINK"))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceId()).isEqualTo(2));
        assertThat(externalServiceMapper.findAll()).extracting(ExternalService::getExternalServiceName).containsExactly("BRICKLINK");
    }
}
