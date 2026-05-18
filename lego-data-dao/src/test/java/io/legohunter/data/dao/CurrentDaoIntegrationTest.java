package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.BricklinkItemInventory;
import io.legohunter.data.dto.Carrier;
import io.legohunter.data.dto.Category;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.ExternalItem;
import io.legohunter.data.dto.ExternalItemInventory;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceItem;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.InventoryIndex;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.PaymentPlatform;
import io.legohunter.data.dto.TransactionCost;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.TransactionItemCost;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.enums.CurrencyCode;
import io.legohunter.data.enums.PhotoStatus;
import io.legohunter.data.mybatis.mapper.TransactionItemCostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = "/scripts/db/h2/current_schema.ddl")
class CurrentDaoIntegrationTest {

    @Autowired CarrierDao carrierDao;
    @Autowired CategoryDao categoryDao;
    @Autowired ConditionDao conditionDao;
    @Autowired CostTypeDao costTypeDao;
    @Autowired ExternalItemDao externalItemDao;
    @Autowired ExternalItemInventoryDao externalItemInventoryDao;
    @Autowired ExternalServiceDao externalServiceDao;
    @Autowired ExternalServiceItemDao externalServiceItemDao;
    @Autowired ExternalServiceTypeDao externalServiceTypeDao;
    @Autowired InventoryIndexDao inventoryIndexDao;
    @Autowired ItemInventoryDao itemInventoryDao;
    @Autowired ItemInventoryPhotoDao itemInventoryPhotoDao;
    @Autowired BricklinkItemInventoryDao bricklinkItemInventoryDao;
    @Autowired PartyDao partyDao;
    @Autowired PaymentPlatformDao paymentPlatformDao;
    @Autowired TransactionCostDao transactionCostDao;
    @Autowired TransactionItemDao transactionItemDao;
    @Autowired TransactionPlatformDao transactionPlatformDao;
    @Autowired TransactionsDao transactionsDao;
    @Autowired TransactionTypeDao transactionTypeDao;
    @Autowired TransactionItemCostMapper transactionItemCostMapper;

    @Test
    void referenceDataDaosDelegateCrudOperations() {
        Carrier carrier = Carrier.builder()
                .carrierCode("UPS")
                .carrierName("UPS")
                .trackingUrlPattern("https://track.example")
                .build();
        carrierDao.insert(carrier);
        carrier.setCarrierName("United Parcel Service");
        carrierDao.update(carrier);
        assertThat(carrierDao.findCarrierByCode("UPS"))
                .hasValueSatisfying(found -> assertThat(found.getCarrierName()).isEqualTo("United Parcel Service"));
        assertThat(carrierDao.findAll()).hasSize(1);

        Condition condition = Condition.builder()
                .conditionId(1)
                .conditionCode("G")
                .conditionDescription("Good")
                .conditionText("Good text")
                .build();
        conditionDao.insert(condition);
        condition.setConditionText("Updated text");
        conditionDao.update(condition);
        assertThat(conditionDao.findConditionById(1))
                .hasValueSatisfying(found -> assertThat(found.getConditionText()).isEqualTo("Updated text"));
        assertThat(conditionDao.findByConditionCode("G")).isPresent();
        assertThat(conditionDao.findAll()).hasSize(1);

        CostType costType = CostType.builder()
                .costTypeCode("SHIP")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping")
                .build();
        costTypeDao.insert(costType);
        costType.setCostTypeDescription("Postage");
        costTypeDao.update(costType);
        assertThat(costTypeDao.findCostTypeByCode("SHIP"))
                .hasValueSatisfying(found -> assertThat(found.getCostTypeDescription()).isEqualTo("Postage"));
        assertThat(costTypeDao.findAll()).hasSize(1);

        TransactionType transactionType = TransactionType.builder()
                .transactionTypeCode("BUY")
                .transactionTypeDescription("Buy")
                .conversionFactor(1)
                .build();
        transactionTypeDao.insert(transactionType);
        transactionType.setConversionFactor(2);
        transactionTypeDao.udpate(transactionType);
        assertThat(transactionTypeDao.findTransactionTypeByCode("BUY"))
                .hasValueSatisfying(found -> assertThat(found.getConversionFactor()).isEqualTo(2));
        assertThat(transactionTypeDao.findAll()).hasSize(1);

        TransactionPlatform transactionPlatform = TransactionPlatform.builder()
                .transactionPlatformId(1)
                .transactionPlatformName("BrickLink")
                .build();
        transactionPlatformDao.insert(transactionPlatform);
        transactionPlatform.setTransactionPlatformName("BrickLink Marketplace");
        transactionPlatformDao.update(transactionPlatform);
        assertThat(transactionPlatformDao.findTransactionPlatformById(1)).isPresent();
        assertThat(transactionPlatformDao.findTransactionPlatformByName("BrickLink Marketplace")).isPresent();
        assertThat(transactionPlatformDao.findAll()).hasSize(1);

        PaymentPlatform paymentPlatform = PaymentPlatform.builder()
                .paymentPlatformId(1)
                .paymentPlatformName("PayPal")
                .paymentPlatformUrl("https://paypal.example")
                .build();
        paymentPlatformDao.insert(paymentPlatform);
        paymentPlatform.setPaymentPlatformUrl("https://paypal.test");
        paymentPlatformDao.update(paymentPlatform);
        assertThat(paymentPlatformDao.findPaymentPlatformById(1))
                .hasValueSatisfying(found -> assertThat(found.getPaymentPlatformUrl()).isEqualTo("https://paypal.test"));
        assertThat(paymentPlatformDao.findPaymentPlatformByName("PayPal")).isPresent();
        assertThat(paymentPlatformDao.findAll()).hasSize(1);
    }

