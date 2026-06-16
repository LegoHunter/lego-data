package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventoryMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindsAndUpsert() {
        seedDefaultCondition();
        ExternalCatalogItem externalCatalogItem = insertExternalCatalogItem("uuid-inventory-catalog-item");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "CATALOG"));
        ExternalCategory category = externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "5").orElseThrow();
        externalCatalogItemCategoryMapper.insert(externalCatalogItemCategory(
                externalCatalogItem.getExternalCatalogItemId(),
                category.getExternalCategoryId()
        ));
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
                    assertThat(found.getInventoryStateCode()).isEqualTo("AVAILABLE");
                    assertThat(found.getInventoryStateChangedAt()).isNotNull();
                    assertThat(found.getSaleIntentCode()).isEqualTo("UNDECIDED");
                    assertThat(found.getSaleIntentUpdatedAt()).isNotNull();
                    assertThat(found.getExternalCatalogItems())
                            .hasSize(1)
                            .first()
                            .satisfies(this::assertHydratedCatalogLink);
                });

        ZonedDateTime stateChangedAt = ZonedDateTime.parse("2026-06-16T10:00:00Z");
        assertThat(itemInventoryMapper.updateInventoryState(
                itemInventory.getItemInventoryId(),
                "RESERVED_FOR_ORDER",
                stateChangedAt
        )).isOne();
        assertThat(itemInventoryMapper.findByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getInventoryStateCode()).isEqualTo("RESERVED_FOR_ORDER");
                    assertThat(found.getInventoryStateChangedAt()).isEqualTo(stateChangedAt);
                });

        ZonedDateTime saleIntentUpdatedAt = ZonedDateTime.parse("2026-06-16T11:00:00Z");
        assertThat(itemInventoryMapper.updateSaleIntent(
                itemInventory.getItemInventoryId(),
                "KEEP",
                saleIntentUpdatedAt,
                "Personal collection"
        )).isOne();
        assertThat(itemInventoryMapper.findByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getSaleIntentCode()).isEqualTo("KEEP");
                    assertThat(found.getSaleIntentUpdatedAt()).isEqualTo(saleIntentUpdatedAt);
                    assertThat(found.getSaleIntentNote()).isEqualTo("Personal collection");
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
        assertThat(link.getExternalCatalogItem().getCategories())
                .extracting(ExternalCategory::getExternalCategoryKey)
                .containsExactly("5");
    }
}
