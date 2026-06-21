package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.dto.PricingSnapshotListing;
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
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/current_lookup_data.sql"
})
class PricingPlaneDaoTest {
    private static final ZonedDateTime CRAWL_AT = ZonedDateTime.parse("2026-06-18T14:00:00Z");
    private static final ZonedDateTime SNAPSHOT_AT = ZonedDateTime.parse("2026-06-18T15:00:00Z");

    @Autowired ConditionDao conditionDao;
    @Autowired ExternalCatalogItemDao externalCatalogItemDao;
    @Autowired ExternalServiceDao externalServiceDao;
    @Autowired ItemInventoryDao itemInventoryDao;
    @Autowired MarketplaceListingDao marketplaceListingDao;
    @Autowired PricingCrawlWorkItemDao pricingCrawlWorkItemDao;
    @Autowired PricingSnapshotDao pricingSnapshotDao;
    @Autowired PricingSnapshotListingDao pricingSnapshotListingDao;
    @Autowired PricingDecisionDao pricingDecisionDao;

    @Test
    void pricingDaosSupportPhaseOneCrudAndLookups() {
        PricingFixture fixture = pricingFixture();

        PricingCrawlWorkItem workItem = pricingCrawlWorkItem(fixture);
        workItem = pricingCrawlWorkItemDao.insert(workItem);
        workItem.setWorkStatusCode("CLAIMED");
        workItem = pricingCrawlWorkItemDao.update(workItem);
        assertThat(workItem.getWorkStatusCode()).isEqualTo("CLAIMED");
        assertThat(pricingCrawlWorkItemDao.findByPricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId())).isPresent();
        assertThat(pricingCrawlWorkItemDao.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingCrawlWorkItemDao.findByWorkStatusCode("CLAIMED")).hasSize(1);
        assertThat(pricingCrawlWorkItemDao.findDueByWorkStatusCode("CLAIMED", CRAWL_AT.plusMinutes(1), 5)).hasSize(1);
        assertThat(pricingCrawlWorkItemDao.findAll()).hasSize(1);
        workItem.setWorkStatusCode("SUCCEEDED");
        assertThat(pricingCrawlWorkItemDao.upsert(workItem).getWorkStatusCode()).isEqualTo("SUCCEEDED");

        PricingSnapshot snapshot = pricingSnapshot(workItem, fixture);
        snapshot = pricingSnapshotDao.insert(snapshot);
        snapshot.setComparableCount(3);
        snapshot = pricingSnapshotDao.update(snapshot);
        assertThat(snapshot.getComparableCount()).isEqualTo(3);
        assertThat(pricingSnapshotDao.findByPricingSnapshotId(snapshot.getPricingSnapshotId())).isPresent();
        assertThat(pricingSnapshotDao.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingSnapshotDao.findByExternalCatalogItemId(fixture.catalogItem().getExternalCatalogItemId())).hasSize(1);
        assertThat(pricingSnapshotDao.findLatestByMarketplaceListingId(fixture.listing().getMarketplaceListingId()))
                .map(PricingSnapshot::getPricingSnapshotId)
                .contains(snapshot.getPricingSnapshotId());
        assertThat(pricingSnapshotDao.findAll()).hasSize(1);
        snapshot.setComparableCount(4);
        assertThat(pricingSnapshotDao.upsert(snapshot).getComparableCount()).isEqualTo(4);

        PricingSnapshotListing snapshotListing = pricingSnapshotListing(snapshot);
        snapshotListing = pricingSnapshotListingDao.insert(snapshotListing);
        snapshotListing.setUnitPrice(new BigDecimal("215.00"));
        snapshotListing = pricingSnapshotListingDao.update(snapshotListing);
        assertThat(snapshotListing.getUnitPrice()).isEqualByComparingTo("215.00");
        assertThat(pricingSnapshotListingDao.findByPricingSnapshotListingId(snapshotListing.getPricingSnapshotListingId())).isPresent();
        assertThat(pricingSnapshotListingDao.findByPricingSnapshotId(snapshot.getPricingSnapshotId())).hasSize(1);
        assertThat(pricingSnapshotListingDao.findByPricingSnapshotIdAndExternalListingId(snapshot.getPricingSnapshotId(), "remote-listing-1")).isPresent();
        assertThat(pricingSnapshotListingDao.findAll()).hasSize(1);
        snapshotListing.setQuantityAvailable(2);
        assertThat(pricingSnapshotListingDao.upsert(snapshotListing).getQuantityAvailable()).isEqualTo(2);

        PricingDecision decision = pricingDecision(snapshot, fixture);
        decision = pricingDecisionDao.insert(decision);
        decision.setDecisionStatusCode("APPLIED");
        decision.setAppliedAt(SNAPSHOT_AT.plusMinutes(1));
        decision = pricingDecisionDao.update(decision);
        assertThat(decision.getDecisionStatusCode()).isEqualTo("APPLIED");
        assertThat(pricingDecisionDao.findByPricingDecisionId(decision.getPricingDecisionId())).isPresent();
        assertThat(pricingDecisionDao.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingDecisionDao.findByDecisionStatusCode("APPLIED")).hasSize(1);
        assertThat(pricingDecisionDao.findByReasonCode("MATCHED_LOWEST_COMPETITOR")).hasSize(1);
        assertThat(pricingDecisionDao.findLatestByMarketplaceListingId(fixture.listing().getMarketplaceListingId()))
                .map(PricingDecision::getPricingDecisionId)
                .contains(decision.getPricingDecisionId());
        assertThat(pricingDecisionDao.findAll()).hasSize(1);
        decision.setReasonCode("FIXED_PRICE_OVERRIDE");
        assertThat(pricingDecisionDao.upsert(decision).getReasonCode()).isEqualTo("FIXED_PRICE_OVERRIDE");

