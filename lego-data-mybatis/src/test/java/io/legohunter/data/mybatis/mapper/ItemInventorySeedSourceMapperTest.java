package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventorySeedSource;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static io.legohunter.data.dto.ItemInventorySeedSource.SOURCE_SYSTEM_LEGO_PROD_LEGACY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MapperIntegrationTest
class ItemInventorySeedSourceMapperTest extends MapperTestSupport {
    private static final String RUN_1 = "11111111-1111-1111-1111-111111111111";
    private static final String RUN_2 = "22222222-2222-2222-2222-222222222222";

    @Test
    void insertFindByItemSourceKeyAndRunThenDelete() {
        ItemInventory first = insertItemInventory("seed-source-first");
        ItemInventory second = insertItemInventory("seed-source-second");
        ItemInventory third = insertItemInventory("seed-source-third");

        itemInventorySeedSourceMapper.insert(source(first, "28", RUN_1));
        itemInventorySeedSourceMapper.insert(source(second, "30", RUN_1));
        itemInventorySeedSourceMapper.insert(source(third, "43", RUN_2));

        assertThat(itemInventorySeedSourceMapper.findByItemInventoryId(first.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getSourceSystem()).isEqualTo(SOURCE_SYSTEM_LEGO_PROD_LEGACY);
                    assertThat(found.getSourceKey()).isEqualTo("28");
                    assertThat(found.getSeedRunId()).isEqualTo(RUN_1);
                    assertThat(found.getSeededAt()).isNotNull();
                });
        assertThat(itemInventorySeedSourceMapper.findBySourceSystemAndSourceKey(SOURCE_SYSTEM_LEGO_PROD_LEGACY, "30"))
                .hasValueSatisfying(found -> assertThat(found.getItemInventoryId()).isEqualTo(second.getItemInventoryId()));
        assertThat(itemInventorySeedSourceMapper.findBySourceSystemAndSourceKey(SOURCE_SYSTEM_LEGO_PROD_LEGACY, "999")).isEmpty();
        assertThat(itemInventorySeedSourceMapper.findBySourceSystemAndSourceKey("OTHER_SYSTEM", "30")).isEmpty();
        assertThat(itemInventorySeedSourceMapper.findBySeedRunId(RUN_1))
                .extracting(ItemInventorySeedSource::getSourceKey)
                .containsExactly("28", "30");
        assertThat(itemInventorySeedSourceMapper.findAll())
                .extracting(ItemInventorySeedSource::getItemInventoryId)
                .containsExactly(first.getItemInventoryId(), second.getItemInventoryId(), third.getItemInventoryId());

        assertThat(itemInventorySeedSourceMapper.deleteByItemInventoryId(second.getItemInventoryId())).isOne();
        assertThat(itemInventorySeedSourceMapper.findByItemInventoryId(second.getItemInventoryId())).isEmpty();
        assertThat(itemInventorySeedSourceMapper.deleteByItemInventoryId(second.getItemInventoryId())).isZero();
    }

    @Test
    void theSameSourceItemCannotBeSeededTwice() {
        ItemInventory first = insertItemInventory("seed-source-dup-first");
        ItemInventory second = insertItemInventory("seed-source-dup-second");
        itemInventorySeedSourceMapper.insert(source(first, "28", RUN_1));

        assertThatThrownBy(() -> itemInventorySeedSourceMapper.insert(source(second, "28", RUN_2)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(itemInventorySeedSourceMapper.findByItemInventoryId(second.getItemInventoryId())).isEmpty();
    }

    @Test
    void anItemHasAtMostOneSourceAndMustExist() {
        ItemInventory item = insertItemInventory("seed-source-one-per-item");
        itemInventorySeedSourceMapper.insert(source(item, "28", RUN_1));

        assertThatThrownBy(() -> itemInventorySeedSourceMapper.insert(source(item, "30", RUN_1)))
                .isInstanceOf(DataIntegrityViolationException.class);

        ItemInventory missing = new ItemInventory();
        missing.setItemInventoryId(987654);
        assertThatThrownBy(() -> itemInventorySeedSourceMapper.insert(source(missing, "43", RUN_1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static ItemInventorySeedSource source(ItemInventory item, String sourceKey, String seedRunId) {
        return ItemInventorySeedSource.builder()
                .itemInventoryId(item.getItemInventoryId())
                .sourceSystem(SOURCE_SYSTEM_LEGO_PROD_LEGACY)
                .sourceKey(sourceKey)
                .seedRunId(seedRunId)
                .build();
    }
}
