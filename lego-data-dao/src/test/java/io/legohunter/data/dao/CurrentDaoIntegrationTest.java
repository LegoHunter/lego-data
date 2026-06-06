package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.EbayListingItemSpecific;
import io.legohunter.data.dto.EbayMarketplaceListing;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCatalogItemCategory;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.MarketplaceListing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/current_lookup_data.sql"
})
class CurrentDaoIntegrationTest {

    @Autowired ExternalServiceTypeDao externalServiceTypeDao;
    @Autowired ExternalServiceDao externalServiceDao;
    @Autowired ExternalServiceCapabilityDao externalServiceCapabilityDao;
    @Autowired ExternalCategoryDao externalCategoryDao;
    @Autowired ExternalCatalogItemDao externalCatalogItemDao;
    @Autowired ExternalCatalogItemCategoryDao externalCatalogItemCategoryDao;
    @Autowired ItemInventoryDao itemInventoryDao;
    @Autowired ItemInventoryExternalCatalogItemDao itemInventoryExternalCatalogItemDao;
    @Autowired MarketplaceListingDao marketplaceListingDao;
    @Autowired BricklinkMarketplaceListingDao bricklinkMarketplaceListingDao;
    @Autowired EbayMarketplaceListingDao ebayMarketplaceListingDao;
    @Autowired EbayListingItemSpecificDao ebayListingItemSpecificDao;
    @Autowired ConditionDao conditionDao;

    @Test
    void serviceDaosSupportBaselineCrudAndLookupData() {
        assertThat(externalServiceTypeDao.findAll()).extracting(ExternalServiceType::getExternalServiceTypeName)
                .contains("MARKETPLACE", "AUCTION", "IMAGE_HOSTING");
        assertThat(externalServiceDao.findAll()).extracting(ExternalService::getServiceCode)
                .contains("BRICKLINK", "EBAY", "FLICKR");
        assertThat(externalServiceCapabilityDao.findAll()).extracting(ExternalServiceCapability::getCapabilityCode)
                .contains("CATALOG", "PRICE_GUIDE", "IMAGE_HOSTING");

        ExternalServiceType serviceType = ExternalServiceType.builder()
                .externalServiceTypeId(100)
                .externalServiceTypeName("UNIT_TEST_SERVICE")
                .externalServiceTypeDescription("Unit test service")
                .build();
        assertThat(externalServiceTypeDao.insert(serviceType)).isSameAs(serviceType);
        serviceType.setExternalServiceTypeDescription("Updated");
        assertThat(externalServiceTypeDao.update(serviceType).getExternalServiceTypeDescription()).isEqualTo("Updated");
        serviceType.setExternalServiceTypeDescription("Upserted");
        assertThat(externalServiceTypeDao.upsert(serviceType).getExternalServiceTypeDescription()).isEqualTo("Upserted");
        assertThat(externalServiceTypeDao.findByExternalServiceTypeId(100)).isPresent();
        assertThat(externalServiceTypeDao.findByExternalServiceTypeName("UNIT_TEST_SERVICE")).isPresent();
        assertThat(externalServiceTypeDao.findExternalServiceTypeById(100)).isPresent();
        assertThat(externalServiceTypeDao.findExternalServiceTypeByName("UNIT_TEST_SERVICE")).isPresent();

        ExternalService service = externalService(100, "UNIT_TEST", "Unit Test", 100);
        assertThat(externalServiceDao.insert(service)).isSameAs(service);
        service.setDisplayName("Unit Test Updated");
        assertThat(externalServiceDao.update(service).getDisplayName()).isEqualTo("Unit Test Updated");
        service.setDisplayName("Unit Test Upserted");
        assertThat(externalServiceDao.upsert(service).getDisplayName()).isEqualTo("Unit Test Upserted");
        assertThat(externalServiceDao.findByExternalServiceId(100)).isPresent();
        assertThat(externalServiceDao.findByServiceCode("UNIT_TEST")).isPresent();
        assertThat(externalServiceDao.findExternalServiceById(100)).isPresent();
        assertThat(externalServiceDao.findExternalServiceByName("UNIT_TEST")).isPresent();

        ExternalServiceCapability capability = ExternalServiceCapability.builder()
                .externalServiceId(100)
                .capabilityCode("UNIT_TEST_CAPABILITY")
                .build();
        assertThat(externalServiceCapabilityDao.insert(capability)).isSameAs(capability);
        assertThat(externalServiceCapabilityDao.update(capability)).isEqualTo(capability);
        assertThat(externalServiceCapabilityDao.upsert(capability)).isEqualTo(capability);
        assertThat(externalServiceCapabilityDao.findByExternalServiceIdAndCapabilityCode(100, "UNIT_TEST_CAPABILITY")).isPresent();
        assertThat(externalServiceCapabilityDao.findByExternalServiceId(100)).hasSize(1);
        assertThat(externalServiceCapabilityDao.findByCapabilityCode("UNIT_TEST_CAPABILITY")).hasSize(1);

        externalServiceCapabilityDao.delete(100, "UNIT_TEST_CAPABILITY");
        externalServiceDao.delete(100);
        externalServiceTypeDao.delete(100);
    }

