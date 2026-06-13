package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.MarketplaceOrder;
import io.legohunter.data.dto.MarketplaceOrderItem;
import io.legohunter.data.dto.MarketplaceOrderPayload;
import io.legohunter.data.dto.MarketplaceOrderSyncRun;
import io.legohunter.data.dto.MarketplaceOrderTransactionLink;
import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.dto.Transactions;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = "/scripts/db/h2/current_schema.ddl")
class MarketplaceOrderSyncDaoTest {
    private static final ZonedDateTime STARTED_AT = ZonedDateTime.parse("2026-06-01T12:00:00Z");
    private static final ZonedDateTime ORDERED_AT = ZonedDateTime.parse("2026-06-01T13:00:00Z");

    @Autowired MarketplaceOrderSyncRunDao marketplaceOrderSyncRunDao;
    @Autowired MarketplaceOrderDao marketplaceOrderDao;
    @Autowired MarketplaceOrderItemDao marketplaceOrderItemDao;
    @Autowired MarketplaceOrderPayloadDao marketplaceOrderPayloadDao;
    @Autowired MarketplaceOrderTransactionLinkDao marketplaceOrderTransactionLinkDao;
    @Autowired PartyDao partyDao;
    @Autowired TransactionPlatformDao transactionPlatformDao;
    @Autowired TransactionsDao transactionsDao;
    @Autowired TransactionTypeDao transactionTypeDao;
    @Autowired TransactionItemDao transactionItemDao;

    @Test
    void marketplaceOrderSyncRunDaoExposesMapperMethods() {
        MarketplaceOrderSyncRun syncRun = marketplaceOrderSyncRunDao.insert(marketplaceOrderSyncRun());
        syncRun.setSyncStatusCode("SUCCESS");
        syncRun.setCompletedAt(STARTED_AT.plusMinutes(5));
        assertThat(marketplaceOrderSyncRunDao.update(syncRun)).isOne();

        assertThat(marketplaceOrderSyncRunDao.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId()))
                .hasValueSatisfying(found -> assertThat(found.getSyncStatusCode()).isEqualTo("SUCCESS"));
        assertThat(marketplaceOrderSyncRunDao.findAll()).hasSize(1);

