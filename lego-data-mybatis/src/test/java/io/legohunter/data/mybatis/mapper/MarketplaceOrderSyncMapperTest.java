package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrder;
import io.legohunter.data.dto.MarketplaceOrderItem;
import io.legohunter.data.dto.MarketplaceOrderPayload;
import io.legohunter.data.dto.MarketplaceOrderSyncRun;
import io.legohunter.data.dto.MarketplaceOrderTransactionLink;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.Transactions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class MarketplaceOrderSyncMapperTest extends MapperTestSupport {
    private static final ZonedDateTime STARTED_AT = ZonedDateTime.parse("2026-06-01T12:00:00Z");
    private static final ZonedDateTime ORDERED_AT = ZonedDateTime.parse("2026-06-01T13:00:00Z");

    @Autowired MarketplaceOrderSyncRunMapper marketplaceOrderSyncRunMapper;
    @Autowired MarketplaceOrderMapper marketplaceOrderMapper;
    @Autowired MarketplaceOrderItemMapper marketplaceOrderItemMapper;
    @Autowired MarketplaceOrderPayloadMapper marketplaceOrderPayloadMapper;
    @Autowired MarketplaceOrderTransactionLinkMapper marketplaceOrderTransactionLinkMapper;

    @Test
    void marketplaceOrderSyncRunSupportsCrudAndUpsert() {
        MarketplaceOrderSyncRun syncRun = marketplaceOrderSyncRun();

        marketplaceOrderSyncRunMapper.insert(syncRun);
        syncRun.setSyncStatusCode("SUCCESS");
        syncRun.setCompletedAt(STARTED_AT.plusMinutes(5));
        assertThat(marketplaceOrderSyncRunMapper.update(syncRun)).isOne();

        assertThat(marketplaceOrderSyncRunMapper.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId()))
                .hasValueSatisfying(found -> assertThat(found.getSyncStatusCode()).isEqualTo("SUCCESS"));
        assertThat(marketplaceOrderSyncRunMapper.findAll()).hasSize(1);

        syncRun.setOrdersFetched(7);
        marketplaceOrderSyncRunMapper.upsert(syncRun);
        assertThat(marketplaceOrderSyncRunMapper.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId()))
                .hasValueSatisfying(found -> assertThat(found.getOrdersFetched()).isEqualTo(7));

        assertThat(marketplaceOrderSyncRunMapper.delete(syncRun.getMarketplaceOrderSyncRunId())).isOne();
        assertThat(marketplaceOrderSyncRunMapper.findByMarketplaceOrderSyncRunId(syncRun.getMarketplaceOrderSyncRunId())).isEmpty();
    }

    @Test
    void marketplaceOrderSupportsCrudUniqueLookupAndUpsert() {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        MarketplaceOrder order = marketplaceOrder(syncRun.getMarketplaceOrderSyncRunId(), "BL-1001");

        marketplaceOrderMapper.insert(order);
        order.setExternalStatusCode("PAID");
        order.setGrandTotalAmount(new BigDecimal("19.99"));
        assertThat(marketplaceOrderMapper.update(order)).isOne();

        assertThat(marketplaceOrderMapper.findByMarketplaceOrderId(order.getMarketplaceOrderId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalStatusCode()).isEqualTo("PAID"));
        assertThat(marketplaceOrderMapper.findByMarketplaceCodeAndExternalOrderId("BRICKLINK", "BL-1001")).isPresent();
        assertThat(marketplaceOrderMapper.findAll()).hasSize(1);

        MarketplaceOrder naturalKeyOrder = marketplaceOrder(syncRun.getMarketplaceOrderSyncRunId(), "BL-1001");
        naturalKeyOrder.setExternalStatusCode("UPDATED");
        marketplaceOrderMapper.upsert(naturalKeyOrder);
        assertThat(marketplaceOrderMapper.findByMarketplaceCodeAndExternalOrderId("BRICKLINK", "BL-1001"))
                .hasValueSatisfying(found -> assertThat(found.getExternalStatusCode()).isEqualTo("UPDATED"));

        assertThat(marketplaceOrderMapper.delete(order.getMarketplaceOrderId())).isOne();
        assertThat(marketplaceOrderMapper.findByMarketplaceOrderId(order.getMarketplaceOrderId())).isEmpty();
    }

    @Test
    void marketplaceOrderItemSupportsCrudAndUpsert() {
        MarketplaceOrder order = insertedOrder("BL-ITEM-1");
        MarketplaceOrderItem item = marketplaceOrderItem(order.getMarketplaceOrderId());

        marketplaceOrderItemMapper.insert(item);
        item.setQuantity(3);
        item.setRemarks("Updated remarks");
        item.setDescription("Updated description");
        assertThat(marketplaceOrderItemMapper.update(item)).isOne();

        assertThat(marketplaceOrderItemMapper.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getQuantity()).isEqualTo(3);
                    assertThat(found.getRemarks()).isEqualTo("Updated remarks");
                    assertThat(found.getDescription()).isEqualTo("Updated description");
                });
        assertThat(marketplaceOrderItemMapper.findAll()).hasSize(1);

        item.setQuantity(5);
        marketplaceOrderItemMapper.upsert(item);
        assertThat(marketplaceOrderItemMapper.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId()))
                .hasValueSatisfying(found -> assertThat(found.getQuantity()).isEqualTo(5));

        assertThat(marketplaceOrderItemMapper.delete(item.getMarketplaceOrderItemId())).isOne();
        assertThat(marketplaceOrderItemMapper.findByMarketplaceOrderItemId(item.getMarketplaceOrderItemId())).isEmpty();
    }

    @Test
    void marketplaceOrderPayloadSupportsCrudAndUpsert() {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        MarketplaceOrder order = insertedOrder(syncRun.getMarketplaceOrderSyncRunId(), "BL-PAYLOAD-1");
        MarketplaceOrderPayload payload = marketplaceOrderPayload(order.getMarketplaceOrderId(), syncRun.getMarketplaceOrderSyncRunId());

        marketplaceOrderPayloadMapper.insert(payload);
        payload.setPayloadHash("hash-updated");
        payload.setPayloadJson("{\"status\":\"updated\"}");
        assertThat(marketplaceOrderPayloadMapper.update(payload)).isOne();

        assertThat(marketplaceOrderPayloadMapper.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId()))
                .hasValueSatisfying(found -> assertThat(found.getPayloadHash()).isEqualTo("hash-updated"));
        assertThat(marketplaceOrderPayloadMapper.findAll()).hasSize(1);

        payload.setPayloadTypeCode("ORDER_ITEMS_RESPONSE");
        marketplaceOrderPayloadMapper.upsert(payload);
        assertThat(marketplaceOrderPayloadMapper.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId()))
                .hasValueSatisfying(found -> assertThat(found.getPayloadTypeCode()).isEqualTo("ORDER_ITEMS_RESPONSE"));

        assertThat(marketplaceOrderPayloadMapper.delete(payload.getMarketplaceOrderPayloadId())).isOne();
        assertThat(marketplaceOrderPayloadMapper.findByMarketplaceOrderPayloadId(payload.getMarketplaceOrderPayloadId())).isEmpty();
    }

    @Test
    void marketplaceOrderTransactionLinkSupportsCrudAndUpsert() {
        MarketplaceOrder order = insertedOrder("BL-LINK-1");
        MarketplaceOrderItem orderItem = insertedOrderItem(order.getMarketplaceOrderId());
        TransactionItem transactionItem = insertedTransactionItem();
        MarketplaceOrderTransactionLink link = marketplaceOrderTransactionLink(
                order.getMarketplaceOrderId(),
                transactionItem.getTransactionId(),
                orderItem.getMarketplaceOrderItemId(),
                transactionItem.getTransactionItemId()
        );

        marketplaceOrderTransactionLinkMapper.insert(link);
        link.setLinkStatusCode("UNLINKED");
        link.setUnlinkedAt(STARTED_AT.plusDays(1));
        assertThat(marketplaceOrderTransactionLinkMapper.update(link)).isOne();

        assertThat(marketplaceOrderTransactionLinkMapper.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId()))
                .hasValueSatisfying(found -> assertThat(found.getLinkStatusCode()).isEqualTo("UNLINKED"));
        assertThat(marketplaceOrderTransactionLinkMapper.findAll()).hasSize(1);

        link.setLinkStatusCode("ACTIVE");
        link.setUnlinkedAt(null);
        marketplaceOrderTransactionLinkMapper.upsert(link);
        assertThat(marketplaceOrderTransactionLinkMapper.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId()))
                .hasValueSatisfying(found -> assertThat(found.getLinkStatusCode()).isEqualTo("ACTIVE"));

        assertThat(marketplaceOrderTransactionLinkMapper.delete(link.getMarketplaceOrderTransactionLinkId())).isOne();
        assertThat(marketplaceOrderTransactionLinkMapper.findByMarketplaceOrderTransactionLinkId(link.getMarketplaceOrderTransactionLinkId())).isEmpty();
    }

    private MarketplaceOrderSyncRun insertedSyncRun() {
        MarketplaceOrderSyncRun syncRun = marketplaceOrderSyncRun();
        marketplaceOrderSyncRunMapper.insert(syncRun);
        return syncRun;
    }

    private MarketplaceOrder insertedOrder(String externalOrderId) {
        MarketplaceOrderSyncRun syncRun = insertedSyncRun();
        return insertedOrder(syncRun.getMarketplaceOrderSyncRunId(), externalOrderId);
    }

    private MarketplaceOrder insertedOrder(Integer syncRunId, String externalOrderId) {
        MarketplaceOrder order = marketplaceOrder(syncRunId, externalOrderId);
        marketplaceOrderMapper.insert(order);
        return order;
    }

    private MarketplaceOrderItem insertedOrderItem(Integer marketplaceOrderId) {
        MarketplaceOrderItem orderItem = marketplaceOrderItem(marketplaceOrderId);
        marketplaceOrderItemMapper.insert(orderItem);
        return orderItem;
    }

    private TransactionItem insertedTransactionItem() {
        Transactions transaction = insertTransaction();
        insertTransactionType();
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .notes("Transaction item")
                .build();
        transactionItemMapper.insert(transactionItem);
        return transactionItem;
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
                .buyerDisplayName("Buyer One")
                .buyerEmail("buyer@example.com")
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
                .payloadHash("order-hash")
                .lastSeenAt(ORDERED_AT.plusHours(3))
                .build();
    }

    private MarketplaceOrderItem marketplaceOrderItem(Integer marketplaceOrderId) {
        return MarketplaceOrderItem.builder()
                .marketplaceOrderId(marketplaceOrderId)
                .externalOrderItemId("line-1")
                .externalInventoryId("inventory-1")
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
                .remarks("Initial remarks")
                .description("Initial description")
                .payloadHash("item-hash")
                .build();
    }

    private MarketplaceOrderPayload marketplaceOrderPayload(Integer marketplaceOrderId, Integer syncRunId) {
        return MarketplaceOrderPayload.builder()
                .marketplaceOrderId(marketplaceOrderId)
                .marketplaceOrderSyncRunId(syncRunId)
                .payloadTypeCode("ORDER_RESPONSE")
                .payloadHash("hash-initial")
                .payloadJson("{\"status\":\"initial\"}")
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
}
