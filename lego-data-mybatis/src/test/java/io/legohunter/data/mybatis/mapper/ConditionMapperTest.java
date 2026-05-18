package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Condition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ConditionMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByIdFindByCodeAndFindAll() {
        Condition condition = Condition.builder()
                .conditionId(1)
                .conditionCode("G")
                .conditionDescription("Good")
                .conditionText("Good text")
                .build();

        conditionMapper.insert(condition);
        condition.setConditionDescription("Great");
        conditionMapper.update(condition);

        assertThat(conditionMapper.findConditionById(1))
                .hasValueSatisfying(found -> assertThat(found.getConditionDescription()).isEqualTo("Great"));
        assertThat(conditionMapper.findByConditionCode("G"))
                .hasValueSatisfying(found -> assertThat(found.getConditionId()).isEqualTo(1));
        assertThat(conditionMapper.findAll()).extracting(Condition::getConditionCode).containsExactly("G");
    }
}
