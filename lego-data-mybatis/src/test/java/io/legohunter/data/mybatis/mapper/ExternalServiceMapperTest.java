package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.dto.ExternalServiceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalServiceMapperTest extends MapperTestSupport {

    @Test
    void externalServiceSupportsBaselineCrudAndCodeLookup() {
        externalServiceTypeMapper.insertExternalServiceType(ExternalServiceType.builder()
                .externalServiceTypeId(2)
                .externalServiceTypeName("MARKETPLACE")
                .externalServiceTypeDescription("Marketplace")
                .build());
        ExternalService service = externalService(2, "BRICKLINK", "BrickLink", 2);

        externalServiceMapper.insertExternalService(service);
        service.setServiceUrl("https://bricklink.test");
        externalServiceMapper.updateExternalService(service);

        assertThat(externalServiceMapper.findExternalServiceById(2))
                .hasValueSatisfying(found -> assertThat(found.getServiceUrl()).isEqualTo("https://bricklink.test"));
        assertThat(externalServiceMapper.findExternalServiceByName("BRICKLINK"))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceId()).isEqualTo(2));
        assertThat(externalServiceMapper.findAll()).extracting(ExternalService::getServiceCode).containsExactly("BRICKLINK");

        service.setDisplayName("BrickLink Updated");
        externalServiceMapper.upsert(service);
        assertThat(externalServiceMapper.findByServiceCode("BRICKLINK"))
                .hasValueSatisfying(found -> assertThat(found.getDisplayName()).isEqualTo("BrickLink Updated"));

        assertThat(externalServiceMapper.delete(2)).isOne();
    }

    @Test
    void externalServiceCapabilitySupportsBaselineCrudAndCompositeKeyLookup() {
        seedExternalCatalog();
        ExternalServiceCapability capability = externalServiceCapability(2, "PRICE_GUIDE");

        externalServiceCapabilityMapper.insert(capability);

        assertThat(externalServiceCapabilityMapper.findByExternalServiceIdAndCapabilityCode(2, "PRICE_GUIDE")).isPresent();
        assertThat(externalServiceCapabilityMapper.findByExternalServiceId(2)).extracting(ExternalServiceCapability::getCapabilityCode).contains("PRICE_GUIDE");
        assertThat(externalServiceCapabilityMapper.findByCapabilityCode("PRICE_GUIDE")).extracting(ExternalServiceCapability::getExternalServiceId).contains(2);
        assertThat(externalServiceCapabilityMapper.findAll()).hasSize(1);

        externalServiceCapabilityMapper.upsert(capability);
        assertThat(externalServiceCapabilityMapper.findByExternalServiceIdAndCapabilityCode(2, "PRICE_GUIDE")).isPresent();
        assertThat(externalServiceCapabilityMapper.delete(2, "PRICE_GUIDE")).isOne();
    }
}