    @Test
    void catalogDaosDelegateCrudAndUpsertOperations() {
        seedExternalCatalog();

        assertThat(externalServiceTypeDao.findExternalServiceTypeById(1)).isPresent();
        assertThat(externalServiceTypeDao.findExternalServiceTypeByName("Marketplace")).isPresent();
        assertThat(externalServiceTypeDao.findAll()).hasSize(1);

        ExternalServiceType type = ExternalServiceType.builder()
                .externalServiceTypeId(1)
                .externalServiceTypeName("Marketplace")
                .externalServiceTypeDescription("Updated")
                .build();
        externalServiceTypeDao.update(type);
        assertThat(externalServiceTypeDao.findExternalServiceTypeById(1))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceTypeDescription()).isEqualTo("Updated"));

        assertThat(externalServiceDao.findExternalServiceById(2)).isPresent();
        assertThat(externalServiceDao.findExternalServiceByName("BRICKLINK")).isPresent();
        assertThat(externalServiceDao.findAll()).hasSize(1);
        ExternalService service = externalService(2, "BRICKLINK");
        service.setExternalServiceUrl("https://updated.example");
        externalServiceDao.update(service);
        assertThat(externalServiceDao.findExternalServiceById(2))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceUrl()).isEqualTo("https://updated.example"));

        Category category = category(2, 101, "Parts", null);
        categoryDao.insert(category);
        category.setCategoryName("Updated Parts");
        categoryDao.update(category);
        categoryDao.upsert(category(2, 101, "Upserted Parts", 100));
        assertThat(categoryDao.findCategoryByExternalServiceAndCategoryId(2, 101))
                .hasValueSatisfying(found -> assertThat(found.getParentId()).isEqualTo(100));
        assertThat(categoryDao.findAll()).hasSize(2);

        ExternalItem externalItem = externalItem("3001-1", 123L, "Brick", 100, 2);
        externalItemDao.insert(externalItem);
        externalItem.setName("Brick Updated");
        externalItemDao.update(externalItem);
        assertThat(externalItemDao.findByExternalItemId(externalItem.getExternalItemId()))
                .hasValueSatisfying(found -> assertThat(found.getName()).isEqualTo("Brick Updated"));
        assertThat(externalItemDao.findByExternalServiceAndUniqueId(2, 123)).isPresent();
        assertThat(externalItemDao.findByExternalServiceAndNumber(2, "3001-1")).isPresent();
        assertThat(externalItemDao.findAllByExternalService("BRICKLINK")).hasSize(1);
        externalItemDao.upsert(externalItem("3001-1", 456L, "Brick Upserted", 100, 2));
        assertThat(externalItemDao.findByExternalServiceAndNumber(2, "3001-1"))
                .hasValueSatisfying(found -> assertThat(found.getName()).isEqualTo("Brick Upserted"));

        ItemInventory itemInventory = itemInventory("uuid-catalog");
        itemInventoryDao.insert(itemInventory);
        ExternalServiceItem serviceItem = ExternalServiceItem.builder()
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .build();
        externalServiceItemDao.insert(serviceItem);
        assertThat(externalServiceItemDao.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .hasValue(serviceItem);
        assertThat(externalServiceItemDao.findByExternalItemId(externalItem.getExternalItemId())).hasValue(serviceItem);
        assertThat(externalServiceItemDao.findByItemId(itemInventory.getItemInventoryId())).hasValue(serviceItem);
        externalServiceItemDao.delete(serviceItem);
        assertThat(externalServiceItemDao.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .isEmpty();

        ExternalItemInventory externalItemInventory = ExternalItemInventory.builder()
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .fixedPrice(true)
                .orderId(100)
                .extendedDescription("Extended")
                .extraDescription("Extra")
                .internalComments("Internal")
                .updateTimestamp(Instant.parse("2026-01-01T00:00:00Z"))
                .lastSynchronizedTimestamp(Instant.parse("2026-01-02T00:00:00Z"))
                .build();
        externalItemInventoryDao.insert(externalItemInventory);
        externalItemInventory.setInternalComments("Updated internal");
        externalItemInventoryDao.update(externalItemInventory);
        assertThat(externalItemInventoryDao.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getInternalComments()).isEqualTo("Updated internal"));
        assertThat(externalItemInventoryDao.findByExternalItemId(externalItem.getExternalItemId())).hasSize(1);
        assertThat(externalItemInventoryDao.findByItemInventoryId(itemInventory.getItemInventoryId())).hasSize(1);
    }

    @Test
    void inventoryDaosHandleDomainQueriesAndUpserts() {
        ItemInventory itemInventory = itemInventory("uuid-inventory");
        itemInventoryDao.upsert(itemInventory);
        assertThat(itemInventory.getItemInventoryId()).isNotNull();
        itemInventory.setDescription("Updated by id");
        itemInventoryDao.upsert(itemInventory);
        assertThat(itemInventoryDao.findByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getDescription()).isEqualTo("Updated by id"));

        ItemInventory sameUuid = itemInventory("uuid-inventory");
        sameUuid.setDescription("Updated by uuid");
        itemInventoryDao.upsert(sameUuid);
        assertThat(sameUuid.getItemInventoryId()).isEqualTo(itemInventory.getItemInventoryId());
        assertThat(itemInventoryDao.findByUuid("uuid-inventory"))
                .hasValueSatisfying(found -> assertThat(found.getDescription()).isEqualTo("Updated by uuid"));
        assertThat(itemInventoryDao.findAll()).hasSize(1);

        InventoryIndex inventoryIndex = InventoryIndex.builder()
                .boxId(1)
                .boxIndex(2)
                .itemNumber("3001-1")
                .boxName("Main")
                .boxNumber("A")
                .sealed("N")
                .quantity(1)
                .description("Inventory index")
                .active(true)
                .build();
        inventoryIndexDao.insert(inventoryIndex);
        inventoryIndex.setQuantity(2);
        inventoryIndexDao.update(inventoryIndex);
        assertThat(inventoryIndexDao.findByItemNumber("3001-1")).isPresent();
        assertThat(inventoryIndexDao.findByBoxIdAndBoxIndex(1, 2)).isPresent();
        assertThat(inventoryIndexDao.findByBoxIdAndBoxIndexAndItemNumber(1, 2, "3001-1")).isPresent();
        assertThat(inventoryIndexDao.getAllForBox(1)).hasSize(1);
        assertThat(inventoryIndexDao.getAllWithNoItem()).hasSize(1);
        assertThat(inventoryIndexDao.findAll()).hasSize(1);

        ItemInventoryPhoto uploaded = itemInventoryPhotoDao.insertPhoto(
                itemInventory.getItemInventoryId(),
                "md5-uploaded",
                "uploaded.jpg",
                "bucket",
                null,
                100L,
                false,
                PhotoStatus.UPLOADED
        );
        assertThat(itemInventoryPhotoDao.findAll()).hasSize(1);
        assertThat(itemInventoryPhotoDao.findByItemInventoryPhotoId(uploaded.getItemInventoryPhotoId())).isPresent();
        assertThat(itemInventoryPhotoDao.findByUuid("md5-uploaded")).isPresent();
        assertThat(itemInventoryPhotoDao.markUploaded("md5-uploaded", "bucket", "key", 200L)).isOne();
        assertThat(itemInventoryPhotoDao.findWithoutS3Key()).isEmpty();
        assertThat(itemInventoryPhotoDao.countByStatus(PhotoStatus.UPLOADED)).isEqualTo(1);
        itemInventoryPhotoDao.transitionStatus("md5-uploaded", PhotoStatus.UPLOADED, PhotoStatus.FAILED);
        assertThat(itemInventoryPhotoDao.findFailed()).hasSize(1);
        assertThat(itemInventoryPhotoDao.resetToUploaded("md5-uploaded")).isZero();
        itemInventoryPhotoDao.transitionStatus("md5-uploaded", PhotoStatus.FAILED, PhotoStatus.PROCESSED);
        itemInventoryPhotoDao.setPrimaryForItem(itemInventory.getItemInventoryId(), "md5-uploaded");
        assertThat(itemInventoryPhotoDao.findPrimaryByItemInventoryId(itemInventory.getItemInventoryId())).isPresent();

        seedExternalCatalog();
        ExternalItem externalItem = externalItem("bii-1", 789L, "Bricklink item", 100, 2);
        externalItemDao.insert(externalItem);
        externalItemInventoryDao.insert(ExternalItemInventory.builder()
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .fixedPrice(false)
                .build());
        BricklinkItemInventory bricklink = bricklinkItemInventory(externalItem.getExternalItemId(), itemInventory.getItemInventoryId());
        bricklinkItemInventoryDao.insert(bricklink);
        assertThat(bricklinkItemInventoryDao.findByBricklinkItemInventoryId(1)).isPresent();
        assertThat(bricklinkItemInventoryDao.findByExternalItemIdAndItemInventoryId(externalItem.getExternalItemId(), itemInventory.getItemInventoryId()))
                .isPresent();
        assertThat(bricklinkItemInventoryDao.findByUuid("uuid-inventory")).isPresent();
        BricklinkItemInventory updated = BricklinkItemInventory.builder()
                .bricklinkItemInventoryId(1)
                .externalItemId(externalItem.getExternalItemId())
                .itemInventoryId(itemInventory.getItemInventoryId())
                .inventoryId(2L)
                .itemType("SET")
                .colorId(5)
                .colorName("Red")
                .quantity(2)
                .unitPrice(10.50)
                .isRetain(true)
                .isStockRoom(false)
                .dateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .build();
        bricklinkItemInventoryDao.update(updated);
        assertThat(bricklinkItemInventoryDao.findByBricklinkItemInventoryId(1))
                .hasValueSatisfying(found -> assertThat(found.getQuantity()).isEqualTo(2));
    }

    @Test
    void transactionDaosHandleTransactionsItemsAndCostOrchestration() {
        Party fromParty = party("From");
        partyDao.insert(fromParty);
        Party toParty = party("To");
        toParty.setPartyId(10L);
        partyDao.migrate(toParty);
        toParty.setPartyCity("Updated City");
        partyDao.update(toParty);
        assertThat(partyDao.findPartyById(10L))
                .hasValueSatisfying(found -> assertThat(found.getPartyCity()).isEqualTo("Updated City"));
        partyDao.decrementPartyId(10L);
        assertThat(partyDao.findPartyById(9L)).isPresent();
        assertThat(partyDao.findAll()).hasSize(2);

        transactionPlatformDao.insert(TransactionPlatform.builder()
                .transactionPlatformId(1)
                .transactionPlatformName("BrickLink")
                .build());
        Transactions transaction = Transactions.builder()
                .transactionDateTime(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .notes("Transaction")
                .fromPartyId(fromParty.getPartyId())
                .toPartyId(9L)
                .transactionPlatformId(1)
                .transactionOrderId("ORDER-1")
                .build();
        transactionsDao.insert(transaction);
        transaction.setNotes("Updated transaction");
        transactionsDao.update(transaction);
        assertThat(transactionsDao.findById(transaction.getTransactionId()))
                .hasValueSatisfying(found -> assertThat(found.getNotes()).isEqualTo("Updated transaction"));
        assertThat(transactionsDao.findAll()).hasSize(1);

        transactionTypeDao.insert(TransactionType.builder()
                .transactionTypeCode("BUY")
                .transactionTypeDescription("Buy")
                .conversionFactor(1)
                .build());
        ItemInventory itemInventory = itemInventory("uuid-transaction");
        itemInventoryDao.insert(itemInventory);
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(itemInventory.getItemInventoryId())
                .notes("Item")
                .build();
        transactionItemDao.insert(transactionItem);
        TransactionItem migratedItem = TransactionItem.builder()
                .transactionItemId(10L)
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(itemInventory.getItemInventoryId())
                .notes("Migrated item")
                .build();
        transactionItemDao.migrate(migratedItem);
        transactionItem.setNotes("Updated item");
        transactionItemDao.update(transactionItem);
        assertThat(transactionItemDao.findById(transactionItem.getTransactionItemId()))
                .hasValueSatisfying(found -> assertThat(found.getNotes()).isEqualTo("Updated item"));
        assertThat(transactionItemDao.findAll()).hasSize(2);

        costTypeDao.insert(CostType.builder()
                .costTypeCode("SHIP")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping")
                .build());
        TransactionCost firstCost = TransactionCost.builder()
                .costTypeCode("SHIP")
                .currencyCode(CurrencyCode.USD)
                .amount(1.25)
                .notes("First")
                .build();
        TransactionCost secondCost = TransactionCost.builder()
                .costTypeCode("SHIP")
                .currencyCode(CurrencyCode.USD)
                .amount(2.50)
                .notes("Second")
                .build();
        transactionCostDao.setTransactionCosts(transaction.getTransactionId(), List.of(firstCost, secondCost));
        assertThat(transactionCostDao.findAll()).hasSize(2);
        assertThat(transactionCostDao.findByTransactionIdAndCostTypeCode(transaction.getTransactionId(), "SHIP"))
                .hasValueSatisfying(found -> assertThat(found.getCostTypeCode()).isEqualTo("SHIP"));
        TransactionCost foundCost = transactionCostDao.findAll().get(0);
        foundCost.setAmount(3.75);
        transactionCostDao.update(foundCost);
        assertThat(transactionCostDao.findById(foundCost.getTransactionCostId()))
                .hasValueSatisfying(found -> assertThat(found.getAmount()).isEqualTo(3.75));
        transactionCostDao.delete(foundCost.getTransactionCostId());
        assertThat(transactionCostDao.findById(foundCost.getTransactionCostId())).isEmpty();
        transactionCostDao.migrate(TransactionCost.builder()
                .transactionCostId(10L)
                .transactionId(transaction.getTransactionId())
                .costTypeCode("SHIP")
                .currencyCode(CurrencyCode.USD)
                .amount(4.25)
                .notes("Migrated")
                .build());
        transactionCostDao.deleteTransactionCosts(transaction.getTransactionId());
        assertThat(transactionCostDao.findAll()).isEmpty();

        TransactionItemCost itemCost = TransactionItemCost.builder()
                .costTypeCode("SHIP")
                .currencyCode("USD")
                .amount(1.25)
                .notes("Item cost")
                .build();
        transactionCostDao.setTransactionItemCosts(transactionItem.getTransactionItemId(), List.of(itemCost));
        assertThat(transactionItemCostMapper.findAll()).hasSize(1);
        transactionCostDao.deleteTransactionItemCosts(transactionItem.getTransactionItemId());
        assertThat(transactionItemCostMapper.findAll()).isEmpty();
    }

    private void seedExternalCatalog() {
        externalServiceTypeDao.insert(ExternalServiceType.builder()
                .externalServiceTypeId(1)
                .externalServiceTypeName("Marketplace")
                .externalServiceTypeDescription("Marketplace APIs")
                .build());
        externalServiceDao.insert(externalService(2, "BRICKLINK"));
        categoryDao.insert(category(2, 100, "Sets", null));
    }

    private ExternalService externalService(Integer id, String name) {
        ExternalService service = new ExternalService();
        service.setExternalServiceId(id);
        service.setExternalServiceName(name);
        service.setExternalServiceUrl("https://" + name.toLowerCase() + ".example");
        service.setExternalServiceTypeId(1);
        return service;
    }

    private Category category(Integer externalServiceId, Integer externalCategoryId, String name, Integer parentId) {
        Category category = new Category();
        category.setExternalServiceId(externalServiceId);
        category.setExternalCategoryId(externalCategoryId);
        category.setCategoryName(name);
        category.setParentId(parentId);
        return category;
    }

    private ExternalItem externalItem(String number, Long uniqueId, String name, Integer categoryId, Integer serviceId) {
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

    private ItemInventory itemInventory(String uuid) {
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

    private BricklinkItemInventory bricklinkItemInventory(Integer externalItemId, Integer itemInventoryId) {
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
                .description("Description")
                .isRetain(false)
                .isStockRoom(false)
                .dateCreated(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    private Party party(String name) {
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

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