        pricingDecisionDao.delete(decision.getPricingDecisionId());
        pricingSnapshotListingDao.delete(snapshotListing.getPricingSnapshotListingId());
        pricingSnapshotDao.delete(snapshot.getPricingSnapshotId());
        pricingCrawlWorkItemDao.delete(workItem.getPricingCrawlWorkItemId());
        assertThat(pricingDecisionDao.findAll()).isEmpty();
        assertThat(pricingSnapshotListingDao.findAll()).isEmpty();
        assertThat(pricingSnapshotDao.findAll()).isEmpty();
        assertThat(pricingCrawlWorkItemDao.findAll()).isEmpty();
    }

    private PricingFixture pricingFixture() {
        seedDefaultCondition();
        ExternalService bricklink = externalServiceDao.findByServiceCode("BRICKLINK").orElseThrow();
        ExternalCatalogItem catalogItem = ExternalCatalogItem.builder()
                .externalServiceId(bricklink.getExternalServiceId())
                .externalItemKey("6390-1")
                .externalUniqueKey("4997")
                .itemName("Main Street")
                .itemTypeCode("SET")
                .itemUrl("https://bricklink.example/6390-1")
                .yearReleased(1980)
                .build();
        catalogItem = externalCatalogItemDao.insert(catalogItem);

        ItemInventory inventory = new ItemInventory();
        inventory.setUuid("pricing-dao-inventory");
        inventory.setBoxNumber(1);
        inventory.setPurchasePrice(new BigDecimal("100.00"));
        inventory.setDescription("Pricing DAO inventory");
        inventory.setActive(true);
        inventory.setForSale(true);
        inventory.setNewOrUsed("USED");
        inventory.setCompleteness("COMPLETE");
        inventory.setItemConditionId(1);
        inventory.setBoxConditionId(1);
        inventory.setInstructionsConditionId(1);
        inventory.setSealed(false);
        inventory.setBuiltOnce(true);
        inventory = itemInventoryDao.insert(inventory);

        MarketplaceListing listing = MarketplaceListing.builder()
                .itemInventoryId(inventory.getItemInventoryId())
                .listingExternalServiceId(bricklink.getExternalServiceId())
                .externalCatalogItemId(catalogItem.getExternalCatalogItemId())
                .externalListingId("BL-PRICING-1")
                .externalListingUrl("https://bricklink.example/listing/BL-PRICING-1")
                .listingStatusCode("ACTIVE")
                .title("Main Street")
                .description("Complete")
                .unitPrice(new BigDecimal("225.00"))
                .currencyCode("USD")
                .fixedPrice(false)
                .build();
        listing = marketplaceListingDao.insert(listing);
        return new PricingFixture(bricklink, catalogItem, inventory, listing);
    }

    private PricingCrawlWorkItem pricingCrawlWorkItem(PricingFixture fixture) {
        return PricingCrawlWorkItem.builder()
                .marketplaceListingId(fixture.listing().getMarketplaceListingId())
                .externalCatalogItemId(fixture.catalogItem().getExternalCatalogItemId())
                .sourceExternalServiceId(fixture.bricklink().getExternalServiceId())
                .workStatusCode("PENDING")
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(CRAWL_AT)
                .sourceRequestUrl("https://bricklink.example/ajax/clone/catalogifs.ajax")
                .sourceRequestParameters("itemid=4997&rpp=500&iconly=0")
                .build();
    }

    private PricingSnapshot pricingSnapshot(PricingCrawlWorkItem workItem, PricingFixture fixture) {
        return PricingSnapshot.builder()
                .pricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId())
                .marketplaceListingId(fixture.listing().getMarketplaceListingId())
                .externalCatalogItemId(fixture.catalogItem().getExternalCatalogItemId())
                .sourceExternalServiceId(fixture.bricklink().getExternalServiceId())
                .sourceItemKey("6390-1")
                .sourceUniqueKey("4997")
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .sourceRequestUrl("https://bricklink.example/ajax/clone/catalogifs.ajax")
                .sourceRequestParameters("itemid=4997&rpp=500&iconly=0")
                .rawPayloadHash("hash-1")
                .comparableCount(2)
                .capturedAt(SNAPSHOT_AT)
                .build();
    }

    private PricingSnapshotListing pricingSnapshotListing(PricingSnapshot snapshot) {
        return PricingSnapshotListing.builder()
                .pricingSnapshotId(snapshot.getPricingSnapshotId())
                .externalListingId("remote-listing-1")
                .sellerName("seller-one")
                .sellerCountryCode("US")
                .itemConditionCode("U")
                .completenessCode("COMPLETE")
                .quantityAvailable(1)
                .unitPrice(new BigDecimal("220.00"))
                .currencyCode("USD")
                .description("Main Street")
                .extendedDescription("Complete with box")
                .sourceListingPayload("{\"id\":\"remote-listing-1\"}")
                .build();
    }

    private PricingDecision pricingDecision(PricingSnapshot snapshot, PricingFixture fixture) {
        return PricingDecision.builder()
                .marketplaceListingId(fixture.listing().getMarketplaceListingId())
                .pricingSnapshotId(snapshot.getPricingSnapshotId())
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

    private record PricingFixture(
            ExternalService bricklink,
            ExternalCatalogItem catalogItem,
            ItemInventory inventory,
            MarketplaceListing listing
    ) {
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
