package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindsAndUpsert() {
        seedDefaultCondition();
        ItemInventory itemInventory = itemInventory("uuid-inventory");

        itemInventoryMapper.insert(itemInventory);
        assertThat(itemInventory.getItemInventoryId()).isNotNull();

        itemInventory.setDescription("Updated inventory");
        assertThat(itemInventoryMapper.update(itemInventory)).isEqualTo(1);

        assertThat(itemInventoryMapper.findByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getDescription()).isEqualTo("Updated inventory");
                    assertThat(found.getPurchasePrice()).isEqualByComparingTo("12.34");
                });
        assertThat(itemInventoryMapper.findByUuid("uuid-inventory"))
                .hasValueSatisfying(found -> assertThat(found.getItemInventoryId()).isEqualTo(itemInventory.getItemInventoryId()));
        assertThat(itemInventoryMapper.findAll()).hasSize(1);

        itemInventory.setDescription("Upserted inventory");
        itemInventoryMapper.upsert(itemInventory);

        assertThat(itemInventoryMapper.findByUuid("uuid-inventory"))
                .hasValueSatisfying(found -> assertThat(found.getDescription()).isEqualTo("Upserted inventory"));

        assertThat(itemInventoryMapper.delete(itemInventory.getItemInventoryId())).isOne();
        assertThat(itemInventoryMapper.findByUuid("uuid-inventory")).isEmpty();
    }
}
