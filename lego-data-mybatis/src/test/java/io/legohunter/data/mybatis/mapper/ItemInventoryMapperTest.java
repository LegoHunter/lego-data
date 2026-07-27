package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.ItemInventorySearchCriteria;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.Transactions;
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

    @Test
    void searchFiltersByInventoryCatalogConditionAndTransactionFields() {
        seedDefaultCondition();
        insertTransactionType();
        ExternalCatalogItem externalCatalogItem = insertExternalCatalogItem("6390-1");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "CATALOG"));
        ExternalCatalogItem otherCatalogItem = insertExternalCatalogItem("6542-1");
        ItemInventory matching = itemInventory("uuid-search-match");
        matching.setDescription("Main Street complete set");
        matching.setBoxNumber(7);
        matching.setNewOrUsed("U");
        matching.setCompleteness("C");
        matching.setSaleIntentCode("KEEP");
        matching.setInventoryStateCode("AVAILABLE");
        itemInventoryMapper.insert(matching);
        itemInventoryExternalCatalogItemMapper.insert(itemInventoryExternalCatalogItem(
                matching.getItemInventoryId(),
                externalCatalogItem.getExternalCatalogItemId()
        ));
        Transactions transaction = insertTransaction();
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(matching.getItemInventoryId())
                .notes("Search item")
                .build();
        transactionItemMapper.insert(transactionItem);

        ItemInventory other = itemInventory("uuid-search-other");
        other.setDescription("Launch seaport");
        other.setBoxNumber(8);
        itemInventoryMapper.insert(other);
        itemInventoryExternalCatalogItemMapper.insert(itemInventoryExternalCatalogItem(
                other.getItemInventoryId(),
                otherCatalogItem.getExternalCatalogItemId()
        ));

        ItemInventorySearchCriteria criteria = ItemInventorySearchCriteria.builder()
                .itemNumber("6390-1")
                .description("street")
                .boxNumber(7)
                .inventoryStateCode("AVAILABLE")
                .saleIntentCode("KEEP")
                .active(true)
                .newOrUsed("U")
                .completeness("C")
                .itemConditionCode("G")
                .boxConditionCode("G")
                .instructionsConditionCode("G")
                .transactionDateFrom(java.time.LocalDate.parse("2026-01-01"))
                .transactionDateTo(java.time.LocalDate.parse("2026-01-01"))
                .limit(10)
                .offset(0)
                .build();

        assertThat(itemInventoryMapper.search(criteria))
                .extracting(ItemInventory::getUuid)
                .containsExactly("uuid-search-match");
        assertThat(itemInventoryMapper.countSearch(criteria)).isOne();
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