    @Test
    void catalogAndInventoryDaosSupportBaselineCrud() {
        ExternalCategory category = ExternalCategory.builder()
                .externalServiceId(2)
                .externalCategoryKey("101")
                .categoryName("Parts")
                .build();
        category = externalCategoryDao.insert(category);
        category.setCategoryName("Updated Parts");
        assertThat(externalCategoryDao.update(category).getCategoryName()).isEqualTo("Updated Parts");
        category.setCategoryName("Upserted Parts");
        assertThat(externalCategoryDao.upsert(category).getCategoryName()).isEqualTo("Upserted Parts");
        ExternalCategory naturalKeyCategory = ExternalCategory.builder()
                .externalServiceId(2)
                .externalCategoryKey("101")
                .categoryName("Natural Key Upserted Parts")
                .build();
        assertThat(externalCategoryDao.upsert(naturalKeyCategory))
                .extracting(ExternalCategory::getExternalCategoryId, ExternalCategory::getCategoryName)
                .containsExactly(category.getExternalCategoryId(), "Natural Key Upserted Parts");
        assertThat(externalCategoryDao.findByExternalCategoryId(category.getExternalCategoryId())).isPresent();
        assertThat(externalCategoryDao.findByExternalServiceIdAndExternalCategoryKey(2, "101")).isPresent();
        assertThat(externalCategoryDao.findAll()).extracting(ExternalCategory::getExternalCategoryKey).contains("101");

        ExternalCatalogItem catalogItem = externalCatalogItem("dao-item-1", "dao-unique-1");
        catalogItem = externalCatalogItemDao.insert(catalogItem);
        catalogItem.setItemName("Updated Catalog Item");
        assertThat(externalCatalogItemDao.update(catalogItem).getItemName()).isEqualTo("Updated Catalog Item");
        catalogItem.setItemName("Upserted Catalog Item");
        assertThat(externalCatalogItemDao.upsert(catalogItem).getItemName()).isEqualTo("Upserted Catalog Item");
        ExternalCatalogItem naturalKeyCatalogItem = externalCatalogItem("dao-item-1", "dao-unique-1");
        naturalKeyCatalogItem.setItemName("Natural Key Upserted Catalog Item");
        assertThat(externalCatalogItemDao.upsert(naturalKeyCatalogItem))
                .extracting(ExternalCatalogItem::getExternalCatalogItemId, ExternalCatalogItem::getItemName)
                .containsExactly(catalogItem.getExternalCatalogItemId(), "Natural Key Upserted Catalog Item");
        assertThat(externalCatalogItemDao.findByExternalCatalogItemId(catalogItem.getExternalCatalogItemId())).isPresent();
        assertThat(externalCatalogItemDao.findByExternalServiceIdAndExternalItemKey(2, "dao-item-1")).isPresent();
        assertThat(externalCatalogItemDao.findByExternalServiceIdAndExternalUniqueKey(2, "dao-unique-1")).isPresent();
        assertThat(externalCatalogItemDao.findAll()).extracting(ExternalCatalogItem::getExternalItemKey).contains("dao-item-1");

        ExternalCatalogItemCategory itemCategory = ExternalCatalogItemCategory.builder()
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .externalCategoryId(category.getExternalCategoryId())
                .primary(true)
                .build();
        itemCategory = externalCatalogItemCategoryDao.insert(itemCategory);
        itemCategory.setPrimary(false);
        assertThat(externalCatalogItemCategoryDao.update(itemCategory).getPrimary()).isFalse();
        itemCategory.setPrimary(true);
        assertThat(externalCatalogItemCategoryDao.upsert(itemCategory).getPrimary()).isTrue();
        assertThat(externalCatalogItemCategoryDao.findByExternalCatalogItemIdAndExternalCategoryId(catalogItem.getExternalCatalogItemId(), category.getExternalCategoryId())).isPresent();
        assertThat(externalCatalogItemCategoryDao.findByExternalCatalogItemId(catalogItem.getExternalCatalogItemId())).hasSize(1);
        assertThat(externalCatalogItemCategoryDao.findByExternalCategoryId(category.getExternalCategoryId())).hasSize(1);
        assertThat(externalCatalogItemCategoryDao.findAll()).hasSize(1);

        ItemInventory inventory = itemInventory("dao-inventory-1");
        inventory = itemInventoryDao.insert(inventory);
        inventory.setDescription("Updated inventory");
        assertThat(itemInventoryDao.update(inventory).getDescription()).isEqualTo("Updated inventory");
        inventory.setDescription("Upserted inventory");
        assertThat(itemInventoryDao.upsert(inventory).getDescription()).isEqualTo("Upserted inventory");
        ItemInventory naturalKeyInventory = itemInventory("dao-inventory-1");
        naturalKeyInventory.setDescription("Natural Key Upserted inventory");
        assertThat(itemInventoryDao.upsert(naturalKeyInventory))
                .extracting(ItemInventory::getItemInventoryId, ItemInventory::getDescription)
                .containsExactly(inventory.getItemInventoryId(), "Natural Key Upserted inventory");
        assertThat(itemInventoryDao.findByItemInventoryId(inventory.getItemInventoryId())).isPresent();
        assertThat(itemInventoryDao.findByUuid("dao-inventory-1")).isPresent();
        assertThat(itemInventoryDao.findAll()).extracting(ItemInventory::getUuid).contains("dao-inventory-1");

        ItemInventoryExternalCatalogItem inventoryCatalogItem = ItemInventoryExternalCatalogItem.builder()
                .itemInventoryId(inventory.getItemInventoryId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .primary(true)
                .build();
        inventoryCatalogItem = itemInventoryExternalCatalogItemDao.insert(inventoryCatalogItem);
        inventoryCatalogItem.setPrimary(false);
        assertThat(itemInventoryExternalCatalogItemDao.update(inventoryCatalogItem).getPrimary()).isFalse();
        inventoryCatalogItem.setPrimary(true);
        assertThat(itemInventoryExternalCatalogItemDao.upsert(inventoryCatalogItem).getPrimary()).isTrue();
        assertThat(itemInventoryExternalCatalogItemDao.findByItemInventoryIdAndExternalCatalogItemId(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId())).isPresent();
        assertThat(itemInventoryExternalCatalogItemDao.findByItemInventoryId(inventory.getItemInventoryId())).hasSize(1);
        assertThat(itemInventoryExternalCatalogItemDao.findByExternalCatalogItemId(catalogItem.getExternalCatalogItemId())).hasSize(1);
        assertThat(itemInventoryExternalCatalogItemDao.findAll()).hasSize(1);

        itemInventoryExternalCatalogItemDao.delete(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId());
        externalCatalogItemCategoryDao.delete(catalogItem.getExternalCatalogItemId(), category.getExternalCategoryId());
        itemInventoryDao.delete(inventory.getItemInventoryId());
        externalCatalogItemDao.delete(catalogItem.getExternalCatalogItemId());
        externalCategoryDao.delete(category.getExternalCategoryId());
    }

    @Test
    void marketplaceDaosSupportBaselineCrud() {
        ExternalCatalogItem catalogItem = externalCatalogItemDao.insert(externalCatalogItem("dao-listing-item", "dao-listing-unique"));
        ItemInventory inventory = itemInventoryDao.insert(itemInventory("dao-listing-inventory"));
        MarketplaceListing listing = marketplaceListing(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId(), 2, "DAO-BL-1");

        listing = marketplaceListingDao.insert(listing);
        listing.setTitle("Updated listing");
        assertThat(marketplaceListingDao.update(listing).getTitle()).isEqualTo("Updated listing");
        listing.setTitle("Upserted listing");
        assertThat(marketplaceListingDao.upsert(listing).getTitle()).isEqualTo("Upserted listing");
        MarketplaceListing naturalKeyListing = marketplaceListing(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId(), 2, "DAO-BL-1");
        naturalKeyListing.setTitle("Natural Key Upserted listing");
        assertThat(marketplaceListingDao.upsert(naturalKeyListing))
                .extracting(MarketplaceListing::getMarketplaceListingId, MarketplaceListing::getTitle)
                .containsExactly(listing.getMarketplaceListingId(), "Natural Key Upserted listing");
        assertThat(marketplaceListingDao.findByMarketplaceListingId(listing.getMarketplaceListingId())).isPresent();
        assertThat(marketplaceListingDao.findByListingExternalServiceIdAndExternalListingId(2, "DAO-BL-1")).isPresent();
        assertThat(marketplaceListingDao.findByItemInventoryId(inventory.getItemInventoryId())).hasSize(1);
        assertThat(marketplaceListingDao.findAll()).hasSize(1);

        BricklinkMarketplaceListing bricklink = bricklinkMarketplaceListing(listing.getMarketplaceListingId(), 3001);
        assertThat(bricklinkMarketplaceListingDao.insert(bricklink).getBricklinkInventoryId()).isEqualTo(3001);
        bricklink.setLastRemoteQuantity(4);
        assertThat(bricklinkMarketplaceListingDao.update(bricklink).getLastRemoteQuantity()).isEqualTo(4);
        bricklink.setLastRemoteQuantity(5);
        assertThat(bricklinkMarketplaceListingDao.upsert(bricklink).getLastRemoteQuantity()).isEqualTo(5);
        assertThat(bricklinkMarketplaceListingDao.findByMarketplaceListingId(listing.getMarketplaceListingId())).isPresent();
        assertThat(bricklinkMarketplaceListingDao.findByBricklinkInventoryId(3001)).isPresent();
        assertThat(bricklinkMarketplaceListingDao.findAll()).hasSize(1);
        bricklinkMarketplaceListingDao.delete(listing.getMarketplaceListingId());

        MarketplaceListing ebayListing = marketplaceListingDao.insert(marketplaceListing(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId(), 3, "DAO-EBAY-1"));
        EbayMarketplaceListing ebay = ebayMarketplaceListing(ebayListing.getMarketplaceListingId(), "dao-ebay-item-1");
        assertThat(ebayMarketplaceListingDao.insert(ebay).getEbayItemId()).isEqualTo("dao-ebay-item-1");
        ebay.setListingFormat("AUCTION");
        assertThat(ebayMarketplaceListingDao.update(ebay).getListingFormat()).isEqualTo("AUCTION");
        ebay.setDuration("DAYS_7");
        assertThat(ebayMarketplaceListingDao.upsert(ebay).getDuration()).isEqualTo("DAYS_7");
        assertThat(ebayMarketplaceListingDao.findByMarketplaceListingId(ebayListing.getMarketplaceListingId())).isPresent();
        assertThat(ebayMarketplaceListingDao.findByEbayItemId("dao-ebay-item-1")).isPresent();
        assertThat(ebayMarketplaceListingDao.findAll()).hasSize(1);

        EbayListingItemSpecific specific = EbayListingItemSpecific.builder()
                .marketplaceListingId(ebayListing.getMarketplaceListingId())
                .name("Brand")
                .value("LEGO")
                .build();
        assertThat(ebayListingItemSpecificDao.insert(specific)).isEqualTo(specific);
        assertThat(ebayListingItemSpecificDao.update(specific)).isEqualTo(specific);
        assertThat(ebayListingItemSpecificDao.upsert(specific)).isEqualTo(specific);
        assertThat(ebayListingItemSpecificDao.findByMarketplaceListingIdAndNameAndValue(ebayListing.getMarketplaceListingId(), "Brand", "LEGO")).isPresent();
        assertThat(ebayListingItemSpecificDao.findByMarketplaceListingId(ebayListing.getMarketplaceListingId())).hasSize(1);
        assertThat(ebayListingItemSpecificDao.findByName("Brand")).hasSize(1);
        assertThat(ebayListingItemSpecificDao.findAll()).hasSize(1);
        ebayListingItemSpecificDao.delete(ebayListing.getMarketplaceListingId(), "Brand", "LEGO");
        ebayMarketplaceListingDao.delete(ebayListing.getMarketplaceListingId());
        marketplaceListingDao.delete(ebayListing.getMarketplaceListingId());
        marketplaceListingDao.delete(listing.getMarketplaceListingId());
    }

    private ExternalService externalService(Integer id, String serviceCode, String displayName, Integer externalServiceTypeId) {
        ExternalService service = new ExternalService();
        service.setExternalServiceId(id);
        service.setServiceCode(serviceCode);
        service.setDisplayName(displayName);
        service.setServiceUrl("https://" + serviceCode.toLowerCase().replace('_', '-') + ".example");
        service.setExternalServiceTypeId(externalServiceTypeId);
        return service;
    }

    private ExternalCatalogItem externalCatalogItem(String itemKey, String uniqueKey) {
        return ExternalCatalogItem.builder()
                .externalServiceId(2)
                .externalItemKey(itemKey)
                .externalUniqueKey(uniqueKey)
                .itemName("Catalog Item")
                .itemTypeCode("SET")
                .itemUrl("https://catalog.example/" + itemKey)
                .yearReleased(2026)
                .build();
    }

    private ItemInventory itemInventory(String uuid) {
        seedDefaultCondition();
        ItemInventory itemInventory = new ItemInventory();
        itemInventory.setUuid(uuid);
        itemInventory.setBoxNumber(1);
        itemInventory.setPurchasePrice(new BigDecimal("12.34"));
        itemInventory.setDescription("Inventory " + uuid);
        itemInventory.setActive(true);
        itemInventory.setForSale(false);
        itemInventory.setNewOrUsed("USED");
        itemInventory.setCompleteness("COMPLETE");
        itemInventory.setItemConditionId(1);
        itemInventory.setBoxConditionId(1);
        itemInventory.setInstructionsConditionId(1);
        itemInventory.setSealed(false);
        itemInventory.setBuiltOnce(true);
        return itemInventory;
    }

    private MarketplaceListing marketplaceListing(Integer itemInventoryId, Integer externalCatalogItemId, Integer serviceId, String externalListingId) {
        return MarketplaceListing.builder()
                .itemInventoryId(itemInventoryId)
                .listingExternalServiceId(serviceId)
                .externalCatalogItemId(externalCatalogItemId)
                .externalListingId(externalListingId)
                .externalListingUrl("https://market.example/" + externalListingId)
                .listingStatusCode("ACTIVE")
                .title("Listing " + externalListingId)
                .description("Description")
                .privateNotes("Notes")
                .unitPrice(new BigDecimal("19.99"))
                .currencyCode("USD")
                .fixedPrice(true)
                .build();
    }

    private BricklinkMarketplaceListing bricklinkMarketplaceListing(Integer marketplaceListingId, Integer bricklinkInventoryId) {
        return BricklinkMarketplaceListing.builder()
                .marketplaceListingId(marketplaceListingId)
                .bricklinkInventoryId(bricklinkInventoryId)
                .bricklinkInventoryStatusCode("Y")
                .bricklinkDateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .colorId(1)
                .colorName("Blue")
                .bulk(1)
                .isRetain(false)
                .isStockRoom(false)
                .saleRate(10)
                .lastRemoteQuantity(1)
                .build();
    }

    private EbayMarketplaceListing ebayMarketplaceListing(Integer marketplaceListingId, String ebayItemId) {
        return EbayMarketplaceListing.builder()
                .marketplaceListingId(marketplaceListingId)
                .ebayItemId(ebayItemId)
                .ebayCategoryId("19006")
                .conditionId("1000")
                .conditionDescriptorFields("{}")
                .listingFormat("FIXED_PRICE")
                .duration("GTC")
                .shippingPolicyId("ship-1")
                .paymentPolicyId("pay-1")
                .returnPolicyId("return-1")
                .build();
    }

    private void seedDefaultCondition() {
        conditionDao.findConditionById(1)
                .orElseGet(() -> {
                    Condition condition = Condition.builder()
                            .conditionId(1)
                            .conditionCode("G")
                            .conditionDescription("Good")
                            .conditionText("Good")
                            .build();
                    conditionDao.insert(condition);
                    return condition;
                });
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
