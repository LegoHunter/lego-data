package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketSnapshot;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.MarketplaceListingSyncRequest;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingHydrationGap;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PricingPlaneMapperTest extends MapperTestSupport {
    private static final ZonedDateTime CRAWL_AT = ZonedDateTime.parse("2026-06-18T14:00:00Z");
    private static final ZonedDateTime SNAPSHOT_AT = ZonedDateTime.parse("2026-06-18T15:00:00Z");

    @Test
    void marketplaceListingMapperReportsActivePricingHydrationGaps() {
        PricingFixture missingHydration = pricingFixture("pricing-hydration-gap");
        missingHydration.catalogItem().setExternalUniqueKey(null);
        externalCatalogItemMapper.update(missingHydration.catalogItem());

        PricingFixture hydrated = pricingFixture("pricing-hydration-complete");

        Set<PricingHydrationGap> gaps = marketplaceListingMapper.findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(
                2,
                "ACTIVE",
                10
        );

        assertThat(gaps)
                .extracting(PricingHydrationGap::getMarketplaceListingId)
                .contains(missingHydration.listing().getMarketplaceListingId())
                .doesNotContain(hydrated.listing().getMarketplaceListingId());
        assertThat(gaps)
                .filteredOn(gap -> gap.getMarketplaceListingId().equals(missingHydration.listing().getMarketplaceListingId()))
                .singleElement()
                .satisfies(gap -> {
                    assertThat(gap.getExternalItemKey()).isEqualTo("pricing-hydration-gap");
                    assertThat(gap.getExternalUniqueKey()).isNull();
                    assertThat(gap.getNewOrUsed()).isEqualTo("USED");
                    assertThat(gap.getCompleteness()).isEqualTo("COMPLETE");
                });
    }

    @Test
    void pricingDecisionSupportsCrudAndReasonLookups() {
        PricingFixture fixture = pricingFixture("pricing-decision");
        PricingDecision decision = pricingDecision(fixture.listing().getMarketplaceListingId());

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

    @Test
    void pricingDecisionStoresTheRepriceGuardOutcome() {
        PricingFixture fixture = pricingFixture("pricing-decision-guard");
        PricingDecision decision = pricingDecision(fixture.listing().getMarketplaceListingId());
        decision.setDecisionStatusCode("BLOCKED");
        decision.setGuardStatusCode("BLOCKED");
        decision.setGuardReasonCode("BELOW_MINIMUM_DELTA");

        pricingDecisionMapper.insert(decision);
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getGuardStatusCode()).isEqualTo("BLOCKED");
                    assertThat(found.getGuardReasonCode()).isEqualTo("BELOW_MINIMUM_DELTA");
                });

        decision.setGuardStatusCode("PASSED");
        decision.setGuardReasonCode(null);
        assertThat(pricingDecisionMapper.update(decision)).isOne();
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getGuardStatusCode()).isEqualTo("PASSED");
                    assertThat(found.getGuardReasonCode()).isNull();
                });

        decision.setGuardReasonCode("ABOVE_MAXIMUM_PERCENT_DELTA");
        pricingDecisionMapper.upsert(decision);
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(found -> assertThat(found.getGuardReasonCode()).isEqualTo("ABOVE_MAXIMUM_PERCENT_DELTA"));

        // Decisions from the older decision job carry no guard outcome.
        PricingDecision older = pricingDecision(fixture.listing().getMarketplaceListingId());
        pricingDecisionMapper.insert(older);
        assertThat(pricingDecisionMapper.findByPricingDecisionId(older.getPricingDecisionId()))
                .hasValueSatisfying(found -> assertThat(found.getGuardStatusCode()).isNull());
    }

    @Test
    void pricingDecisionTransitionStatusOnlyMovesTheExpectedUnappliedStatus() {
        PricingFixture fixture = pricingFixture("pricing-decision-transition");
        PricingDecision decision = pricingDecision(fixture.listing().getMarketplaceListingId());
        decision.setDecisionStatusCode("PROPOSED");
        pricingDecisionMapper.insert(decision);

        assertThat(pricingDecisionMapper.transitionStatus(decision.getPricingDecisionId(), "FAILED", "ACCEPTED", null)).isZero();
        assertThat(pricingDecisionMapper.transitionStatus(decision.getPricingDecisionId(), "PROPOSED", "ACCEPTED", null)).isOne();
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(accepted -> {
                    assertThat(accepted.getDecisionStatusCode()).isEqualTo("ACCEPTED");
                    assertThat(accepted.getAppliedAt()).isNull();
                });

        assertThat(pricingDecisionMapper.transitionStatus(decision.getPricingDecisionId(), "ACCEPTED", "APPLIED", SNAPSHOT_AT.plusMinutes(5))).isOne();
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()))
                .hasValueSatisfying(applied -> {
                    assertThat(applied.getDecisionStatusCode()).isEqualTo("APPLIED");
                    assertThat(applied.getAppliedAt()).isNotNull();
                });
        assertThat(pricingDecisionMapper.transitionStatus(decision.getPricingDecisionId(), "APPLIED", "ACCEPTED", null)).isZero();
    }

    @Test
    void marketplaceListingSyncRequestSupportsCrudClaimAndIdempotentUpsert() {
        PricingFixture fixture = pricingFixture("pricing-sync-request");
        PricingDecision decision = pricingDecision(fixture.listing().getMarketplaceListingId());
        pricingDecisionMapper.insert(decision);
        MarketplaceListingSyncRequest syncRequest = marketplaceListingSyncRequest(
                fixture.listing().getMarketplaceListingId(),
                2,
                decision.getPricingDecisionId()
        );

        marketplaceListingSyncRequestMapper.insert(syncRequest);
        syncRequest.setSyncRequestStatusCode("PENDING");
        syncRequest.setLastErrorMessage("Waiting for sync");
        assertThat(marketplaceListingSyncRequestMapper.update(syncRequest)).isOne();

        assertThat(marketplaceListingSyncRequestMapper.findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getMarketplaceListingId()).isEqualTo(fixture.listing().getMarketplaceListingId());
                    assertThat(found.getPricingDecisionId()).isEqualTo(decision.getPricingDecisionId());
                    assertThat(found.getSyncRequestTypeCode()).isEqualTo("PRICE_UPDATE");
                    assertThat(found.getSyncRequestStatusCode()).isEqualTo("PENDING");
                    assertThat(found.getRemoteVisibilityScopeCode()).isEqualTo("STOCKROOM");
                    assertThat(found.getRemoteIsPubliclyAvailable()).isFalse();
                    assertThat(found.getLastErrorMessage()).isEqualTo("Waiting for sync");
                });
        assertThat(marketplaceListingSyncRequestMapper.findByMarketplaceListingId(fixture.listing().getMarketplaceListingId())).hasSize(1);
        assertThat(marketplaceListingSyncRequestMapper.findBySyncRequestStatusCode("PENDING")).hasSize(1);
        assertThat(marketplaceListingSyncRequestMapper.countBySyncRequestStatusCode("PENDING")).isOne();
        assertThat(marketplaceListingSyncRequestMapper.countDueBySyncRequestStatusCode("PENDING", SNAPSHOT_AT)).isZero();
        assertThat(marketplaceListingSyncRequestMapper.countDueBySyncRequestStatusCode("PENDING", SNAPSHOT_AT.plusHours(2))).isOne();
        assertThat(marketplaceListingSyncRequestMapper.findClaimableByStatusCode("PENDING", SNAPSHOT_AT.plusHours(2), 10))
                .extracting(MarketplaceListingSyncRequest::getMarketplaceListingSyncRequestId)
                .containsExactly(syncRequest.getMarketplaceListingSyncRequestId());

        MarketplaceListingSyncRequest listingCreateRequest = marketplaceListingSyncRequest(
                fixture.listing().getMarketplaceListingId(),
                2,
                null
        );
        listingCreateRequest.setSyncRequestTypeCode("LISTING_CREATE");
        listingCreateRequest.setSyncReasonCode("MANUAL_LISTING_CREATE");
        listingCreateRequest.setRemoteInventoryId(null);
        marketplaceListingSyncRequestMapper.insert(listingCreateRequest);
        assertThat(marketplaceListingSyncRequestMapper.findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
                fixture.listing().getMarketplaceListingId(),
                "LISTING_CREATE",
                Set.of("PENDING", "CLAIMED")
        )).extracting(MarketplaceListingSyncRequest::getMarketplaceListingSyncRequestId)
                .containsExactly(listingCreateRequest.getMarketplaceListingSyncRequestId());
        assertThat(marketplaceListingSyncRequestMapper.findClaimableByStatusCodeAndSyncRequestTypeCodes(
                "PENDING",
                Set.of("PRICE_UPDATE"),
                SNAPSHOT_AT.plusHours(2),
                10
        )).extracting(MarketplaceListingSyncRequest::getMarketplaceListingSyncRequestId)
                .containsExactly(syncRequest.getMarketplaceListingSyncRequestId());

        assertThat(marketplaceListingSyncRequestMapper.claim(
                syncRequest.getMarketplaceListingSyncRequestId(),
                "PENDING",
                "CLAIMED",
                SNAPSHOT_AT.plusHours(2)
        )).isOne();
        assertThat(marketplaceListingSyncRequestMapper.claim(
                syncRequest.getMarketplaceListingSyncRequestId(),
                "PENDING",
                "CLAIMED",
                SNAPSHOT_AT.plusHours(2)
        )).isZero();
        assertThat(marketplaceListingSyncRequestMapper.findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getSyncRequestStatusCode()).isEqualTo("CLAIMED");
                    assertThat(found.getAttemptCount()).isOne();
                    assertThat(found.getClaimedAt()).isEqualTo(SNAPSHOT_AT.plusHours(2));
                });

        syncRequest.setSyncRequestStatusCode("PENDING");
        syncRequest.setRequestedUnitPrice(new BigDecimal("218.00"));
        syncRequest.setNextAttemptAt(SNAPSHOT_AT.plusHours(3));
        marketplaceListingSyncRequestMapper.upsert(syncRequest);
        assertThat(marketplaceListingSyncRequestMapper.findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(
                fixture.listing().getMarketplaceListingId(),
                decision.getPricingDecisionId(),
                "PRICE_UPDATE"
        )).hasValueSatisfying(found -> {
            assertThat(found.getMarketplaceListingSyncRequestId()).isEqualTo(syncRequest.getMarketplaceListingSyncRequestId());
            assertThat(found.getRequestedUnitPrice()).isEqualByComparingTo("218.00");
        });

        assertThat(marketplaceListingSyncRequestMapper.findAll()).hasSize(2);
        assertThat(marketplaceListingSyncRequestMapper.delete(listingCreateRequest.getMarketplaceListingSyncRequestId())).isOne();
        assertThat(marketplaceListingSyncRequestMapper.delete(syncRequest.getMarketplaceListingSyncRequestId())).isOne();
        assertThat(marketplaceListingSyncRequestMapper.findAll()).isEmpty();
    }

    private PricingFixture pricingFixture(String key) {
        seedExternalCatalog();
        externalServiceCapabilityMapper.upsert(externalServiceCapability(2, "MARKETPLACE_LISTING"));
        externalServiceCapabilityMapper.upsert(externalServiceCapability(2, "PRICE_GUIDE"));
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
