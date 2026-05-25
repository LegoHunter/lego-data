package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkItemInventory;
import io.legohunter.data.dto.Category;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.ExternalItem;
import io.legohunter.data.dto.ExternalItemInventory;
import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalImageAlbumImage;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.enums.PhotoStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

abstract class MapperTestSupport {

    @Autowired CarrierMapper carrierMapper;
    @Autowired CategoryMapper categoryMapper;
    @Autowired ConditionMapper conditionMapper;
    @Autowired CostTypeMapper costTypeMapper;
    @Autowired ExternalItemMapper externalItemMapper;
    @Autowired ExternalItemInventoryMapper externalItemInventoryMapper;
    @Autowired ExternalImageAlbumMapper externalImageAlbumMapper;
    @Autowired ExternalImageMapper externalImageMapper;
    @Autowired ExternalImageAlbumImageMapper externalImageAlbumImageMapper;
    @Autowired ExternalServiceMapper externalServiceMapper;
    @Autowired ExternalServiceTypeMapper externalServiceTypeMapper;
    @Autowired InventoryIndexMapper inventoryIndexMapper;
    @Autowired ItemInventoryMapper itemInventoryMapper;
    @Autowired ItemInventoryPhotoMapper itemInventoryPhotoMapper;
    @Autowired BricklinkItemInventoryMapper bricklinkItemInventoryMapper;
    @Autowired PartyMapper partyMapper;
    @Autowired PaymentPlatformMapper paymentPlatformMapper;
    @Autowired TransactionCostMapper transactionCostMapper;
    @Autowired TransactionItemCostMapper transactionItemCostMapper;
    @Autowired TransactionItemMapper transactionItemMapper;
    @Autowired TransactionPlatformMapper transactionPlatformMapper;
    @Autowired TransactionsMapper transactionsMapper;
    @Autowired TransactionTypeMapper transactionTypeMapper;

    void seedExternalCatalog() {
        externalServiceTypeMapper.insertExternalServiceType(ExternalServiceType.builder()
                .externalServiceTypeId(2)
                .externalServiceTypeName("MARKETPLACE")
                .externalServiceTypeDescription("Marketplace")
                .build());
        externalServiceMapper.insertExternalService(externalService(2, "BRICKLINK"));
        categoryMapper.insert(category(2, 5, "Brick", null));
    }

    ExternalService externalService(Integer id, String name) {
        ExternalService service = new ExternalService();
        service.setExternalServiceId(id);
        service.setExternalServiceName(name);
        service.setExternalServiceUrl("https://" + name.toLowerCase() + ".example");
        service.setExternalServiceTypeId(2);
        return service;
    }

    Category category(Integer externalServiceId, Integer externalCategoryId, String name, Integer parentId) {
        Category category = new Category();
        category.setExternalServiceId(externalServiceId);
        category.setExternalCategoryId(externalCategoryId);
        category.setCategoryName(name);
        category.setParentId(parentId);
        return category;
    }

    ExternalItem externalItem(String number, Long uniqueId, String name, Integer categoryId, Integer serviceId) {
        ExternalItem externalItem = new ExternalItem();
        externalItem.setExternalNumber(number);
        externalItem.setUniqueId(uniqueId);
        externalItem.setName(name);
        externalItem.setItemType("SET");
        externalItem.setUrl("https://bricklink.example/" + number);
        externalItem.setCategoryId(categoryId);
        externalItem.setServiceId(serviceId);
        externalItem.setYearReleased(2026);
        return externalItem;
    }

    ItemInventory itemInventory(String uuid) {
        ItemInventory itemInventory = new ItemInventory();
        itemInventory.setUuid(uuid);
        itemInventory.setBoxNumber(1);
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

    ExternalItem insertExternalItem(String number) {
        seedExternalCatalog();
        ExternalItem externalItem = externalItem(number, 789L, "Bricklink item", 5, 2);
        externalItemMapper.insert(externalItem);
        return externalItem;
    }

    ExternalItemInventory insertExternalItemInventory(Integer externalItemId, Integer itemInventoryId) {
        ExternalItemInventory externalItemInventory = ExternalItemInventory.builder()
                .externalItemId(externalItemId)
                .itemInventoryId(itemInventoryId)
                .fixedPrice(false)
                .build();
        externalItemInventoryMapper.insert(externalItemInventory);
        return externalItemInventory;
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

    BricklinkItemInventory bricklinkItemInventory(Integer externalItemId, Integer itemInventoryId) {
        return BricklinkItemInventory.builder()
                .bricklinkItemInventoryId(1)
                .externalItemId(externalItemId)
                .itemInventoryId(itemInventoryId)
                .inventoryId(1L)
                .itemType("SET")
                .colorId(1)
                .colorName("Blue")
                .quantity(1)
                .unitPrice(9.99)
                .bindId(2)
                .description("Description")
                .remarks("Remarks")
                .bulk(1)
                .isRetain(false)
                .isStockRoom(false)
                .stockRoomId("A")
                .dateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .myCost(7.50)
                .saleRate(10)
                .tierQuantity1(2)
                .tierQuantity2(3)
                .tierQuantity3(4)
                .tierPrice1(8.99)
                .tierPrice2(7.99)
                .tierPrice3(6.99)
                .myWeight(1.25)
                .build();
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
