package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkItemInventory;
import io.legohunter.data.dto.ExternalItem;
import io.legohunter.data.dto.ItemInventory;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class BricklinkItemInventoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateAndFinds() {
        ItemInventory itemInventory = insertItemInventory("uuid-bricklink");
        ExternalItem externalItem = insertExternalItem("bii-1");
        insertExternalItemInventory(externalItem.getExternalItemId(), itemInventory.getItemInventoryId());
        BricklinkItemInventory bricklinkItemInventory = bricklinkItemInventory(externalItem.getExternalItemId(), itemInventory.getItemInventoryId());

        bricklinkItemInventoryMapper.insert(bricklinkItemInventory);

        BricklinkItemInventory updatedBricklink = BricklinkItemInventory.builder()
                .bricklinkItemInventoryId(bricklinkItemInventory.getBricklinkItemInventoryId())
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .inventoryId(99L)
                .itemType("SET")
                .colorId(5)
                .colorName("Red")
                .quantity(3)
                .unitPrice(10.50)
                .description("Updated")
                .isRetain(true)
                .isStockRoom(false)
                .dateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .build();
        bricklinkItemInventoryMapper.update(updatedBricklink);

        assertThat(bricklinkItemInventoryMapper.findByBricklinkItemInventoryId(bricklinkItemInventory.getBricklinkItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getQuantity()).isEqualTo(3));
        assertThat(bricklinkItemInventoryMapper.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getInventoryId()).isEqualTo(99L));
        assertThat(bricklinkItemInventoryMapper.findByUuid("uuid-bricklink"))
                .hasValueSatisfying(found -> assertThat(found.getColorName()).isEqualTo("Red"));
    }
}
