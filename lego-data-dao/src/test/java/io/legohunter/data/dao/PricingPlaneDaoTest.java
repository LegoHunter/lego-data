package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.MarketplaceListingSyncRequest;
import io.legohunter.data.dto.PricingDecision;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/marketplace_pricing_schema.ddl",
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
    @Autowired PricingDecisionDao pricingDecisionDao;
    @Autowired MarketplaceListingSyncRequestDao marketplaceListingSyncRequestDao;

    @Test
    void pricingDaosSupportPhaseOneCrudAndLookups() {
        PricingFixture fixture = pricingFixture();

        PricingDecision decision = pricingDecision(fixture);
        decision = pricingDecisionDao.insert(decision);
        decision.setDecisionStatusCode("APPLIED");
        decision.setAppliedAt(SNAPSHOT_AT.plusMinutes(1));
        decision = pricingDecisionDao.update(decision);
        assertThat(decision.getDecisionStatusCode()).isEqualTo("APPLIED");
        assertThat(pricingDecisionDao.findByPricingDecisionId(decision.getPricingDecisionId())).isPresent();
        assertThat(pricingDecisionDao.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingDecisionDao.findByDecisionStatusCode("APPLIED")).hasSize(1);
        assertThat(pricingDecisionDao.findByReasonCode("MATCHED_LOWEST_COMPETITOR")).hasSize(1);
        assertThat(pricingDecisionDao.countLatestByDecisionStatusCode("APPLIED")).isOne();
        assertThat(pricingDecisionDao.countLatestUnappliedByDecisionStatusCode("APPLIED")).isZero();
        assertThat(pricingDecisionDao.findLatestByMarketplaceListingId(fixture.listing().getMarketplaceListingId()))
                .map(PricingDecision::getPricingDecisionId)
                .contains(decision.getPricingDecisionId());
        assertThat(pricingDecisionDao.findAll()).hasSize(1);
        decision.setReasonCode("FIXED_PRICE_OVERRIDE");
        assertThat(pricingDecisionDao.upsert(decision).getReasonCode()).isEqualTo("FIXED_PRICE_OVERRIDE");

        pricingDecisionDao.delete(decision.getPricingDecisionId());
        assertThat(pricingDecisionDao.findAll()).isEmpty();
    }

    @Test
    void marketplaceListingSyncRequestDaoSupportsCrudClaimAndMetricsCounts() {
        PricingFixture fixture = pricingFixture();
        PricingDecision decision = pricingDecisionDao.insert(pricingDecision(fixture));
        MarketplaceListingSyncRequest syncRequest = marketplaceListingSyncRequest(fixture, decision);

        syncRequest = marketplaceListingSyncRequestDao.insert(syncRequest);
        syncRequest.setLastErrorMessage("Waiting for remote worker");
        syncRequest = marketplaceListingSyncRequestDao.update(syncRequest);

        assertThat(syncRequest.getLastErrorMessage()).isEqualTo("Waiting for remote worker");
        assertThat(marketplaceListingSyncRequestDao.findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId())).isPresent();
        assertThat(marketplaceListingSyncRequestDao.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(marketplaceListingSyncRequestDao.findBySyncRequestStatusCode("PENDING")).hasSize(1);
        assertThat(marketplaceListingSyncRequestDao.countBySyncRequestStatusCode("PENDING")).isOne();
        assertThat(marketplaceListingSyncRequestDao.countDueBySyncRequestStatusCode("PENDING", SNAPSHOT_AT.plusHours(2))).isOne();
        assertThat(marketplaceListingSyncRequestDao.findClaimableByStatusCode("PENDING", SNAPSHOT_AT.plusHours(2), 10)).hasSize(1);

        MarketplaceListingSyncRequest listingCreateRequest = marketplaceListingSyncRequest(fixture, decision);
        listingCreateRequest.setPricingDecisionId(null);
        listingCreateRequest.setSyncRequestTypeCode("LISTING_CREATE");
        listingCreateRequest.setSyncReasonCode("MANUAL_LISTING_CREATE");
        listingCreateRequest.setRemoteInventoryId(null);
        listingCreateRequest = marketplaceListingSyncRequestDao.insert(listingCreateRequest);
        assertThat(marketplaceListingSyncRequestDao.findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
                fixture.listing().getMarketplaceListingId(),
                "LISTING_CREATE",
                Set.of("PENDING", "CLAIMED")
        )).extracting(MarketplaceListingSyncRequest::getMarketplaceListingSyncRequestId)
                .containsExactly(listingCreateRequest.getMarketplaceListingSyncRequestId());
        assertThat(marketplaceListingSyncRequestDao.findClaimableByStatusCodeAndSyncRequestTypeCodes(
                "PENDING",
                Set.of("PRICE_UPDATE"),
                SNAPSHOT_AT.plusHours(2),
                10
        )).extracting(MarketplaceListingSyncRequest::getMarketplaceListingSyncRequestId)
                .containsExactly(syncRequest.getMarketplaceListingSyncRequestId());

        assertThat(marketplaceListingSyncRequestDao.claim(
                syncRequest.getMarketplaceListingSyncRequestId(),
                "PENDING",
                "CLAIMED",
                SNAPSHOT_AT.plusHours(2)
        )).hasValueSatisfying(claimed -> {
            assertThat(claimed.getSyncRequestStatusCode()).isEqualTo("CLAIMED");
            assertThat(claimed.getAttemptCount()).isOne();
        });
        assertThat(marketplaceListingSyncRequestDao.claim(
                syncRequest.getMarketplaceListingSyncRequestId(),
                "PENDING",
                "CLAIMED",
                SNAPSHOT_AT.plusHours(2)
        )).isEmpty();

        syncRequest.setSyncRequestStatusCode("PENDING");
        syncRequest.setRequestedUnitPrice(new BigDecimal("218.00"));
        syncRequest = marketplaceListingSyncRequestDao.upsert(syncRequest);
        assertThat(syncRequest.getRequestedUnitPrice()).isEqualByComparingTo("218.00");
        assertThat(marketplaceListingSyncRequestDao.findAll()).hasSize(2);

        marketplaceListingSyncRequestDao.delete(listingCreateRequest.getMarketplaceListingSyncRequestId());
        marketplaceListingSyncRequestDao.delete(syncRequest.getMarketplaceListingSyncRequestId());
        assertThat(marketplaceListingSyncRequestDao.findAll()).isEmpty();
    }

    @Test
    void pricingDecisionDaoMovesAProposedDecisionThroughAcceptedToApplied() {
        PricingFixture fixture = pricingFixture();
        PricingDecision decision = pricingDecisionDao.insert(pricingDecision(fixture));

        assertThat(pricingDecisionDao.transitionStatus(decision.getPricingDecisionId(), "PROPOSED", "ACCEPTED", null))
                .hasValueSatisfying(accepted -> {
                    assertThat(accepted.getDecisionStatusCode()).isEqualTo("ACCEPTED");
                    assertThat(accepted.getAppliedAt()).isNull();
                });
        // A second worker that still thinks it is PROPOSED loses the race.
        assertThat(pricingDecisionDao.transitionStatus(decision.getPricingDecisionId(), "PROPOSED", "ACCEPTED", null)).isEmpty();

        assertThat(pricingDecisionDao.transitionStatus(decision.getPricingDecisionId(), "ACCEPTED", "APPLIED", SNAPSHOT_AT.plusMinutes(5)))
                .hasValueSatisfying(applied -> {
                    assertThat(applied.getDecisionStatusCode()).isEqualTo("APPLIED");
                    assertThat(applied.getAppliedAt()).isNotNull();
                });
        // Applied is final.
        assertThat(pricingDecisionDao.transitionStatus(decision.getPricingDecisionId(), "APPLIED", "ACCEPTED", null)).isEmpty();
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

    private PricingDecision pricingDecision(PricingFixture fixture) {
        return PricingDecision.builder()
                .marketplaceListingId(fixture.listing().getMarketplaceListingId())
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

    private MarketplaceListingSyncRequest marketplaceListingSyncRequest(PricingFixture fixture, PricingDecision decision) {
        return MarketplaceListingSyncRequest.builder()
                .marketplaceListingId(fixture.listing().getMarketplaceListingId())
                .listingExternalServiceId(fixture.bricklink().getExternalServiceId())
                .pricingDecisionId(decision.getPricingDecisionId())
                .syncRequestTypeCode("PRICE_UPDATE")
                .syncRequestStatusCode("PENDING")
                .syncReasonCode("PRICING_DECISION_APPLIED")
                .previousUnitPrice(new BigDecimal("225.00"))
                .requestedUnitPrice(new BigDecimal("219.00"))
                .currencyCode("USD")
                .remoteInventoryId("123456")
                .remoteVisibilityScopeCode("STOCKROOM")
                .remoteVisibilityContainerId("A")
                .remoteIsPubliclyAvailable(false)
                .environmentCode("sandbox")
                .createdByJobName("BricklinkPricingApplyJob")
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(SNAPSHOT_AT.plusHours(1))
                .appliedLocalAt(SNAPSHOT_AT.plusMinutes(30))
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
