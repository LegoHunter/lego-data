package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventoryState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventoryStateMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByCodeFindAllUpsertAndDelete() {
        ItemInventoryState state = ItemInventoryState.builder()
                .inventoryStateCode("AVAILABLE")
                .inventoryStateName("Available")
                .inventoryStateDescription("Available for inventory workflows")
                .active(true)
                .sortOrder(10)
                .build();

        itemInventoryStateMapper.insert(state);
        state.setInventoryStateDescription("Available for marketplace listing");
        itemInventoryStateMapper.update(state);

        assertThat(itemInventoryStateMapper.findByInventoryStateCode("AVAILABLE"))
                .hasValueSatisfying(found -> assertThat(found.getInventoryStateDescription()).isEqualTo("Available for marketplace listing"));
        assertThat(itemInventoryStateMapper.findAll()).extracting(ItemInventoryState::getInventoryStateCode).containsExactly("AVAILABLE");

        state.setInventoryStateName("Ready");
        itemInventoryStateMapper.upsert(state);
        assertThat(itemInventoryStateMapper.findByInventoryStateCode("AVAILABLE"))
                .hasValueSatisfying(found -> assertThat(found.getInventoryStateName()).isEqualTo("Ready"));
        assertThat(itemInventoryStateMapper.delete("AVAILABLE")).isOne();
    }
}
