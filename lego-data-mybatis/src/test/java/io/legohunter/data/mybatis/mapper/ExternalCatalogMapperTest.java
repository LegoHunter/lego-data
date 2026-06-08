package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCatalogItemCategory;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalCatalogMapperTest extends MapperTestSupport {

    @Test
    void externalCategorySupportsBaselineCrudAndUniqueLookup() {
        seedExternalCatalog();
        ExternalCategory category = externalCategory(2, "101", "Parts", null);

        externalCategoryMapper.insert(category);
        category.setCategoryName("Updated Parts");
        externalCategoryMapper.update(category);

        assertThat(externalCategoryMapper.findByExternalCategoryId(category.getExternalCategoryId()))
                .hasValueSatisfying(found -> assertThat(found.getCategoryName()).isEqualTo("Updated Parts"));
        assertThat(externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "101"))
                .hasValueSatisfying(found -> assertThat(found.getExternalCategoryId()).isEqualTo(category.getExternalCategoryId()));
        assertThat(externalCategoryMapper.findAll()).extracting(ExternalCategory::getExternalCategoryKey)
                .contains("5", "101");

        category.setCategoryName("Upserted Parts");
        externalCategoryMapper.upsert(category);
        assertThat(externalCategoryMapper.findByExternalCategoryId(category.getExternalCategoryId()))
                .hasValueSatisfying(found -> assertThat(found.getCategoryName()).isEqualTo("Upserted Parts"));

        assertThat(externalCategoryMapper.delete(category.getExternalCategoryId())).isOne();
        assertThat(externalCategoryMapper.findByExternalCategoryId(category.getExternalCategoryId())).isEmpty();
    }

    @Test
    void externalCatalogItemSupportsBaselineCrudAndServiceKeyLookups() {
        seedExternalCatalog();
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "CATALOG"));
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "PRICE_GUIDE"));
        ExternalCatalogItem item = externalCatalogItem("3001-1", "123", "Brick 2 x 4", 2);

        externalCatalogItemMapper.insert(item);
        item.setItemName("Brick 2 x 4 Updated");
        externalCatalogItemMapper.update(item);

        assertThat(externalCatalogItemMapper.findByExternalCatalogItemId(item.getExternalCatalogItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getItemName()).isEqualTo("Brick 2 x 4 Updated");
                    assertThat(found.getExternalService().getServiceCode()).isEqualTo("BRICKLINK");
                    assertThat(found.getExternalService().getExternalServiceType().getExternalServiceTypeName()).isEqualTo("MARKETPLACE");
                    assertThat(found.getExternalService().getCapabilities())
                            .extracting("capabilityCode")
                            .containsExactlyInAnyOrder("CATALOG", "PRICE_GUIDE");
                });
        assertThat(externalCatalogItemMapper.findByExternalServiceIdAndExternalItemKey(2, "3001-1")).isPresent();
        assertThat(externalCatalogItemMapper.findByExternalServiceIdAndExternalUniqueKey(2, "123")).isPresent();
        assertThat(externalCatalogItemMapper.findAll()).extracting(ExternalCatalogItem::getExternalItemKey).contains("3001-1");

        item.setItemName("Upserted Brick");
        externalCatalogItemMapper.upsert(item);
        assertThat(externalCatalogItemMapper.findByExternalCatalogItemId(item.getExternalCatalogItemId()))
                .hasValueSatisfying(found -> assertThat(found.getItemName()).isEqualTo("Upserted Brick"));

        assertThat(externalCatalogItemMapper.delete(item.getExternalCatalogItemId())).isOne();
        assertThat(externalCatalogItemMapper.findByExternalCatalogItemId(item.getExternalCatalogItemId())).isEmpty();
    }

    @Test
    void externalCatalogItemCategorySupportsJoinCrud() {
        ExternalCatalogItem item = insertExternalCatalogItem("eicc-1");
        ExternalCategory category = externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "5").orElseThrow();
        ExternalCatalogItemCategory join = externalCatalogItemCategory(item.getExternalCatalogItemId(), category.getExternalCategoryId());

        externalCatalogItemCategoryMapper.insert(join);
        join.setPrimary(false);
        externalCatalogItemCategoryMapper.update(join);

        assertThat(externalCatalogItemCategoryMapper.findByExternalCatalogItemIdAndExternalCategoryId(item.getExternalCatalogItemId(), category.getExternalCategoryId()))
                .hasValueSatisfying(found -> assertThat(found.getPrimary()).isFalse());
        assertThat(externalCatalogItemCategoryMapper.findByExternalCatalogItemId(item.getExternalCatalogItemId())).hasSize(1);
        assertThat(externalCatalogItemCategoryMapper.findByExternalCategoryId(category.getExternalCategoryId())).hasSize(1);
        assertThat(externalCatalogItemCategoryMapper.findAll()).hasSize(1);

        join.setPrimary(true);
        externalCatalogItemCategoryMapper.upsert(join);
        assertThat(externalCatalogItemCategoryMapper.findByExternalCatalogItemIdAndExternalCategoryId(item.getExternalCatalogItemId(), category.getExternalCategoryId()))
                .hasValueSatisfying(found -> assertThat(found.getPrimary()).isTrue());

        assertThat(externalCatalogItemCategoryMapper.delete(item.getExternalCatalogItemId(), category.getExternalCategoryId())).isOne();
    }

    @Test
    void itemInventoryExternalCatalogItemSupportsJoinCrud() {
        ExternalCatalogItem item = insertExternalCatalogItem("iieci-1");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "CATALOG"));
        ItemInventory inventory = insertItemInventory("uuid-iieci");
        ItemInventoryExternalCatalogItem join = itemInventoryExternalCatalogItem(inventory.getItemInventoryId(), item.getExternalCatalogItemId());

        itemInventoryExternalCatalogItemMapper.insert(join);
        join.setPrimary(false);
        itemInventoryExternalCatalogItemMapper.update(join);

        assertThat(itemInventoryExternalCatalogItemMapper.findByItemInventoryIdAndExternalCatalogItemId(inventory.getItemInventoryId(), item.getExternalCatalogItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getPrimary()).isFalse();
                    assertThat(found.getExternalCatalogItem()).isNotNull();
                    assertThat(found.getExternalCatalogItem().getExternalItemKey()).isEqualTo("iieci-1");
                    assertThat(found.getExternalCatalogItem().getExternalService()).isNotNull();
                    assertThat(found.getExternalCatalogItem().getExternalService().getServiceCode()).isEqualTo("BRICKLINK");
                    assertThat(found.getExternalCatalogItem().getExternalService().getExternalServiceType().getExternalServiceTypeName()).isEqualTo("MARKETPLACE");
                    assertThat(found.getExternalCatalogItem().getExternalService().getCapabilities())
                            .extracting("capabilityCode")
                            .containsExactly("CATALOG");
                });
        assertThat(itemInventoryExternalCatalogItemMapper.findByItemInventoryId(inventory.getItemInventoryId())).hasSize(1);
        assertThat(itemInventoryExternalCatalogItemMapper.findByExternalCatalogItemId(item.getExternalCatalogItemId())).hasSize(1);
        assertThat(itemInventoryExternalCatalogItemMapper.findAll()).hasSize(1);

        join.setPrimary(true);
        itemInventoryExternalCatalogItemMapper.upsert(join);
        assertThat(itemInventoryExternalCatalogItemMapper.findByItemInventoryIdAndExternalCatalogItemId(inventory.getItemInventoryId(), item.getExternalCatalogItemId()))
                .hasValueSatisfying(found -> assertThat(found.getPrimary()).isTrue());

        assertThat(itemInventoryExternalCatalogItemMapper.delete(inventory.getItemInventoryId(), item.getExternalCatalogItemId())).isOne();
    }
}
