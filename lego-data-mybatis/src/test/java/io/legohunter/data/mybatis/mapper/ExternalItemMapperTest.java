package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalItemMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindsAndUpsert() {
        seedExternalCatalog();
        ExternalItem externalItem = externalItem("3001-1", 123L, "Brick 2 x 4", 100, 2);

        externalItemMapper.insert(externalItem);
        assertThat(externalItem.getExternalItemId()).isNotNull();

        externalItem.setName("Brick 2 x 4 Updated");
        externalItemMapper.update(externalItem);

        assertThat(externalItemMapper.findByExternalItemId(externalItem.getExternalItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getName()).isEqualTo("Brick 2 x 4 Updated");
                    assertThat(found.getExternalService().getExternalServiceName()).isEqualTo("BRICKLINK");
                    assertThat(found.getCategory().getCategoryName()).isEqualTo("Sets");
                });
        assertThat(externalItemMapper.findByExternalServiceAndUniqueId(2, 123))
                .hasValueSatisfying(found -> assertThat(found.getExternalNumber()).isEqualTo("3001-1"));
        assertThat(externalItemMapper.findByExternalServiceAndNumber(2, "3001-1"))
                .hasValueSatisfying(found -> assertThat(found.getUniqueId()).isEqualTo(123L));
        assertThat(externalItemMapper.findAllByExternalService("BRICKLINK"))
                .extracting(ExternalItem::getExternalNumber)
                .containsExactly("3001-1");

        externalItemMapper.upsert(externalItem("3001-1", 456L, "Brick 2 x 4 Upserted", 100, 2));

        assertThat(externalItemMapper.findByExternalServiceAndNumber(2, "3001-1"))
                .hasValueSatisfying(found -> assertThat(found.getName()).isEqualTo("Brick 2 x 4 Upserted"));
    }
}
