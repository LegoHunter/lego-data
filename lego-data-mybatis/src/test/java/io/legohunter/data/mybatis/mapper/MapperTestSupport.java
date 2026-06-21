package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.EbayListingItemSpecific;
import io.legohunter.data.dto.EbayMarketplaceListing;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCatalogItemCategory;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalImageAlbumImage;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.dto.PricingSnapshotListing;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.enums.PhotoStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

abstract class MapperTestSupport {

    @Autowired CarrierMapper carrierMapper;
    @Autowired ConditionMapper conditionMapper;
    @Autowired CostTypeMapper costTypeMapper;
    @Autowired ExternalCatalogItemMapper externalCatalogItemMapper;
    @Autowired ExternalCatalogItemCategoryMapper externalCatalogItemCategoryMapper;
    @Autowired ExternalCategoryMapper externalCategoryMapper;
    @Autowired ExternalImageAlbumMapper externalImageAlbumMapper;
    @Autowired ExternalImageMapper externalImageMapper;
    @Autowired ExternalImageAlbumImageMapper externalImageAlbumImageMapper;
    @Autowired ExternalServiceCapabilityMapper externalServiceCapabilityMapper;
    @Autowired ExternalServiceMapper externalServiceMapper;
    @Autowired ExternalServiceTypeMapper externalServiceTypeMapper;
    @Autowired InventoryIndexMapper inventoryIndexMapper;
    @Autowired ItemInventoryExternalCatalogItemMapper itemInventoryExternalCatalogItemMapper;
    @Autowired ItemInventoryMapper itemInventoryMapper;
    @Autowired ItemInventoryPhotoMapper itemInventoryPhotoMapper;
    @Autowired ItemInventorySaleIntentMapper itemInventorySaleIntentMapper;
    @Autowired ItemInventoryStateMapper itemInventoryStateMapper;
    @Autowired MarketplaceListingMapper marketplaceListingMapper;
    @Autowired BricklinkMarketplaceListingMapper bricklinkMarketplaceListingMapper;
    @Autowired EbayMarketplaceListingMapper ebayMarketplaceListingMapper;
    @Autowired EbayListingItemSpecificMapper ebayListingItemSpecificMapper;
    @Autowired PricingCrawlWorkItemMapper pricingCrawlWorkItemMapper;
    @Autowired PricingSnapshotMapper pricingSnapshotMapper;
    @Autowired PricingSnapshotListingMapper pricingSnapshotListingMapper;
    @Autowired PricingDecisionMapper pricingDecisionMapper;
    @Autowired PartyMapper partyMapper;
    @Autowired PaymentPlatformMapper paymentPlatformMapper;
    @Autowired TransactionCostMapper transactionCostMapper;
    @Autowired TransactionItemCostMapper transactionItemCostMapper;
    @Autowired TransactionItemMapper transactionItemMapper;
    @Autowired TransactionPlatformMapper transactionPlatformMapper;
    @Autowired TransactionsMapper transactionsMapper;
    @Autowired TransactionTypeMapper transactionTypeMapper;

