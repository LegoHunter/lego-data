package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.dto.PricingSnapshotListing;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PricingPlaneMapperTest extends MapperTestSupport {
    private static final ZonedDateTime CRAWL_AT = ZonedDateTime.parse("2026-06-18T14:00:00Z");
    private static final ZonedDateTime SNAPSHOT_AT = ZonedDateTime.parse("2026-06-18T15:00:00Z");

    @Test
    void pricingCrawlWorkItemSupportsCrudAndDueLookup() {
        PricingFixture fixture = pricingFixture("pricing-work-item");
        PricingCrawlWorkItem workItem = pricingCrawlWorkItem(
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT
        );

        pricingCrawlWorkItemMapper.insert(workItem);
        workItem.setWorkStatusCode("CLAIMED");
        workItem.setAttemptCount(1);
        workItem.setClaimedAt(CRAWL_AT.plusMinutes(1));
        assertThat(pricingCrawlWorkItemMapper.update(workItem)).isOne();

        assertThat(pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getWorkStatusCode()).isEqualTo("CLAIMED");
                    assertThat(found.getAttemptCount()).isOne();
                });
        assertThat(pricingCrawlWorkItemMapper.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingCrawlWorkItemMapper.findByWorkStatusCode("CLAIMED")).hasSize(1);
        assertThat(pricingCrawlWorkItemMapper.findDueByWorkStatusCode("CLAIMED", CRAWL_AT.plusMinutes(2), 10)).hasSize(1);
        assertThat(pricingCrawlWorkItemMapper.findDueByWorkStatusCode("CLAIMED", CRAWL_AT.minusMinutes(1), 10)).isEmpty();
        assertThat(pricingCrawlWorkItemMapper.findAll()).hasSize(1);

        workItem.setWorkStatusCode("SUCCEEDED");
        pricingCrawlWorkItemMapper.upsert(workItem);
        assertThat(pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId()))
                .hasValueSatisfying(found -> assertThat(found.getWorkStatusCode()).isEqualTo("SUCCEEDED"));

        assertThat(pricingCrawlWorkItemMapper.delete(workItem.getPricingCrawlWorkItemId())).isOne();
        assertThat(pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(workItem.getPricingCrawlWorkItemId())).isEmpty();
    }

    @Test
    void pricingSnapshotSupportsCrudAndLatestLookup() {
        PricingFixture fixture = pricingFixture("pricing-snapshot");
        PricingCrawlWorkItem workItem = insertedWorkItem(fixture, CRAWL_AT);
        PricingSnapshot snapshot = pricingSnapshot(
                workItem.getPricingCrawlWorkItemId(),
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                SNAPSHOT_AT
        );

        pricingSnapshotMapper.insert(snapshot);
        snapshot.setComparableCount(3);
        snapshot.setRawPayloadHash("updated-hash");
        assertThat(pricingSnapshotMapper.update(snapshot)).isOne();

        assertThat(pricingSnapshotMapper.findByPricingSnapshotId(snapshot.getPricingSnapshotId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getComparableCount()).isEqualTo(3);
                    assertThat(found.getRawPayloadHash()).isEqualTo("updated-hash");
                });
        assertThat(pricingSnapshotMapper.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingSnapshotMapper.findByExternalCatalogItemId(fixture.catalogItem().getExternalCatalogItemId())).hasSize(1);
        assertThat(pricingSnapshotMapper.findLatestByMarketplaceListingId(fixture.listing().getMarketplaceListingId()))
                .map(PricingSnapshot::getPricingSnapshotId)
                .contains(snapshot.getPricingSnapshotId());
        assertThat(pricingSnapshotMapper.findAll()).hasSize(1);

        snapshot.setComparableCount(4);
        pricingSnapshotMapper.upsert(snapshot);
        assertThat(pricingSnapshotMapper.findByPricingSnapshotId(snapshot.getPricingSnapshotId()))
                .hasValueSatisfying(found -> assertThat(found.getComparableCount()).isEqualTo(4));

        assertThat(pricingSnapshotMapper.delete(snapshot.getPricingSnapshotId())).isOne();
        assertThat(pricingSnapshotMapper.findByPricingSnapshotId(snapshot.getPricingSnapshotId())).isEmpty();
    }

    @Test
    void pricingSnapshotListingSupportsCrudAndSnapshotLookup() {
        PricingFixture fixture = pricingFixture("pricing-snapshot-listing");
        PricingSnapshot snapshot = insertedSnapshot(fixture);
        PricingSnapshotListing listing = pricingSnapshotListing(snapshot.getPricingSnapshotId(), "remote-listing-1");

        pricingSnapshotListingMapper.insert(listing);
        listing.setUnitPrice(new BigDecimal("215.00"));
        listing.setSellerCountryCode("CA");
        assertThat(pricingSnapshotListingMapper.update(listing)).isOne();

        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotListingId(listing.getPricingSnapshotListingId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getUnitPrice()).isEqualByComparingTo("215.00");
                    assertThat(found.getSellerCountryCode()).isEqualTo("CA");
                });
        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotId(snapshot.getPricingSnapshotId())).hasSize(1);
        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotIdAndExternalListingId(snapshot.getPricingSnapshotId(), "remote-listing-1")).isPresent();
        assertThat(pricingSnapshotListingMapper.findAll()).hasSize(1);

        listing.setQuantityAvailable(2);
        pricingSnapshotListingMapper.upsert(listing);
        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotListingId(listing.getPricingSnapshotListingId()))
                .hasValueSatisfying(found -> assertThat(found.getQuantityAvailable()).isEqualTo(2));

        assertThat(pricingSnapshotListingMapper.delete(listing.getPricingSnapshotListingId())).isOne();
        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotListingId(listing.getPricingSnapshotListingId())).isEmpty();
    }

    @Test
    void pricingDecisionSupportsCrudAndReasonLookups() {
        PricingFixture fixture = pricingFixture("pricing-decision");
        PricingSnapshot snapshot = insertedSnapshot(fixture);
        PricingDecision decision = pricingDecision(fixture.listing().getMarketplaceListingId(), snapshot.getPricingSnapshotId());

        pricingDecisionMapper.insert(decision);
        decision.setDecisionStatusCode("APPLIED");
        decision.setAppliedAt(SNAPSHOT_AT.plusMinutes(5));
        decision.setFinalPrice(new BigDecimal("218.50"));
        assertThat(pricingDecisionMapper.update(decision)).isOne();

        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getDecisionStatusCode()).isEqualTo("APPLIED");
                    assertThat(found.getFinalPrice()).isEqualByComparingTo("218.50");
                });
        assertThat(pricingDecisionMapper.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(pricingDecisionMapper.findByDecisionStatusCode("APPLIED")).hasSize(1);
        assertThat(pricingDecisionMapper.findByReasonCode("MATCHED_LOWEST_COMPETITOR")).hasSize(1);
        assertThat(pricingDecisionMapper.findLatestByMarketplaceListingId(fixture.listing().getMarketplaceListingId()))
                .map(PricingDecision::getPricingDecisionId)
                .contains(decision.getPricingDecisionId());
        assertThat(pricingDecisionMapper.findAll()).hasSize(1);

        decision.setReasonCode("FIXED_PRICE_OVERRIDE");
        pricingDecisionMapper.upsert(decision);
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(found -> assertThat(found.getReasonCode()).isEqualTo("FIXED_PRICE_OVERRIDE"));

        assertThat(pricingDecisionMapper.delete(decision.getPricingDecisionId())).isOne();
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId())).isEmpty();
    }

    private PricingSnapshot insertedSnapshot(PricingFixture fixture) {
        PricingCrawlWorkItem workItem = insertedWorkItem(fixture, CRAWL_AT);
        PricingSnapshot snapshot = pricingSnapshot(
                workItem.getPricingCrawlWorkItemId(),
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                SNAPSHOT_AT
        );
        pricingSnapshotMapper.insert(snapshot);
        return snapshot;
    }

    private PricingCrawlWorkItem insertedWorkItem(PricingFixture fixture, ZonedDateTime nextAttemptAt) {
        PricingCrawlWorkItem workItem = pricingCrawlWorkItem(
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                nextAttemptAt
        );
        pricingCrawlWorkItemMapper.insert(workItem);
        return workItem;
    }

    private PricingFixture pricingFixture(String key) {
        seedExternalCatalog();
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "MARKETPLACE_LISTING"));
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "PRICE_GUIDE"));
        ExternalCatalogItem catalogItem = insertExternalCatalogItem(key);
        ItemInventory inventory = insertItemInventory("inventory-" + key);
        MarketplaceListing listing = marketplaceListing(
                inventory.getItemInventoryId(),
                catalogItem.getExternalCatalogItemId(),
                2,
                "listing-" + key
        );
        marketplaceListingMapper.insert(listing);
        return new PricingFixture(catalogItem, inventory, listing);
    }

    private record PricingFixture(
            ExternalCatalogItem catalogItem,
            ItemInventory inventory,
            MarketplaceListing listing
    ) {
    }
}
