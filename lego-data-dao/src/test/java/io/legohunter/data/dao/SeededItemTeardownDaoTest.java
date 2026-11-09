package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.ItemInventorySeedSource;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.MarketplaceListingSyncRequest;
import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.Payment;
import io.legohunter.data.dto.PricingApplyReadiness;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.dto.PricingSnapshotListing;
import io.legohunter.data.dto.SeededItemTeardownCheck;
import io.legohunter.data.dto.SeededItemTeardownResult;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.TransactionItemCost;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.mybatis.mapper.TransactionItemCostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static io.legohunter.data.dto.ItemInventorySeedSource.SOURCE_SYSTEM_LEGO_PROD_LEGACY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HOMELAB-40 teardown on H2: removes a seeded item's whole local graph, never touches an unseeded
 * item, keeps a shared acquisition until its last item is gone, and refuses blocked items.
 */
@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/marketplace_pricing_schema.ddl",
        "/scripts/db/h2/current_lookup_data.sql"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SeededItemTeardownDaoTest {
    private static final ZonedDateTime AT = ZonedDateTime.parse("2026-11-05T12:00:00Z");
    private static final String RUN = "f80effb8-078e-466c-bf05-193d762fb49b";

    @Autowired SeededItemTeardownDao seededItemTeardownDao;
    @Autowired ConditionDao conditionDao;
    @Autowired ExternalServiceDao externalServiceDao;
    @Autowired ExternalCatalogItemDao externalCatalogItemDao;
    @Autowired ItemInventoryDao itemInventoryDao;
    @Autowired ItemInventoryExternalCatalogItemDao itemInventoryExternalCatalogItemDao;
    @Autowired ItemInventorySeedSourceDao itemInventorySeedSourceDao;
    @Autowired MarketplaceListingDao marketplaceListingDao;
    @Autowired BricklinkMarketplaceListingDao bricklinkMarketplaceListingDao;
    @Autowired PricingCrawlWorkItemDao pricingCrawlWorkItemDao;
    @Autowired PricingSnapshotDao pricingSnapshotDao;
    @Autowired PricingSnapshotListingDao pricingSnapshotListingDao;
    @Autowired PricingDecisionDao pricingDecisionDao;
    @Autowired PricingApplyReadinessDao pricingApplyReadinessDao;
    @Autowired MarketplaceListingSyncRequestDao marketplaceListingSyncRequestDao;
    @Autowired PartyDao partyDao;
    @Autowired TransactionsDao transactionsDao;
    @Autowired TransactionItemDao transactionItemDao;
    @Autowired TransactionItemCostMapper transactionItemCostMapper;
    @Autowired PaymentDao paymentDao;
    @Autowired JdbcTemplate jdbc;

    private ExternalService bricklink;
    private Transactions acquisition;

    @BeforeEach
    void setUp() {
        conditionDao.findConditionById(1).orElseGet(() -> {
            Condition condition = Condition.builder().conditionId(1).conditionCode("G").conditionDescription("Good").conditionText("Good").build();
            conditionDao.insert(condition);
            return condition;
        });
        bricklink = externalServiceDao.findByServiceCode("BRICKLINK").orElseThrow();
        Party seller = party("Seller");
        Party buyer = party("Buyer");
        acquisition = Transactions.builder()
                .transactionDate(LocalDate.parse("2026-11-05"))
                .notes("Lower-environment seed run " + RUN)
                .fromPartyId(seller.getPartyId())
                .toPartyId(buyer.getPartyId())
                .transactionPlatformId(1)
                .build();
        transactionsDao.insert(acquisition);
        paymentDao.insert(Payment.builder()
                .paymentDate(LocalDate.parse("2026-11-05"))
                .transactionId(acquisition.getTransactionId())
                .currencyCode("USD")
                .sellerCurrencyCode("USD")
                .exchangeRate(new BigDecimal("1.00000"))
                .amount(BigDecimal.ZERO)
                .paymentPlatformId(1)
                .paymentPlatformTransactionId("seed-" + RUN)
                .build());
    }

    @Test
    void removesTheWholeLocalGraphOfASeededItemAndKeepsTheSharedAcquisitionForItsOtherItem() {
        Fixture first = seededItemWithPricingHistory("seed-first", "7");
        Fixture second = seededItemWithPricingHistory("seed-second", "9");

        assertThat(seededItemTeardownDao.check(first.item().getItemInventoryId()).blockers()).isEmpty();
        SeededItemTeardownResult result = seededItemTeardownDao.teardown(first.item().getItemInventoryId());

        assertThat(result.getDeletedRows()).containsEntry("item_inventory", 1)
                .containsEntry("item_inventory_seed_source", 1)
                .containsEntry("marketplace_listing", 1)
                .containsEntry("bricklink_marketplace_listing", 1)
                .containsEntry("marketplace_listing_sync_request", 1)
                .containsEntry("pricing_apply_readiness", 1)
                .containsEntry("pricing_decision", 1)
                .containsEntry("pricing_snapshot_listing", 1)
                .containsEntry("pricing_snapshot", 1)
                .containsEntry("pricing_crawl_work_item", 1)
                .containsEntry("item_inventory_external_catalog_item", 1)
                .containsEntry("transaction_item_cost", 1)
                .containsEntry("transaction_item", 1)
                .doesNotContainKey("transactions");
        assertThat(result.getDeletedTransactionIds()).isEmpty();
        assertThat(itemInventoryDao.findByItemInventoryId(first.item().getItemInventoryId())).isEmpty();
        assertThat(marketplaceListingDao.findByMarketplaceListingId(first.listing().getMarketplaceListingId())).isEmpty();
        assertThat(rows("item_inventory_seed_source")).isOne();

        // The other seeded item is untouched, and the acquisition still holds it.
        assertThat(itemInventoryDao.findByItemInventoryId(second.item().getItemInventoryId())).isPresent();
        assertThat(rows("pricing_snapshot")).isOne();
        assertThat(rows("transactions")).isOne();
        assertThat(rows("payment")).isOne();

        // Its last item gone, the acquisition and its payment go too.
        SeededItemTeardownResult last = seededItemTeardownDao.teardown(second.item().getItemInventoryId());
        assertThat(last.getDeletedTransactionIds()).containsExactly(acquisition.getTransactionId());
        assertThat(last.getDeletedRows()).containsEntry("transactions", 1).containsEntry("payment", 1);
        for (String table : new String[]{"item_inventory", "item_inventory_seed_source", "marketplace_listing",
                "bricklink_marketplace_listing", "marketplace_listing_sync_request", "pricing_apply_readiness",
                "pricing_decision", "pricing_snapshot_listing", "pricing_snapshot", "pricing_crawl_work_item",
                "item_inventory_external_catalog_item", "transaction_item_cost", "transaction_item", "transactions", "payment"}) {
            assertThat(rows(table)).as(table).isZero();
        }
    }

    @Test
    void neverTouchesAnUnseededItemEvenWhenCalledWithItsId() {
        Fixture real = itemWithPricingHistory("real-item", null);

        SeededItemTeardownCheck check = seededItemTeardownDao.check(real.item().getItemInventoryId());
        assertThat(check.isSeededItem()).isFalse();
        assertThat(check.blockers()).contains("not a seeded item");
        assertThatThrownBy(() -> seededItemTeardownDao.teardown(real.item().getItemInventoryId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not a seeded item");

        assertThat(itemInventoryDao.findByItemInventoryId(real.item().getItemInventoryId())).isPresent();
        assertThat(rows("marketplace_listing")).isOne();
        assertThat(rows("pricing_snapshot")).isOne();
        assertThat(rows("transaction_item")).isOne();
        assertThat(rows("transactions")).isOne();
    }

    @Test
    void refusesASeededItemThatStillHasABricklinkLot() {
        Fixture seeded = seededItemWithPricingHistory("seed-with-lot", "28");
        BricklinkMarketplaceListing bricklinkListing = bricklinkMarketplaceListingDao
                .findByMarketplaceListingId(seeded.listing().getMarketplaceListingId()).orElseThrow();
        bricklinkListing.setBricklinkInventoryId(512345678);
        bricklinkMarketplaceListingDao.update(bricklinkListing);

        assertThat(seededItemTeardownDao.check(seeded.item().getItemInventoryId()).getBricklinkLots()).isOne();
        assertThatThrownBy(() -> seededItemTeardownDao.teardown(seeded.item().getItemInventoryId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("BrickLink lot");
        assertThat(itemInventoryDao.findByItemInventoryId(seeded.item().getItemInventoryId())).isPresent();
        assertThat(rows("pricing_snapshot")).isOne();
    }

    @Test
    void refusesASeededItemWithAnActiveSyncRequest() {
        Fixture seeded = seededItemWithPricingHistory("seed-claimed", "30");
        MarketplaceListingSyncRequest request = marketplaceListingSyncRequestDao
                .findByMarketplaceListingId(seeded.listing().getMarketplaceListingId()).iterator().next();
        request.setSyncRequestStatusCode("CLAIMED");
        marketplaceListingSyncRequestDao.update(request);

        assertThat(seededItemTeardownDao.check(seeded.item().getItemInventoryId()).getActiveSyncRequests()).isOne();
        assertThatThrownBy(() -> seededItemTeardownDao.teardown(seeded.item().getItemInventoryId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("active (PENDING or CLAIMED) sync request");
        assertThat(rows("marketplace_listing_sync_request")).isOne();
    }

    @Test
    void blockersListEveryReason() {
        SeededItemTeardownCheck check = SeededItemTeardownCheck.builder()
                .itemInventoryId(1).seeded(1).bricklinkLots(1).ebayItems(1).activeSyncRequests(2).photos(3)
                .imageAlbums(1).orderItems(1).orderTransactionLinks(1).revenues(1).shipments(1)
                .build();
        assertThat(check.blockers()).hasSize(9);
        assertThat(SeededItemTeardownCheck.builder().itemInventoryId(1).seeded(1).build().blockers()).isEmpty();
        assertThat(SeededItemTeardownCheck.builder().itemInventoryId(1).seeded(0).build().blockers())
                .containsExactly("not a seeded item");
    }

    private Fixture seededItemWithPricingHistory(String uuid, String sourceKey) {
        return itemWithPricingHistory(uuid, sourceKey);
    }

    /** An item with listing, BrickLink draft, full pricing chain, sync request, catalog link and acquisition line. */
    private Fixture itemWithPricingHistory(String uuid, String sourceKey) {
        ExternalCatalogItem catalogItem = externalCatalogItemDao.insert(ExternalCatalogItem.builder()
                .externalServiceId(bricklink.getExternalServiceId())
                .externalItemKey(uuid + "-1")
                .externalUniqueKey(uuid)
                .itemName("Set " + uuid)
                .itemTypeCode("SET")
                .itemUrl("https://bricklink.example/" + uuid)
                .yearReleased(1980)
                .build());
        ItemInventory item = new ItemInventory();
        item.setUuid(uuid);
        item.setBoxNumber(900);
        item.setPurchasePrice(BigDecimal.ZERO);
        item.setActive(true);
        item.setForSale(true);
        item.setNewOrUsed("USED");
        item.setCompleteness("COMPLETE");
        item.setItemConditionId(1);
        item.setBoxConditionId(1);
        item.setInstructionsConditionId(1);
        item.setSealed(false);
        item.setBuiltOnce(true);
        item = itemInventoryDao.insert(item);
        itemInventoryExternalCatalogItemDao.insert(ItemInventoryExternalCatalogItem.builder()
                .itemInventoryId(item.getItemInventoryId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .primary(true)
                .build());
        if (sourceKey != null) {
            itemInventorySeedSourceDao.insert(ItemInventorySeedSource.builder()
                    .itemInventoryId(item.getItemInventoryId())
                    .sourceSystem(SOURCE_SYSTEM_LEGO_PROD_LEGACY)
                    .sourceKey(sourceKey)
                    .seedRunId(RUN)
                    .build());
        }
        TransactionItem line = TransactionItem.builder()
                .transactionId(acquisition.getTransactionId())
                .transactionTypeCode("P")
                .itemInventoryId(item.getItemInventoryId())
                .notes("Seeded item")
                .build();
        transactionItemDao.insert(line);
        transactionItemCostMapper.insert(TransactionItemCost.builder()
                .transactionItemId(line.getTransactionItemId())
                .costTypeCode("PRICE")
                .currencyCode("USD")
                .amount(0.0)
                .notes("Seed cost")
                .build());

        MarketplaceListing listing = marketplaceListingDao.insert(MarketplaceListing.builder()
                .itemInventoryId(item.getItemInventoryId())
                .listingExternalServiceId(bricklink.getExternalServiceId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .listingStatusCode("DRAFT")
                .title("Set " + uuid)
                .currencyCode("USD")
                .fixedPrice(false)
                .build());
        bricklinkMarketplaceListingDao.insert(BricklinkMarketplaceListing.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .isStockRoom(true)
                .stockRoomId("C")
                .environmentCode("sandbox")
                .build());

        PricingCrawlWorkItem workItem = pricingCrawlWorkItemDao.insert(PricingCrawlWorkItem.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .sourceExternalServiceId(bricklink.getExternalServiceId())
                .workStatusCode("SUCCEEDED")
                .attemptCount(1)
                .maxAttempts(3)
                .nextAttemptAt(AT)
                .sourceRequestUrl("https://bricklink.example/catalogifs")
                .sourceRequestParameters("itemid=" + uuid)
                .build());
        PricingSnapshot snapshot = pricingSnapshotDao.insert(PricingSnapshot.builder()
                .pricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId())
                .marketplaceListingId(listing.getMarketplaceListingId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .sourceExternalServiceId(bricklink.getExternalServiceId())
                .sourceItemKey(uuid + "-1")
                .sourceUniqueKey(uuid)
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .sourceRequestUrl("https://bricklink.example/catalogifs")
                .sourceRequestParameters("itemid=" + uuid)
                .rawPayloadHash("hash-" + uuid)
                .comparableCount(1)
                .capturedAt(AT)
                .build());
        pricingSnapshotListingDao.insert(PricingSnapshotListing.builder()
                .pricingSnapshotId(snapshot.getPricingSnapshotId())
                .externalListingId("competitor-" + uuid)
                .sellerName("seller")
                .sellerCountryCode("US")
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .quantityAvailable(1)
                .unitPrice(new BigDecimal("50.00"))
                .currencyCode("USD")
                .build());
        PricingDecision decision = pricingDecisionDao.insert(PricingDecision.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .pricingSnapshotId(snapshot.getPricingSnapshotId())
                .algorithmVersion("BRICKLINK_MARKET_POSITION_V2")
                .decisionStatusCode("APPLIED")
                .reasonCode("MARKET_POSITION")
                .strategyCode("MARKET_POSITION")
                .computedPrice(new BigDecimal("49.00"))
                .finalPrice(new BigDecimal("49.00"))
                .currencyCode("USD")
                .comparableCount(1)
                .confidence(new BigDecimal("0.5000"))
                .build());
        PricingApplyReadiness readiness = pricingApplyReadinessDao.insert(PricingApplyReadiness.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .pricingDecisionId(decision.getPricingDecisionId())
                .pricingSnapshotId(snapshot.getPricingSnapshotId())
                .readinessStatusCode("READY_TO_APPLY_INITIAL_PRICE")
                .proposedPrice(new BigDecimal("49.00"))
                .currencyCode("USD")
                .confidence(new BigDecimal("0.5000"))
                .comparableCount(1)
                .evaluatedAt(AT)
                .build());
        marketplaceListingSyncRequestDao.insert(MarketplaceListingSyncRequest.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .listingExternalServiceId(bricklink.getExternalServiceId())
                .pricingDecisionId(decision.getPricingDecisionId())
                .pricingApplyReadinessId(readiness.getPricingApplyReadinessId())
                .syncRequestTypeCode("LISTING_CREATE")
                .syncRequestStatusCode("BLOCKED")
                .syncReasonCode("PRICING_DECISION_APPLIED")
                .requestedUnitPrice(new BigDecimal("49.00"))
                .currencyCode("USD")
                .environmentCode("sandbox")
                .attemptCount(1)
                .maxAttempts(3)
                .nextAttemptAt(AT)
                .build());
        return new Fixture(item, listing);
    }

    private Party party(String name) {
        Party party = Party.builder().partyFirstName(name).partyLastName("Test").partyType("PERSON").build();
        partyDao.insert(party);
        return party;
    }

    private int rows(String table) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
        return count == null ? 0 : count;
    }

    private record Fixture(ItemInventory item, MarketplaceListing listing) {
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
