package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.InventoryIndex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class InventoryIndexMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateAndFinds() {
        InventoryIndex inventoryIndex = InventoryIndex.builder()
                .boxId(1)
                .boxIndex(2)
                .itemNumber("3001-1")
                .boxName("Main")
                .boxNumber("A")
                .sealed("N")
                .quantity(1)
                .description("Inventory index")
                .active(true)
                .movedToBoxId(null)
                .build();

        inventoryIndexMapper.insert(inventoryIndex);
        inventoryIndex.setQuantity(2);
        inventoryIndexMapper.update(inventoryIndex);

        assertThat(inventoryIndexMapper.findByItemNumber("3001-1"))
                .hasValueSatisfying(found -> assertThat(found.getQuantity()).isEqualTo(2));
        assertThat(inventoryIndexMapper.findByBoxIdAndBoxIndex(1, 2)).hasValue(inventoryIndex);
        assertThat(inventoryIndexMapper.findByBoxIdAndBoxIndexAndItemNumber("3001-1", 1, 2)).hasValue(inventoryIndex);
        assertThat(inventoryIndexMapper.getAllForBox(1)).hasSize(1);
        assertThat(inventoryIndexMapper.getAllWithNoItem()).hasSize(1);
        assertThat(inventoryIndexMapper.getAll()).hasSize(1);
    }
}
