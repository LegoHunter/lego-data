package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CostType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class CostTypeMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByCodeAndFindAll() {
        CostType costType = CostType.builder()
                .costTypeCode("SHIP")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping cost")
                .build();

        costTypeMapper.insert(costType);
        costType.setCostTypeName("Postage");
        costTypeMapper.update(costType);

        assertThat(costTypeMapper.findCostTypeByCode("SHIP"))
                .hasValueSatisfying(found -> assertThat(found.getCostTypeName()).isEqualTo("Postage"));
        assertThat(costTypeMapper.findAll()).extracting(CostType::getCostTypeCode).containsExactly("SHIP");
    }
}
