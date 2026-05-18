package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalItem;
import io.legohunter.data.dto.ExternalItemInventory;
import io.legohunter.data.dto.ItemInventory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalItemInventoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateAndFinds() {
        ExternalItem externalItem = insertExternalItem("eii-1");
        ItemInventory itemInventory = insertItemInventory("uuid-external-item-inventory");
        ExternalItemInventory externalItemInventory = ExternalItemInventory.builder()
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .fixedPrice(true)
                .orderId(12)
                .extendedDescription("Extended")
                .extraDescription("Extra")
                .internalComments("Internal")
                .updateTimestamp(Instant.parse("2026-01-01T00:00:00Z"))
                .lastSynchronizedTimestamp(Instant.parse("2026-01-02T00:00:00Z"))
                .build();

        externalItemInventoryMapper.insert(externalItemInventory);
        externalItemInventory.setExtraDescription("Extra updated");
        externalItemInventoryMapper.update(externalItemInventory);

        assertThat(externalItemInventoryMapper.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getExtraDescription()).isEqualTo("Extra updated"));
        assertThat(externalItemInventoryMapper.findByExternalItemId(externalItem.getExternalItemId())).hasSize(1);
        assertThat(externalItemInventoryMapper.findByItemInventoryId(itemInventory.getItemInventoryId())).hasSize(1);
    }
}
