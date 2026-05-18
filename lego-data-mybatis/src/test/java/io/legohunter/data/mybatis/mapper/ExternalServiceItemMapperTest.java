package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalItem;
import io.legohunter.data.dto.ExternalServiceItem;
import io.legohunter.data.dto.ItemInventory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalServiceItemMapperTest extends MapperTestSupport {

    @Test
    void insertFindsAndDelete() {
        ExternalItem externalItem = insertExternalItem("esi-1");
        ItemInventory itemInventory = insertItemInventory("uuid-external-service-item");
        ExternalServiceItem serviceItem = ExternalServiceItem.builder()
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .build();

        externalServiceItemMapper.insert(serviceItem);

        assertThat(externalServiceItemMapper.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .hasValue(serviceItem);
        assertThat(externalServiceItemMapper.findByExternalItemId(externalItem.getExternalItemId())).hasValue(serviceItem);
        assertThat(externalServiceItemMapper.findByItemInventoryId(itemInventory.getItemInventoryId())).hasValue(serviceItem);

        externalServiceItemMapper.delete(serviceItem);

        assertThat(externalServiceItemMapper.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .isEmpty();
    }
}
