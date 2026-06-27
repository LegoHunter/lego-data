package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingCrawlWorkItemDuplicate;
import io.legohunter.data.dto.PricingCrawlWorkItemFailure;
import io.legohunter.data.dto.PricingCrawlWorkItemMaintenanceSummary;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingDecisionReview;
import io.legohunter.data.dto.PricingHydrationGap;
import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.dto.PricingSnapshotListing;
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
    void pricingCrawlWorkItemSupportsClaimAndStaleRequeue() {
        PricingFixture fixture = pricingFixture("pricing-work-claim");
        PricingCrawlWorkItem dueWorkItem = insertedWorkItem(fixture, CRAWL_AT.minusMinutes(1));
        PricingCrawlWorkItem retryableWorkItem = pricingCrawlWorkItem(
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.plusHours(6)
        );
        retryableWorkItem.setAttemptCount(1);
        retryableWorkItem.setMaxAttempts(3);
        pricingCrawlWorkItemMapper.insert(retryableWorkItem);
        PricingCrawlWorkItem maxedWorkItem = pricingCrawlWorkItem(
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.minusMinutes(1)
        );
        maxedWorkItem.setAttemptCount(3);
        maxedWorkItem.setMaxAttempts(3);
        pricingCrawlWorkItemMapper.insert(maxedWorkItem);

        assertThat(pricingCrawlWorkItemMapper.countByWorkStatusCode("PENDING")).isEqualTo(3);
        assertThat(pricingCrawlWorkItemMapper.countDueByWorkStatusCode("PENDING", CRAWL_AT)).isEqualTo(2);
        assertThat(pricingCrawlWorkItemMapper.countRetryableByWorkStatusCode("PENDING")).isOne();
        assertThat(pricingCrawlWorkItemMapper.findClaimableByWorkStatusCode("PENDING", CRAWL_AT, 10))
                .extracting(PricingCrawlWorkItem::getPricingCrawlWorkItemId)
                .containsExactly(dueWorkItem.getPricingCrawlWorkItemId());

        assertThat(pricingCrawlWorkItemMapper.claim(
                dueWorkItem.getPricingCrawlWorkItemId(),
                "PENDING",
                "CLAIMED",
                CRAWL_AT,
                CRAWL_AT
        )).isOne();
        assertThat(pricingCrawlWorkItemMapper.claim(
                dueWorkItem.getPricingCrawlWorkItemId(),
                "PENDING",
                "CLAIMED",
                CRAWL_AT,
                CRAWL_AT.plusSeconds(1)
        )).isZero();

        assertThat(pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(dueWorkItem.getPricingCrawlWorkItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getWorkStatusCode()).isEqualTo("CLAIMED");
                    assertThat(found.getAttemptCount()).isOne();
                    assertThat(found.getClaimedAt()).isEqualTo(CRAWL_AT);
                });
        assertThat(pricingCrawlWorkItemMapper.countStaleClaimed("CLAIMED", CRAWL_AT.plusMinutes(5))).isOne();

        assertThat(pricingCrawlWorkItemMapper.requeueStaleClaimed(
                "CLAIMED",
                "PENDING",
                CRAWL_AT.plusMinutes(5),
                CRAWL_AT.plusHours(1),
                "Recovered stale claim"
        )).isOne();
        assertThat(pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(dueWorkItem.getPricingCrawlWorkItemId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getWorkStatusCode()).isEqualTo("PENDING");
                    assertThat(found.getClaimedAt()).isNull();
                    assertThat(found.getNextAttemptAt()).isEqualTo(CRAWL_AT.plusHours(1));
                    assertThat(found.getLastErrorMessage()).isEqualTo("Recovered stale claim");
                });
    }

    @Test
    void pricingCrawlWorkItemMaintenanceSummaryAndDuplicateReportExposeQueueHygiene() {
        PricingFixture duplicateFixture = pricingFixture("pricing-work-maintenance-duplicate");
        PricingCrawlWorkItem pendingDue = insertedWorkItem(duplicateFixture, CRAWL_AT.minusMinutes(1));

        PricingCrawlWorkItem pendingRetryable = pricingCrawlWorkItem(
                duplicateFixture.listing().getMarketplaceListingId(),
                duplicateFixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.minusMinutes(2)
        );
        pendingRetryable.setAttemptCount(1);
        pendingRetryable.setMaxAttempts(3);
        pricingCrawlWorkItemMapper.insert(pendingRetryable);

        PricingCrawlWorkItem staleClaimed = pricingCrawlWorkItem(
                duplicateFixture.listing().getMarketplaceListingId(),
                duplicateFixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.minusHours(2)
        );
        staleClaimed.setWorkStatusCode("CLAIMED");
        staleClaimed.setAttemptCount(1);
        staleClaimed.setMaxAttempts(3);
        staleClaimed.setClaimedAt(CRAWL_AT.minusHours(3));
        pricingCrawlWorkItemMapper.insert(staleClaimed);

        PricingFixture terminalFixture = pricingFixture("pricing-work-maintenance-terminal");
        PricingCrawlWorkItem succeeded = insertedWorkItem(terminalFixture, CRAWL_AT.plusDays(7));
        succeeded.setWorkStatusCode("SUCCEEDED");
        pricingCrawlWorkItemMapper.update(succeeded);

        PricingCrawlWorkItem skipped = pricingCrawlWorkItem(
                terminalFixture.listing().getMarketplaceListingId(),
                terminalFixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.plusDays(7)
        );
        skipped.setWorkStatusCode("SKIPPED_MISSING_CONDITION");
        pricingCrawlWorkItemMapper.insert(skipped);

        PricingCrawlWorkItem failed = pricingCrawlWorkItem(
                terminalFixture.listing().getMarketplaceListingId(),
                terminalFixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.plusDays(7)
        );
        failed.setWorkStatusCode("FAILED_ITEM_ID_LOOKUP_NO_MATCH");
        pricingCrawlWorkItemMapper.insert(failed);

        PricingCrawlWorkItemMaintenanceSummary summary = pricingCrawlWorkItemMapper.summarizeMaintenance(
                "PENDING",
                "CLAIMED",
                "SUCCEEDED",
                CRAWL_AT,
                CRAWL_AT.minusHours(1)
        );

        assertThat(summary.getWorkItemCount()).isEqualTo(6);
        assertThat(summary.getDistinctMarketplaceListingCount()).isEqualTo(2);
        assertThat(summary.getDuplicateWorkItemCount()).isEqualTo(4);
        assertThat(summary.getPendingWorkItemCount()).isEqualTo(2);
        assertThat(summary.getDistinctPendingMarketplaceListingCount()).isOne();
        assertThat(summary.getDuplicatePendingWorkItemCount()).isOne();
        assertThat(summary.getDuePendingWorkItemCount()).isEqualTo(2);
        assertThat(summary.getRetryablePendingWorkItemCount()).isOne();
        assertThat(summary.getClaimedWorkItemCount()).isOne();
        assertThat(summary.getStaleClaimedWorkItemCount()).isOne();
        assertThat(summary.getSucceededWorkItemCount()).isOne();
        assertThat(summary.getSkippedWorkItemCount()).isOne();
        assertThat(summary.getFailedWorkItemCount()).isOne();

        PricingCrawlWorkItem historicalTerminal = pricingCrawlWorkItem(
                terminalFixture.listing().getMarketplaceListingId(),
                terminalFixture.catalogItem().getExternalCatalogItemId(),
                2,
                CRAWL_AT.plusDays(8)
        );
        historicalTerminal.setWorkStatusCode("SUCCEEDED");
        pricingCrawlWorkItemMapper.insert(historicalTerminal);

        Set<PricingCrawlWorkItemDuplicate> duplicates = pricingCrawlWorkItemMapper.findDuplicateMarketplaceListingWorkItems("PENDING", "CLAIMED", 10);

        assertThat(duplicates)
                .filteredOn(duplicate -> duplicate.getMarketplaceListingId().equals(duplicateFixture.listing().getMarketplaceListingId()))
                .singleElement()
                .satisfies(duplicate -> {
                    assertThat(duplicate.getWorkItemCount()).isEqualTo(3);
                    assertThat(duplicate.getPendingCount()).isEqualTo(2);
                    assertThat(duplicate.getWorkStatusCodes()).contains("PENDING", "CLAIMED");
                    assertThat(duplicate.getPricingCrawlWorkItemIds()).contains(String.valueOf(pendingDue.getPricingCrawlWorkItemId()));
                });
        assertThat(duplicates)
                .extracting(PricingCrawlWorkItemDuplicate::getMarketplaceListingId)
                .doesNotContain(terminalFixture.listing().getMarketplaceListingId());
    }

    @Test
    void pricingCrawlWorkItemMapperReportsRecentFailuresWithListingContext() {
        PricingFixture fixture = pricingFixture("pricing-work-failure-context");
        PricingCrawlWorkItem failed = insertedWorkItem(fixture, CRAWL_AT);
        failed.setWorkStatusCode("FAILED_PRICING_HTTP_ERROR");
        failed.setAttemptCount(3);
        failed.setMaxAttempts(3);
        failed.setSourceRequestUrl("/ajax/clone/catalogifs.ajax");
        failed.setSourceRequestParameters("{\"itemid\":4997,\"cond\":\"U\"}");
        failed.setLastErrorMessage("GET https://www.bricklink.com/ajax/clone/catalogifs.ajax returned []");
        pricingCrawlWorkItemMapper.update(failed);

        Set<PricingCrawlWorkItemFailure> failures = pricingCrawlWorkItemMapper.findRecentFailures(10);

        assertThat(failures)
                .filteredOn(failure -> failure.getPricingCrawlWorkItemId().equals(failed.getPricingCrawlWorkItemId()))
                .singleElement()
                .satisfies(failure -> {
                    assertThat(failure.getMarketplaceListingId()).isEqualTo(fixture.listing().getMarketplaceListingId());
                    assertThat(failure.getExternalListingId()).isEqualTo(fixture.listing().getExternalListingId());
                    assertThat(failure.getExternalItemKey()).isEqualTo("pricing-work-failure-context");
                    assertThat(failure.getExternalUniqueKey()).isEqualTo("unique-pricing-work-failure-context");
                    assertThat(failure.getItemInventoryUuid()).isEqualTo(fixture.inventory().getUuid());
                    assertThat(failure.getNewOrUsed()).isEqualTo("USED");
                    assertThat(failure.getCompleteness()).isEqualTo("COMPLETE");
                    assertThat(failure.getWorkStatusCode()).isEqualTo("FAILED_PRICING_HTTP_ERROR");
                    assertThat(failure.getAttemptCount()).isEqualTo(3);
                    assertThat(failure.getLastErrorMessage()).contains("returned []");
                    assertThat(failure.getSourceRequestUrl()).isEqualTo("/ajax/clone/catalogifs.ajax");
                    assertThat(failure.getSourceRequestParameters()).contains("\"itemid\":4997");
                });
    }

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
    void pricingSnapshotListingsCanBeQueriedAsExactConditionAndCompletenessComparables() {
        PricingFixture fixture = pricingFixture("pricing-exact-comparables");
        PricingCrawlWorkItem workItem = insertedWorkItem(fixture, CRAWL_AT);
        PricingSnapshot olderSealedSnapshot = pricingSnapshot(
                workItem.getPricingCrawlWorkItemId(),
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                SNAPSHOT_AT.minusHours(1)
        );
        olderSealedSnapshot.setItemConditionCode("N");
        olderSealedSnapshot.setCompletenessCode("S");
        pricingSnapshotMapper.insert(olderSealedSnapshot);

        PricingSnapshot latestSealedSnapshot = pricingSnapshot(
                workItem.getPricingCrawlWorkItemId(),
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                SNAPSHOT_AT
        );
        latestSealedSnapshot.setItemConditionCode("N");
        latestSealedSnapshot.setCompletenessCode("S");
        pricingSnapshotMapper.insert(latestSealedSnapshot);

        PricingSnapshot latestCompleteSnapshot = pricingSnapshot(
                workItem.getPricingCrawlWorkItemId(),
                fixture.listing().getMarketplaceListingId(),
                fixture.catalogItem().getExternalCatalogItemId(),
                2,
                SNAPSHOT_AT.plusHours(1)
        );
        latestCompleteSnapshot.setItemConditionCode("N");
        latestCompleteSnapshot.setCompletenessCode("C");
        pricingSnapshotMapper.insert(latestCompleteSnapshot);

        PricingSnapshotListing exactHigh = pricingSnapshotListing(latestSealedSnapshot.getPricingSnapshotId(), "exact-high");
        exactHigh.setItemConditionCode("N");
        exactHigh.setCompletenessCode("S");
        exactHigh.setUnitPrice(new BigDecimal("25.00"));
        pricingSnapshotListingMapper.insert(exactHigh);

        PricingSnapshotListing exactLow = pricingSnapshotListing(latestSealedSnapshot.getPricingSnapshotId(), "exact-low");
        exactLow.setItemConditionCode("N");
        exactLow.setCompletenessCode("S");
        exactLow.setUnitPrice(new BigDecimal("20.00"));
        pricingSnapshotListingMapper.insert(exactLow);

        PricingSnapshotListing completenessMismatch = pricingSnapshotListing(latestSealedSnapshot.getPricingSnapshotId(), "complete-mismatch");
        completenessMismatch.setItemConditionCode("N");
        completenessMismatch.setCompletenessCode("C");
        completenessMismatch.setUnitPrice(new BigDecimal("15.00"));
        pricingSnapshotListingMapper.insert(completenessMismatch);

        PricingSnapshotListing conditionMismatch = pricingSnapshotListing(latestSealedSnapshot.getPricingSnapshotId(), "condition-mismatch");
        conditionMismatch.setItemConditionCode("U");
        conditionMismatch.setCompletenessCode("S");
        conditionMismatch.setUnitPrice(new BigDecimal("10.00"));
        pricingSnapshotListingMapper.insert(conditionMismatch);

        assertThat(pricingSnapshotMapper.findLatestByMarketplaceListingIdAndConditionAndCompleteness(
                fixture.listing().getMarketplaceListingId(), "N", "S"
        )).map(PricingSnapshot::getPricingSnapshotId).contains(latestSealedSnapshot.getPricingSnapshotId());
        assertThat(pricingSnapshotMapper.findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
                fixture.catalogItem().getExternalCatalogItemId(), "N", "S"
        )).map(PricingSnapshot::getPricingSnapshotId).contains(latestSealedSnapshot.getPricingSnapshotId());
        assertThat(pricingSnapshotListingMapper.findByPricingSnapshotId(latestSealedSnapshot.getPricingSnapshotId())).hasSize(4);
        assertThat(pricingSnapshotListingMapper.findExactComparablesByPricingSnapshotId(latestSealedSnapshot.getPricingSnapshotId()))
                .extracting(PricingSnapshotListing::getExternalListingId)
                .containsExactly("exact-low", "exact-high");
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

    @Test
    void pricingDecisionReviewReturnsLatestDecisionWithListingContext() {
        PricingFixture decidedFixture = pricingFixture("pricing-review-decided");
        PricingSnapshot snapshot = insertedSnapshot(decidedFixture);
        PricingDecision olderDecision = pricingDecision(decidedFixture.listing().getMarketplaceListingId(), snapshot.getPricingSnapshotId());
        olderDecision.setReasonCode("NO_CURRENT_SNAPSHOT");
        olderDecision.setDecisionStatusCode("FAILED");
        olderDecision.setFinalPrice(null);
        pricingDecisionMapper.insert(olderDecision);

        PricingDecision latestDecision = pricingDecision(decidedFixture.listing().getMarketplaceListingId(), snapshot.getPricingSnapshotId());
        latestDecision.setReasonCode("MATCHED_LOWEST_COMPETITOR");
        latestDecision.setDecisionStatusCode("PROPOSED");
        latestDecision.setFinalPrice(new BigDecimal("199.99"));
        pricingDecisionMapper.insert(latestDecision);

        PricingFixture undecidedFixture = pricingFixture("pricing-review-undecided");

        Set<PricingDecisionReview> reviews = pricingDecisionMapper.findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
                2,
                "ACTIVE",
                10
        );

        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .filteredOn(review -> review.getMarketplaceListingId().equals(decidedFixture.listing().getMarketplaceListingId()))
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.getPricingDecisionId()).isEqualTo(latestDecision.getPricingDecisionId());
                    assertThat(review.getReasonCode()).isEqualTo("MATCHED_LOWEST_COMPETITOR");
                    assertThat(review.getDecisionStatusCode()).isEqualTo("PROPOSED");
                    assertThat(review.getFinalPrice()).isEqualByComparingTo("199.99");
                    assertThat(review.getCurrentUnitPrice()).isEqualByComparingTo(decidedFixture.listing().getUnitPrice());
                    assertThat(review.getCurrentCurrencyCode()).isEqualTo("USD");
                    assertThat(review.getDecisionCurrencyCode()).isEqualTo("USD");
                    assertThat(review.getCurrencyCode()).isEqualTo("USD");
                    assertThat(review.getExternalItemKey()).isEqualTo("pricing-review-decided");
                    assertThat(review.getSnapshotComparableCount()).isEqualTo(snapshot.getComparableCount());
                    assertThat(review.getSnapshotCapturedAt()).isEqualTo(snapshot.getCapturedAt());
                });
        assertThat(reviews)
                .filteredOn(review -> review.getMarketplaceListingId().equals(undecidedFixture.listing().getMarketplaceListingId()))
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.getPricingDecisionId()).isNull();
                    assertThat(review.getDecisionStatusCode()).isNull();
                    assertThat(review.getExternalItemKey()).isEqualTo("pricing-review-undecided");
                });
    }

    @Test
    void pricingDecisionApplyReadinessReviewReturnsLatestUnappliedProposedDecisionsOnly() {
        PricingFixture readyFixture = pricingFixture("pricing-apply-ready");
        PricingSnapshot readySnapshot = insertedSnapshot(readyFixture);
        PricingDecision failedDecision = pricingDecision(readyFixture.listing().getMarketplaceListingId(), readySnapshot.getPricingSnapshotId());
        failedDecision.setDecisionStatusCode("FAILED");
        failedDecision.setReasonCode("NO_EXACT_COMPARABLES");
        failedDecision.setFinalPrice(null);
        pricingDecisionMapper.insert(failedDecision);
        PricingDecision readyDecision = pricingDecision(readyFixture.listing().getMarketplaceListingId(), readySnapshot.getPricingSnapshotId());
        readyDecision.setDecisionStatusCode("PROPOSED");
        readyDecision.setReasonCode("MEAN_PLUS_STDDEV");
        readyDecision.setFinalPrice(new BigDecimal("212.25"));
        pricingDecisionMapper.insert(readyDecision);

        PricingFixture appliedFixture = pricingFixture("pricing-apply-applied");
        PricingSnapshot appliedSnapshot = insertedSnapshot(appliedFixture);
        PricingDecision appliedDecision = pricingDecision(appliedFixture.listing().getMarketplaceListingId(), appliedSnapshot.getPricingSnapshotId());
        appliedDecision.setDecisionStatusCode("PROPOSED");
        appliedDecision.setReasonCode("MATCHED_LOWEST_COMPETITOR");
        appliedDecision.setFinalPrice(new BigDecimal("190.00"));
        appliedDecision.setAppliedAt(SNAPSHOT_AT.plusMinutes(10));
        pricingDecisionMapper.insert(appliedDecision);

        PricingFixture failedFixture = pricingFixture("pricing-apply-failed");
        PricingSnapshot failedSnapshot = insertedSnapshot(failedFixture);
        PricingDecision latestFailedDecision = pricingDecision(failedFixture.listing().getMarketplaceListingId(), failedSnapshot.getPricingSnapshotId());
        latestFailedDecision.setDecisionStatusCode("FAILED");
        latestFailedDecision.setReasonCode("NO_CURRENT_SNAPSHOT");
        latestFailedDecision.setFinalPrice(null);
        pricingDecisionMapper.insert(latestFailedDecision);

        assertThat(pricingDecisionMapper.countLatestByDecisionStatusCode("PROPOSED")).isEqualTo(2);
        assertThat(pricingDecisionMapper.countLatestUnappliedByDecisionStatusCode("PROPOSED")).isOne();
        assertThat(pricingDecisionMapper.countLatestByDecisionStatusCode("FAILED")).isOne();

        Set<PricingDecisionReview> reviews = pricingDecisionMapper
                .findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
                        2,
                        "ACTIVE",
                        "PROPOSED",
                        10
                );

        assertThat(reviews)
                .extracting(PricingDecisionReview::getMarketplaceListingId)
                .containsExactly(readyFixture.listing().getMarketplaceListingId());
        assertThat(reviews)
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.getPricingDecisionId()).isEqualTo(readyDecision.getPricingDecisionId());
                    assertThat(review.getDecisionStatusCode()).isEqualTo("PROPOSED");
                    assertThat(review.getReasonCode()).isEqualTo("MEAN_PLUS_STDDEV");
                    assertThat(review.getFinalPrice()).isEqualByComparingTo("212.25");
                    assertThat(review.getAppliedAt()).isNull();
                    assertThat(review.getCurrentUnitPrice()).isEqualByComparingTo(readyFixture.listing().getUnitPrice());
                    assertThat(review.getCurrentCurrencyCode()).isEqualTo("USD");
                    assertThat(review.getDecisionCurrencyCode()).isEqualTo("USD");
                });
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
