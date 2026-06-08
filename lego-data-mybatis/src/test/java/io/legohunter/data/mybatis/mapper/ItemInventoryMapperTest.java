package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindsAndUpsert() {
        seedDefaultCondition();
        ExternalCatalogItem externalCatalogItem = insertExternalCatalogItem("uuid-inventory-catalog-item");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "CATALOG"));
        ItemInventory itemInventory = itemInventory("uuid-inventory");

        itemInventoryMapper.insert(itemInventory);
        assertThat(itemInventory.getItemInventoryId()).isNotNull();
        itemInventoryExternalCatalogItemMapper.insert(itemInventoryExternalCatalogItem(
                itemInventory.getItemInventoryId(),
                externalCatalogItem.getExternalCatalogItemId()
        ));

        itemInventory.setDescription("Updated inventory");
        assertThat(itemInventoryMapper.update(itemInventory)).isEqualTo(1);

        assertThat(itemInventoryMapper.findByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getDescription()).isEqualTo("Updated inventory");
                    assertThat(found.getPurchasePrice()).isEqualByComparingTo("12.34");
                    assertThat(found.getExternalCatalogItems())
                            .hasSize(1)
                            .first()
                            .satisfies(this::assertHydratedCatalogLink);
                });
        assertThat(itemInventoryMapper.findByUuid("uuid-inventory"))
                .hasValueSatisfying(found -> assertThat(found.getItemInventoryId()).isEqualTo(itemInventory.getItemInventoryId()));
        assertThat(itemInventoryMapper.findAll()).hasSize(1);

        itemInventory.setDescription("Upserted inventory");
        itemInventoryMapper.upsert(itemInventory);

        assertThat(itemInventoryMapper.findByUuid("uuid-inventory"))
                .hasValueSatisfying(found -> assertThat(found.getDescription()).isEqualTo("Upserted inventory"));

        assertThat(itemInventoryExternalCatalogItemMapper.delete(
                itemInventory.getItemInventoryId(),
                externalCatalogItem.getExternalCatalogItemId()
        )).isOne();
        assertThat(itemInventoryMapper.delete(itemInventory.getItemInventoryId())).isOne();
        assertThat(itemInventoryMapper.findByUuid("uuid-inventory")).isEmpty();
    }

    private void assertHydratedCatalogLink(ItemInventoryExternalCatalogItem link) {
        assertThat(link.getExternalCatalogItem()).isNotNull();
        assertThat(link.getExternalCatalogItem().getExternalItemKey()).isEqualTo("uuid-inventory-catalog-item");
        assertThat(link.getExternalCatalogItem().getExternalService()).isNotNull();
        assertThat(link.getExternalCatalogItem().getExternalService().getServiceCode()).isEqualTo("BRICKLINK");
        assertThat(link.getExternalCatalogItem().getExternalService().getExternalServiceType().getExternalServiceTypeName()).isEqualTo("MARKETPLACE");
        assertThat(link.getExternalCatalogItem().getExternalService().getCapabilities())
                .extracting("capabilityCode")
                .containsExactly("CATALOG");
    }
}