        syncRun.setOrdersFetched(4);
        marketplaceOrderSyncRunDao.upsert(syncRun);
        assertThat(marketplaceOrderSyncRunDao.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId()))
                .hasValueSatisfying(found -> assertThat(found.getOrdersFetched()).isEqualTo(4));

        assertThat(marketplaceOrderSyncRunDao.delete(syncRun.getMarketplaceOrderSyncRunId())).isOne();
        assertThat(marketplaceOrderSyncRunDao.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId())).isEmpty();
    }

    @Test
    void marketplaceOrderDaoExposesMapperMethods() {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        MarketplaceOrder order = marketplaceOrderDao.insert(marketplaceOrder(syncRun.getMarketplaceOrderSyncRunId(), "DAO-BL-1001"));
        order.setExternalStatusCode("PAID");
        order.setGrandTotalAmount(new BigDecimal("29.99"));
        assertThat(marketplaceOrderDao.update(order)).isOne();

        assertThat(marketplaceOrderDao.findByMarketplaceOrderId(order.getMarketplaceOrderId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalStatusCode()).isEqualTo("PAID"));
        assertThat(marketplaceOrderDao.findByMarketplaceCodeAndExternalOrderId("BRICKLINK", "DAO-BL-1001")).isPresent();
        assertThat(marketplaceOrderDao.findAll()).hasSize(1);

        MarketplaceOrder naturalKeyOrder = marketplaceOrder(syncRun.getMarketplaceOrderSyncRunId(), "DAO-BL-1001");
        naturalKeyOrder.setExternalStatusCode("UPDATED");
        marketplaceOrderDao.upsert(naturalKeyOrder);
        assertThat(marketplaceOrderDao.findByMarketplaceCodeAndExternalOrderId("BRICKLINK", "DAO-BL-1001"))
                .hasValueSatisfying(found -> assertThat(found.getExternalStatusCode()).isEqualTo("UPDATED"));

        assertThat(marketplaceOrderDao.delete(order.getMarketplaceOrderId())).isOne();
        assertThat(marketplaceOrderDao.findByMarketplaceOrderId(order.getMarketplaceOrderId())).isEmpty();
    }

    @Test
    void marketplaceOrderDaoFindsFulfillmentCandidatesAcrossStatuses() {
        MarketplaceOrder pending = insertedOrder("DAO-BL-CANDIDATE-1");
        marketplaceOrderItemDao.insert(marketplaceOrderItem(pending.getMarketplaceOrderId()));
        MarketplaceOrder ready = insertedOrder("DAO-BL-CANDIDATE-2");
        ready.setExternalStatusCode("READY");
        marketplaceOrderDao.update(ready);
        marketplaceOrderItemDao.insert(marketplaceOrderItem(ready.getMarketplaceOrderId()));
        MarketplaceOrder withoutItems = insertedOrder("DAO-BL-CANDIDATE-3");

        assertThat(marketplaceOrderDao.findFulfillmentCandidates("BRICKLINK", List.of("PENDING", "READY"), 10))
                .extracting(MarketplaceOrder::getMarketplaceOrderId)
                .containsExactly(pending.getMarketplaceOrderId(), ready.getMarketplaceOrderId());
        assertThat(withoutItems.getMarketplaceOrderId()).isNotNull();
    }

    @Test
    void marketplaceOrderItemDaoExposesMapperMethods() {
        MarketplaceOrder order = insertedOrder("DAO-BL-ITEM-1");
        MarketplaceOrderItem item = marketplaceOrderItemDao.insert(marketplaceOrderItem(order.getMarketplaceOrderId()));
        item.setQuantity(2);
        item.setRemarks("DAO updated remarks");
        item.setDescription("DAO updated description");
        assertThat(marketplaceOrderItemDao.update(item)).isOne();

        assertThat(marketplaceOrderItemDao.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getQuantity()).isEqualTo(2);
                    assertThat(found.getRemarks()).isEqualTo("DAO updated remarks");
                    assertThat(found.getDescription()).isEqualTo("DAO updated description");
                });
        assertThat(marketplaceOrderItemDao.findByMarketplaceOrderId(order.getMarketplaceOrderId()))
                .extracting(MarketplaceOrderItem::getMarketplaceOrderItemId)
                .containsExactly(item.getMarketplaceOrderItemId());
        assertThat(marketplaceOrderItemDao.findByMarketplaceOrderIdAndExternalInventoryId(
                order.getMarketplaceOrderId(),
                "dao-inventory-1"
        )).extracting(MarketplaceOrderItem::getMarketplaceOrderItemId)
                .containsExactly(item.getMarketplaceOrderItemId());
        assertThat(marketplaceOrderItemDao.findAll()).hasSize(1);

        item.setQuantity(6);
        marketplaceOrderItemDao.upsert(item);
        assertThat(marketplaceOrderItemDao.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId()))
                .hasValueSatisfying(found -> assertThat(found.getQuantity()).isEqualTo(6));

        assertThat(marketplaceOrderItemDao.deleteByMarketplaceOrderId(order.getMarketplaceOrderId())).isOne();
        assertThat(marketplaceOrderItemDao.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId())).isEmpty();
    }

    @Test
    void marketplaceOrderPayloadDaoExposesMapperMethods() {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        MarketplaceOrder order = insertedOrder(syncRun.getMarketplaceOrderSyncRunId(), "DAO-BL-PAYLOAD-1");
        MarketplaceOrderPayload payload = marketplaceOrderPayloadDao.insert(marketplaceOrderPayload(order.getMarketplaceOrderId(), syncRun.getMarketplaceOrderSyncRunId()));
        payload.setPayloadHash("dao-hash-updated");
        payload.setPayloadJson("{\"dao\":\"updated\"}");
        assertThat(marketplaceOrderPayloadDao.update(payload)).isOne();

        assertThat(marketplaceOrderPayloadDao.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId()))
                .hasValueSatisfying(found -> assertThat(found.getPayloadHash()).isEqualTo("dao-hash-updated"));
        assertThat(marketplaceOrderPayloadDao.findAll()).hasSize(1);

        payload.setPayloadTypeCode("ORDER_ITEMS_RESPONSE");
        marketplaceOrderPayloadDao.upsert(payload);
        assertThat(marketplaceOrderPayloadDao.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId()))
                .hasValueSatisfying(found -> assertThat(found.getPayloadTypeCode()).isEqualTo("ORDER_ITEMS_RESPONSE"));

        assertThat(marketplaceOrderPayloadDao.delete(payload.getMarketplaceOrderPayloadId())).isOne();
        assertThat(marketplaceOrderPayloadDao.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId())).isEmpty();
    }

    @Test
    void marketplaceOrderPayloadDaoFindsLatestByOrderAndType() {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        MarketplaceOrder order = insertedOrder(syncRun.getMarketplaceOrderSyncRunId(), "DAO-BL-PAYLOAD-LATEST-1");
        MarketplaceOrderPayload older = marketplaceOrderPayload(order.getMarketplaceOrderId(), syncRun.getMarketplaceOrderSyncRunId());
        older.setPayloadJson("{\"dao\":\"older\"}");
        marketplaceOrderPayloadDao.insert(older);
        MarketplaceOrderPayload latest = marketplaceOrderPayload(order.getMarketplaceOrderId(), syncRun.getMarketplaceOrderSyncRunId());
        latest.setPayloadJson("{\"dao\":\"latest\"}");
        latest.setCapturedAt(STARTED_AT.plusMinutes(2));
        marketplaceOrderPayloadDao.insert(latest);

        assertThat(marketplaceOrderPayloadDao.findLatestByMarketplaceOrderIdAndPayloadTypeCode(
                order.getMarketplaceOrderId(),
                "ORDER_RESPONSE"
        )).hasValueSatisfying(found -> assertThat(found.getPayloadJson()).isEqualTo("{\"dao\":\"latest\"}"));
    }

    @Test
    void marketplaceOrderTransactionLinkDaoExposesMapperMethods() {
        MarketplaceOrder order = insertedOrder("DAO-BL-LINK-1");
        MarketplaceOrderItem orderItem = marketplaceOrderItemDao.insert(marketplaceOrderItem(order.getMarketplaceOrderId()));
        TransactionItem transactionItem = insertedTransactionItem();
        MarketplaceOrderTransactionLink link = marketplaceOrderTransactionLinkDao.insert(marketplaceOrderTransactionLink(
                order.getMarketplaceOrderId(),
                transactionItem.getTransactionId(),
                orderItem.getMarketplaceOrderItemId(),
                transactionItem.getTransactionItemId()
        ));
        link.setLinkStatusCode("UNLINKED");
        link.setUnlinkedAt(STARTED_AT.plusDays(1));
        assertThat(marketplaceOrderTransactionLinkDao.update(link)).isOne();

        assertThat(marketplaceOrderTransactionLinkDao.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId()))
                .hasValueSatisfying(found -> assertThat(found.getLinkStatusCode()).isEqualTo("UNLINKED"));
        assertThat(marketplaceOrderTransactionLinkDao.findAll()).hasSize(1);

        link.setLinkStatusCode("ACTIVE");
        link.setUnlinkedAt(null);
        marketplaceOrderTransactionLinkDao.upsert(link);
        assertThat(marketplaceOrderTransactionLinkDao.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId()))
                .hasValueSatisfying(found -> assertThat(found.getLinkStatusCode()).isEqualTo("ACTIVE"));

        assertThat(marketplaceOrderTransactionLinkDao.delete(link.getMarketplaceOrderTransactionLinkId())).isOne();
        assertThat(marketplaceOrderTransactionLinkDao.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId())).isEmpty();
    }

    private MarketplaceOrderSyncRun insertedSyncRun() {
        return marketplaceOrderSyncRunDao.insert(marketplaceOrderSyncRun());
    }

    private MarketplaceOrder insertedOrder(String externalOrderId) {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        return insertedOrder(syncRun.getMarketplaceOrderSyncRunId(), externalOrderId);
    }

    private MarketplaceOrder insertedOrder(Integer syncRunId, String externalOrderId) {
        return marketplaceOrderDao.insert(marketplaceOrder(syncRunId, externalOrderId));
    }

    private TransactionItem insertedTransactionItem() {
        Party fromParty = party("From");
        partyDao.insert(fromParty);
        Party toParty = party("To");
        partyDao.insert(toParty);
        transactionPlatformDao.insert(TransactionPlatform.builder()
                .transactionPlatformId(1)
                .transactionPlatformName("BrickLink")
                .build());
        transactionTypeDao.insert(TransactionType.builder()
                .transactionTypeCode("BUY")
                .transactionTypeDescription("Buy")
                .conversionFactor(1)
                .build());
        Transactions transaction = Transactions.builder()
                .transactionDateTime(STARTED_AT)
                .notes("DAO transaction")
                .fromPartyId(fromParty.getPartyId())
                .toPartyId(toParty.getPartyId())
                .transactionPlatformId(1)
                .transactionOrderId("DAO-ORDER-1")
                .build();
        transactionsDao.insert(transaction);
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .notes("DAO transaction item")
                .build();
        transactionItemDao.insert(transactionItem);
        return transactionItem;
    }

    private Party party(String name) {
        return Party.builder()
                .partyFirstName(name)
                .partyLastName("Tester")
                .partyAddress1("123 Main")
                .partyCity("Charlotte")
                .partyState("NC")
                .partyPostalCode("28202")
                .partyCountryCode("US")
                .partyCountry("United States")
                .partyEmail(name.toLowerCase() + "@example.com")
                .partyType("PERSON")
                .partyActivationDate(LocalDateTime.parse("2026-01-01T00:00:00"))
                .build();
    }

    private MarketplaceOrderSyncRun marketplaceOrderSyncRun() {
        return MarketplaceOrderSyncRun.builder()
                .marketplaceCode("BRICKLINK")
                .syncJobName("bricklink-order-sync")
                .syncDirection("IN")
                .syncStatusCode("STARTED")
                .startedAt(STARTED_AT)
                .ordersDiscovered(1)
                .ordersFetched(0)
                .ordersFailed(0)
                .build();
    }

    private MarketplaceOrder marketplaceOrder(Integer syncRunId, String externalOrderId) {
        return MarketplaceOrder.builder()
                .lastSyncRunId(syncRunId)
                .marketplaceCode("BRICKLINK")
                .externalOrderId(externalOrderId)
                .orderDirection("IN")
                .externalStatusCode("PENDING")
                .orderedAt(ORDERED_AT)
                .statusChangedAt(ORDERED_AT.plusHours(1))
                .buyerDisplayName("DAO Buyer")
                .buyerEmail("dao-buyer@example.com")
                .paymentStatusCode("PENDING")
                .paymentMethod("PayPal")
                .paymentCurrencyCode("USD")
                .paidAt(ORDERED_AT.plusHours(2))
                .shippingMethod("Request for invoice")
                .shippingMethodId("1")
                .shippingAddressPresent(true)
                .trackingPresent(false)
                .subtotalAmount(new BigDecimal("12.34"))
                .shippingAmount(new BigDecimal("6.42"))
                .grandTotalAmount(new BigDecimal("18.76"))
                .currencyCode("USD")
                .displayCurrencyCode("USD")
                .totalCount(2)
                .uniqueCount(1)
                .totalWeight(new BigDecimal("1.250"))
                .invoiced(false)
                .filed(false)
                .sentDriveThru(false)
                .requireInsurance(false)
                .payloadHash("dao-order-hash")
                .lastSeenAt(ORDERED_AT.plusHours(3))
                .build();
    }

    private MarketplaceOrderItem marketplaceOrderItem(Integer marketplaceOrderId) {
        return MarketplaceOrderItem.builder()
                .marketplaceOrderId(marketplaceOrderId)
                .externalOrderItemId("dao-line-1")
                .externalInventoryId("dao-inventory-1")
                .externalItemNo("3001")
                .externalItemType("SET")
                .colorId(5)
                .colorName("Red")
                .quantity(1)
                .conditionCode("N")
                .completenessCode("C")
                .unitPrice(new BigDecimal("5.99"))
                .finalUnitPrice(new BigDecimal("5.49"))
                .currencyCode("USD")
                .itemWeight(new BigDecimal("0.250"))
                .remarks("DAO initial remarks")
                .description("DAO initial description")
                .payloadHash("dao-item-hash")
                .build();
    }

    private MarketplaceOrderPayload marketplaceOrderPayload(Integer marketplaceOrderId, Integer syncRunId) {
        return MarketplaceOrderPayload.builder()
                .marketplaceOrderId(marketplaceOrderId)
                .marketplaceOrderSyncRunId(syncRunId)
                .payloadTypeCode("ORDER_RESPONSE")
                .payloadHash("dao-hash-initial")
                .payloadJson("{\"dao\":\"initial\"}")
                .capturedAt(STARTED_AT.plusMinutes(1))
                .build();
    }

    private MarketplaceOrderTransactionLink marketplaceOrderTransactionLink(
            Integer marketplaceOrderId,
            Long transactionId,
            Integer marketplaceOrderItemId,
            Long transactionItemId
    ) {
        return MarketplaceOrderTransactionLink.builder()
                .marketplaceOrderId(marketplaceOrderId)
                .transactionId(transactionId)
                .marketplaceOrderItemId(marketplaceOrderItemId)
                .transactionItemId(transactionItemId)
                .linkTypeCode("FULFILLMENT")
                .linkStatusCode("ACTIVE")
                .linkedAt(STARTED_AT.plusHours(1))
                .build();
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