    void seedExternalCatalog() {
        externalServiceTypeMapper.findByExternalServiceTypeId(2)
                .orElseGet(() -> {
                    ExternalServiceType serviceType = ExternalServiceType.builder()
                            .externalServiceTypeId(2)
                            .externalServiceTypeName("MARKETPLACE")
                            .externalServiceTypeDescription("Marketplace")
                            .build();
                    externalServiceTypeMapper.insert(serviceType);
                    return serviceType;
                });
        externalServiceMapper.findByExternalServiceId(2)
                .orElseGet(() -> {
                    ExternalService service = externalService(2, "BRICKLINK", "BrickLink", 2);
                    externalServiceMapper.insert(service);
                    return service;
                });
        externalServiceTypeMapper.findByExternalServiceTypeId(3)
                .orElseGet(() -> {
                    ExternalServiceType serviceType = ExternalServiceType.builder()
                            .externalServiceTypeId(3)
                            .externalServiceTypeName("AUCTION")
                            .externalServiceTypeDescription("Auction")
                            .build();
                    externalServiceTypeMapper.insert(serviceType);
                    return serviceType;
                });
        externalServiceMapper.findByExternalServiceId(3)
                .orElseGet(() -> {
                    ExternalService service = externalService(3, "EBAY", "eBay", 3);
                    externalServiceMapper.insert(service);
                    return service;
                });
        externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "5")
                .orElseGet(() -> {
                    ExternalCategory category = externalCategory(2, "5", "Brick", null);
                    externalCategoryMapper.insert(category);
                    return category;
                });
    }

    void insertImageHostingService() {
        externalServiceTypeMapper.findByExternalServiceTypeId(5)
                .orElseGet(() -> {
                    ExternalServiceType serviceType = ExternalServiceType.builder()
                            .externalServiceTypeId(5)
                            .externalServiceTypeName("IMAGE_HOSTING")
                            .externalServiceTypeDescription("Image Hosting Service")
                            .build();
                    externalServiceTypeMapper.insert(serviceType);
                    return serviceType;
                });
        externalServiceMapper.findByExternalServiceId(10)
                .orElseGet(() -> {
                    ExternalService service = externalService(10, "FLICKR", "Flickr", 5);
                    externalServiceMapper.insert(service);
                    return service;
                });
    }

    ExternalService externalService(Integer id, String serviceCode, String displayName, Integer externalServiceTypeId) {
        ExternalService service = new ExternalService();
        service.setExternalServiceId(id);
        service.setServiceCode(serviceCode);
        service.setDisplayName(displayName);
        service.setServiceUrl("https://" + serviceCode.toLowerCase().replace('_', '-') + ".example");
        service.setExternalServiceTypeId(externalServiceTypeId);
        return service;
    }

    ExternalServiceCapability externalServiceCapability(Integer externalServiceId, String capabilityCode) {
        return ExternalServiceCapability.builder()
                .externalServiceId(externalServiceId)
                .capabilityCode(capabilityCode)
                .build();
    }

    ExternalCategory externalCategory(Integer externalServiceId, String externalCategoryKey, String name, Integer parentExternalCategoryId) {
        return ExternalCategory.builder()
                .externalServiceId(externalServiceId)
                .externalCategoryKey(externalCategoryKey)
                .categoryName(name)
                .parentExternalCategoryId(parentExternalCategoryId)
                .build();
    }

    ExternalCatalogItem externalCatalogItem(String externalItemKey, String externalUniqueKey, String name, Integer serviceId) {
        return ExternalCatalogItem.builder()
                .externalServiceId(serviceId)
                .externalItemKey(externalItemKey)
                .externalUniqueKey(externalUniqueKey)
                .itemName(name)
                .itemTypeCode("SET")
                .itemUrl("https://bricklink.example/" + externalItemKey)
                .yearReleased(2026)
                .build();
    }

    ExternalCatalogItem insertExternalCatalogItem(String externalItemKey) {
        seedExternalCatalog();
        ExternalCatalogItem externalCatalogItem = externalCatalogItem(externalItemKey, "unique-" + externalItemKey, "Catalog item", 2);
        externalCatalogItemMapper.insert(externalCatalogItem);
        return externalCatalogItem;
    }

    ItemInventory itemInventory(String uuid) {
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

    ItemInventory insertItemInventory(String uuid) {
        seedDefaultCondition();
        ItemInventory itemInventory = itemInventory(uuid);
        itemInventoryMapper.insert(itemInventory);
        return itemInventory;
    }

    void seedDefaultCondition() {
        conditionMapper.findConditionById(1)
                .orElseGet(() -> {
                    Condition condition = Condition.builder()
                            .conditionId(1)
                            .conditionCode("G")
                            .conditionDescription("Good")
                            .conditionText("Good")
                            .build();
                    conditionMapper.insert(condition);
                    return condition;
                });
    }

    ExternalCatalogItemCategory externalCatalogItemCategory(Integer externalCatalogItemId, Integer externalCategoryId) {
        return ExternalCatalogItemCategory.builder()
                .externalCatalogItemId(externalCatalogItemId)
                .externalCategoryId(externalCategoryId)
                .primary(true)
                .build();
    }

    ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem(Integer itemInventoryId, Integer externalCatalogItemId) {
        return ItemInventoryExternalCatalogItem.builder()
                .itemInventoryId(itemInventoryId)
                .externalCatalogItemId(externalCatalogItemId)
                .primary(true)
                .build();
    }

    MarketplaceListing marketplaceListing(Integer itemInventoryId, Integer externalCatalogItemId, Integer externalServiceId, String externalListingId) {
        return MarketplaceListing.builder()
                .itemInventoryId(itemInventoryId)
                .listingExternalServiceId(externalServiceId)
                .externalCatalogItemId(externalCatalogItemId)
                .externalListingId(externalListingId)
                .externalListingUrl("https://market.example/listing/" + externalListingId)
                .listingStatusCode("ACTIVE")
                .title("Listing " + externalListingId)
                .description("Listing description")
                .privateNotes("Private notes")
                .unitPrice(new BigDecimal("19.99"))
                .currencyCode("USD")
                .fixedPrice(true)
                .build();
    }

    BricklinkMarketplaceListing bricklinkMarketplaceListing(Integer marketplaceListingId, Integer bricklinkInventoryId) {
        return BricklinkMarketplaceListing.builder()
                .marketplaceListingId(marketplaceListingId)
                .bricklinkInventoryId(bricklinkInventoryId)
                .bricklinkInventoryStatusCode("Y")
                .bricklinkDateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .colorId(1)
                .colorName("Blue")
                .bindId(2)
                .bulk(1)
                .isRetain(false)
                .isStockRoom(false)
                .stockRoomId("A")
                .saleRate(10)
                .tierQuantity1(2)
                .tierPrice1(new BigDecimal("8.99"))
                .tierQuantity2(3)
                .tierPrice2(new BigDecimal("7.99"))
                .tierQuantity3(4)
                .tierPrice3(new BigDecimal("6.99"))
                .myWeight(new BigDecimal("1.2500"))
                .remarks("Remarks")
                .lastRemoteQuantity(1)
                .build();
    }

    EbayMarketplaceListing ebayMarketplaceListing(Integer marketplaceListingId, String ebayItemId) {
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

    EbayListingItemSpecific ebayListingItemSpecific(Integer marketplaceListingId, String name, String value) {
        return EbayListingItemSpecific.builder()
                .marketplaceListingId(marketplaceListingId)
                .name(name)
                .value(value)
                .build();
    }

    PricingCrawlWorkItem pricingCrawlWorkItem(
            Integer marketplaceListingId,
            Integer externalCatalogItemId,
            Integer sourceExternalServiceId,
            ZonedDateTime nextAttemptAt
    ) {
        return PricingCrawlWorkItem.builder()
                .marketplaceListingId(marketplaceListingId)
                .externalCatalogItemId(externalCatalogItemId)
                .sourceExternalServiceId(sourceExternalServiceId)
                .workStatusCode("PENDING")
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(nextAttemptAt)
                .sourceRequestUrl("https://bricklink.example/ajax/clone/catalogifs.ajax")
                .sourceRequestParameters("itemid=4997&rpp=500&iconly=0")
                .build();
    }

    PricingSnapshot pricingSnapshot(
            Long pricingCrawlWorkItemId,
            Integer marketplaceListingId,
            Integer externalCatalogItemId,
            Integer sourceExternalServiceId,
            ZonedDateTime capturedAt
    ) {
        return PricingSnapshot.builder()
                .pricingCrawlWorkItemId(pricingCrawlWorkItemId)
                .marketplaceListingId(marketplaceListingId)
                .externalCatalogItemId(externalCatalogItemId)
                .sourceExternalServiceId(sourceExternalServiceId)
                .sourceItemKey("6390-1")
                .sourceUniqueKey("4997")
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .sourceRequestUrl("https://bricklink.example/ajax/clone/catalogifs.ajax")
                .sourceRequestParameters("itemid=4997&rpp=500&iconly=0")
                .rawPayloadHash("payload-hash")
                .comparableCount(2)
                .capturedAt(capturedAt)
                .build();
    }

    PricingSnapshotListing pricingSnapshotListing(Long pricingSnapshotId, String externalListingId) {
        return PricingSnapshotListing.builder()
                .pricingSnapshotId(pricingSnapshotId)
                .externalListingId(externalListingId)
                .sellerName("seller-one")
                .sellerCountryCode("US")
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .quantityAvailable(1)
                .unitPrice(new BigDecimal("220.00"))
                .currencyCode("USD")
                .description("Main Street")
                .extendedDescription("Complete with box")
                .sourceListingPayload("{\"listing\":\"" + externalListingId + "\"}")
                .build();
    }

    PricingDecision pricingDecision(Integer marketplaceListingId, Long pricingSnapshotId) {
        return PricingDecision.builder()
                .marketplaceListingId(marketplaceListingId)
                .pricingSnapshotId(pricingSnapshotId)
                .algorithmVersion("BRICKLINK_COMPETITIVE_V1")
                .decisionStatusCode("PROPOSED")
                .reasonCode("MATCHED_LOWEST_COMPETITOR")
                .strategyCode("COMPETITIVE")
                .computedPrice(new BigDecimal("219.00"))
                .finalPrice(new BigDecimal("219.00"))
                .previousPrice(new BigDecimal("225.00"))
                .currencyCode("USD")
                .comparableCount(2)
                .confidence(new BigDecimal("0.7500"))
                .sourceSummaryJson("{\"snapshot\":true}")
                .notes("Initial recommendation")
                .build();
    }

    ExternalImageAlbum externalImageAlbum(Integer externalServiceId, Integer itemInventoryId, String externalAlbumId) {
        return ExternalImageAlbum.builder()
                .externalServiceId(externalServiceId)
                .itemInventoryId(itemInventoryId)
                .externalAlbumId(externalAlbumId)
                .title("3001-1 [uuid]")
                .albumUrl("https://photos.example/albums/" + externalAlbumId)
                .shortUrl("https://short.example/" + externalAlbumId)
                .syncStatus(ExternalSyncStatus.PENDING)
                .build();
    }

    ExternalImage externalImage(Integer externalServiceId, Integer itemInventoryPhotoId, String externalServiceImageId, String md5) {
        return ExternalImage.builder()
                .externalServiceId(externalServiceId)
                .itemInventoryPhotoId(itemInventoryPhotoId)
                .externalServiceImageId(externalServiceImageId)
                .title("3001-1 [uuid]")
                .imageUrl("https://photos.example/images/" + externalServiceImageId)
                .md5AtUpload(md5)
                .metadataHashAtSync("metadata-" + md5)
                .syncStatus(ExternalSyncStatus.PENDING)
                .build();
    }

    ExternalImageAlbumImage externalImageAlbumImage(Long externalImageAlbumId, Long externalImageId, int sortOrder, boolean primary) {
        return ExternalImageAlbumImage.builder()
                .externalImageAlbumId(externalImageAlbumId)
                .externalImageId(externalImageId)
                .sortOrder(sortOrder)
                .primary(primary)
                .build();
    }

    ItemInventoryPhoto insertItemInventoryPhoto(Integer itemInventoryId, String md5) {
        ItemInventoryPhoto photo = ItemInventoryPhoto.builder()
                .itemInventoryId(itemInventoryId)
                .s3Bucket("lego-photos-sandbox")
                .s3Key("3001/uuid/" + md5 + ".jpg")
                .md5(md5)
                .metadataHash("metadata-" + md5)
                .fileName(md5 + ".jpg")
                .fileSize(1000L)
                .primary(false)
                .status(PhotoStatus.PROCESSED)
                .build();
        itemInventoryPhotoMapper.insert(photo);
        return photo;
    }

    Party party(String name) {
        return Party.builder()
                .partyFirstName(name)
                .partyMiddleInitial("Q")
                .partyLastName("Tester")
                .partyAddress1("123 Main")
                .partyAddress2("Suite 1")
                .partyCity("Original City")
                .partyState("NC")
                .partyPostalCode("12345")
                .partyCountryCode("US")
                .partyCountry("United States")
                .partyPhone("555-0100")
                .partyEmail(name.toLowerCase() + "@example.com")
                .partyType("PERSON")
                .partyActivationDate(LocalDateTime.parse("2026-01-01T00:00:00"))
                .build();
    }

    Transactions insertTransaction() {
        Party fromParty = party("From");
        partyMapper.insert(fromParty);
        Party toParty = party("To");
        partyMapper.insert(toParty);
        transactionPlatformMapper.insert(TransactionPlatform.builder()
                .transactionPlatformId(1)
                .transactionPlatformName("BrickLink")
                .build());
        Transactions transaction = Transactions.builder()
                .transactionDateTime(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .notes("Transaction")
                .fromPartyId(fromParty.getPartyId())
                .toPartyId(toParty.getPartyId())
                .transactionPlatformId(1)
                .transactionOrderId("ORDER-1")
                .build();
        transactionsMapper.insert(transaction);
        return transaction;
    }

    void insertTransactionType() {
        transactionTypeMapper.insert(TransactionType.builder()
                .transactionTypeCode("BUY")
                .transactionTypeDescription("Buy")
                .conversionFactor(1)
                .build());
    }
}
