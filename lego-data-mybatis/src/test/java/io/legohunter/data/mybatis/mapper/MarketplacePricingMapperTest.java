package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CanonicalItem;
import io.legohunter.data.dto.CrawlAttempt;
import io.legohunter.data.dto.CrawlTarget;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketListing;
import io.legohunter.data.dto.MarketSnapshot;
import io.legohunter.data.dto.Marketplace;
import io.legohunter.data.dto.MarketplaceItem;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingApplyReadiness;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.WantListItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The shared marketplace_pricing schema (HOMELAB-31), reached from the environment connection through
 * the {@code ${marketplacePricingSchema}} variable.
 */
@MapperIntegrationTest
class MarketplacePricingMapperTest extends MapperTestSupport {
    private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-11-09T12:00:00Z");

    @Autowired MarketplaceMapper marketplaceMapper;
    @Autowired MarketplaceItemMapper marketplaceItemMapper;
    @Autowired WantListItemMapper wantListItemMapper;
    @Autowired CrawlAttemptMapper crawlAttemptMapper;
    @Autowired MarketListingMapper marketListingMapper;

    @Test
    void marketplacesComeFromTheReferenceData() {
        assertThat(marketplaceMapper.findAll()).extracting(Marketplace::getMarketplaceCode).containsExactly("BRICKLINK", "EBAY");
        Marketplace bricklink = marketplaceMapper.findByMarketplaceCode("BRICKLINK").orElseThrow();
        assertThat(bricklink.getCrawlEnabled()).isTrue();
        assertThat(bricklink.getDefaultCurrencyCode()).isEqualTo("USD");
        assertThat(marketplaceMapper.findByMarketplaceCode("EBAY").orElseThrow().getCrawlEnabled()).isFalse();
        assertThat(marketplaceMapper.findByMarketplaceCode("NOPE")).isEmpty();
    }

    @Test
    void canonicalItemIsUniqueByTypeAndNumber() {
        CanonicalItem item = canonicalItem("S", "6390-1");

        assertThat(canonicalItemMapper.findByItemTypeCodeAndItemNumber("S", "6390-1")).contains(
                canonicalItemMapper.findByCanonicalItemId(item.getCanonicalItemId()).orElseThrow());
        assertThat(canonicalItemMapper.findByItemTypeCodeAndItemNumber("G", "6390-1")).isEmpty();
        assertThatThrownBy(() -> canonicalItem("S", "6390-1")).isInstanceOf(DuplicateKeyException.class);

        item.setItemName("Main Street (renamed)");
        assertThat(canonicalItemMapper.update(item)).isOne();
        assertThat(canonicalItemMapper.findByCanonicalItemId(item.getCanonicalItemId()).orElseThrow().getItemName())
                .isEqualTo("Main Street (renamed)");
    }

    @Test
    void marketplaceItemHoldsTheResolvedKeyOncePerMarketplace() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        MarketplaceItem marketplaceItem = MarketplaceItem.builder()
                .marketplaceCode("BRICKLINK")
                .canonicalItemId(item.getCanonicalItemId())
                .marketplaceItemKey("6390-1")
                .matchMethodCode("CATALOG_ID")
                .build();
        marketplaceItemMapper.insert(marketplaceItem);

        marketplaceItem.setMarketplaceUniqueKey("4997");
        marketplaceItem.setResolvedAt(NOW);
        assertThat(marketplaceItemMapper.update(marketplaceItem)).isOne();

        MarketplaceItem found = marketplaceItemMapper.findByMarketplaceCodeAndCanonicalItemId("BRICKLINK", item.getCanonicalItemId()).orElseThrow();
        assertThat(found.getMarketplaceItemId()).isEqualTo(marketplaceItem.getMarketplaceItemId());
        assertThat(found.getMarketplaceUniqueKey()).isEqualTo("4997");
        assertThat(found.getResolvedAt()).isNotNull();
        assertThat(marketplaceItemMapper.findByMarketplaceItemId(marketplaceItem.getMarketplaceItemId())).contains(found);
        assertThat(marketplaceItemMapper.findByMarketplaceCodeAndCanonicalItemId("EBAY", item.getCanonicalItemId())).isEmpty();

        MarketplaceItem duplicate = MarketplaceItem.builder()
                .marketplaceCode("BRICKLINK").canonicalItemId(item.getCanonicalItemId()).matchMethodCode("CATALOG_ID").build();
        assertThatThrownBy(() -> marketplaceItemMapper.insert(duplicate)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void wantListUpsertKeepsTheFirstWantedTimeAndEachEnvironmentPrunesOnlyItsOwnRows() {
        CanonicalItem mainStreet = canonicalItem("S", "6390-1");
        CanonicalItem harbor = canonicalItem("S", "6541-1");

        wantListItemMapper.upsert(want("sandbox", mainStreet, "N", "S", NOW.minusDays(10)));
        wantListItemMapper.upsert(want("sandbox", mainStreet, "N", "C", NOW.minusDays(10)));
        wantListItemMapper.upsert(want("sandbox", harbor, "U", "C", NOW.minusDays(10)));
        wantListItemMapper.upsert(want("dev", harbor, "U", "C", NOW.minusDays(10)));

        // Re-confirming moves last_confirmed_at only.
        WantListItem reconfirm = want("sandbox", mainStreet, "N", "S", NOW);
        reconfirm.setFirstWantedAt(NOW);
        wantListItemMapper.upsert(reconfirm);

        List<WantListItem> sandbox = wantListItemMapper.findByEnvironmentCode("sandbox");
        assertThat(sandbox).hasSize(3);
        assertThat(sandbox.get(0).getCompletenessCode()).isEqualTo("C");
        assertThat(sandbox.get(1).getCompletenessCode()).isEqualTo("S");
        assertThat(sandbox.get(1).getFirstWantedAt().toInstant()).isEqualTo(NOW.minusDays(10).toInstant());
        assertThat(sandbox.get(1).getLastConfirmedAt().toInstant()).isEqualTo(NOW.toInstant());

        assertThat(wantListItemMapper.deleteByEnvironmentCodeAndLastConfirmedAtBefore("sandbox", NOW.minusDays(1))).isEqualTo(2);
        assertThat(wantListItemMapper.findByEnvironmentCode("sandbox")).extracting(WantListItem::getCompletenessCode).containsExactly("S");
        assertThat(wantListItemMapper.findByEnvironmentCode("dev")).hasSize(1);
        assertThat(wantListItemMapper.findByCanonicalItemId(harbor.getCanonicalItemId()))
                .extracting(WantListItem::getEnvironmentCode).containsExactly("dev");

        assertThat(wantListItemMapper.delete(want("dev", harbor, "U", "C", NOW))).isOne();
        assertThat(wantListItemMapper.findByEnvironmentCode("dev")).isEmpty();
    }

    @Test
    void crawlTargetIsOneRowPerItemAndConditionAndIsClaimedOnlyOnce() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        CrawlTarget target = crawlTarget(item, "N", NOW.minusMinutes(5));
        crawlTargetMapper.insert(target);
        assertThat(crawlTargetMapper.findByCrawlTargetId(target.getCrawlTargetId()).orElseThrow().getTargetStatusCode())
                .isEqualTo(CrawlTargetMapper.STATUS_IDLE);
        crawlTargetMapper.insert(crawlTarget(item, "U", NOW.plusHours(1)));

        assertThatThrownBy(() -> crawlTargetMapper.insert(crawlTarget(item, "N", NOW))).isInstanceOf(DuplicateKeyException.class);

        assertThat(crawlTargetMapper.findDue("BRICKLINK", NOW, 10)).extracting(CrawlTarget::getQueryConditionCode).containsExactly("N");
        assertThat(crawlTargetMapper.findDue("EBAY", NOW, 10)).isEmpty();

        assertThat(crawlTargetMapper.claim(target.getCrawlTargetId(), "crawler-1", NOW, NOW.plusMinutes(10))).isOne();
        assertThat(crawlTargetMapper.claim(target.getCrawlTargetId(), "crawler-2", NOW, NOW.plusMinutes(10))).isZero();
        assertThat(crawlTargetMapper.findDue("BRICKLINK", NOW.plusMinutes(5), 10)).isEmpty();

        // A crawler that stopped holding a claim: the claim expires and the target is due again.
        assertThat(crawlTargetMapper.findDue("BRICKLINK", NOW.plusMinutes(11), 10)).hasSize(1);
        assertThat(crawlTargetMapper.claim(target.getCrawlTargetId(), "crawler-2", NOW.plusMinutes(11), NOW.plusMinutes(21))).isOne();

        CrawlTarget claimed = crawlTargetMapper.findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
                "BRICKLINK", item.getCanonicalItemId(), "N").orElseThrow();
        assertThat(claimed.getClaimedBy()).isEqualTo("crawler-2");

        claimed.setTargetStatusCode(CrawlTargetMapper.STATUS_IDLE);
        claimed.setClaimedAt(null);
        claimed.setClaimExpiresAt(null);
        claimed.setClaimedBy(null);
        claimed.setLastAttemptAt(NOW.plusMinutes(12));
        claimed.setConsecutiveFailureCount(1);
        claimed.setLastErrorMessage("maintenance redirect");
        claimed.setNextDueAt(NOW.plusHours(2));
        assertThat(crawlTargetMapper.update(claimed)).isOne();
        CrawlTarget failed = crawlTargetMapper.findByCrawlTargetId(target.getCrawlTargetId()).orElseThrow();
        assertThat(failed.getConsecutiveFailureCount()).isOne();
        assertThat(failed.getLastErrorMessage()).isEqualTo("maintenance redirect");
        assertThat(failed.getClaimedBy()).isNull();
    }

    @Test
    void schedulingFindsNewWantsDisablesUnwantedTargetsAndFindsWantsComingBack() {
        CanonicalItem mainStreet = canonicalItem("S", "6390-1");
        CanonicalItem harbor = canonicalItem("S", "6541-1");
        wantListItemMapper.upsert(want("sandbox", mainStreet, "N", "S", NOW));
        wantListItemMapper.upsert(want("dev", mainStreet, "N", "C", NOW));   // same item + N: one target
        wantListItemMapper.upsert(want("sandbox", harbor, "U", "C", NOW));

        assertThat(crawlTargetMapper.findWantedWithoutTarget("BRICKLINK"))
                .extracting(CrawlTarget::getCanonicalItemId, CrawlTarget::getQueryConditionCode)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(mainStreet.getCanonicalItemId(), "N"),
                        org.assertj.core.groups.Tuple.tuple(harbor.getCanonicalItemId(), "U"));

        CrawlTarget mainStreetNew = crawlTarget(mainStreet, "N", NOW);
        crawlTargetMapper.insert(mainStreetNew);
        CrawlTarget harborUsed = crawlTarget(harbor, "U", NOW);
        crawlTargetMapper.insert(harborUsed);
        assertThat(crawlTargetMapper.findWantedWithoutTarget("BRICKLINK")).isEmpty();
        assertThat(crawlTargetMapper.findWantedWithoutTarget("EBAY")).hasSize(2);

        // Harbor is sold everywhere: its target is disabled; Main Street keeps its target.
        assertThat(wantListItemMapper.delete(want("sandbox", harbor, "U", "C", NOW))).isOne();
        assertThat(crawlTargetMapper.disableUnwanted("BRICKLINK")).isOne();
        assertThat(crawlTargetMapper.findByCrawlTargetId(harborUsed.getCrawlTargetId()).orElseThrow().getTargetStatusCode())
                .isEqualTo(CrawlTargetMapper.STATUS_DISABLED);
        assertThat(crawlTargetMapper.findByCrawlTargetId(mainStreetNew.getCrawlTargetId()).orElseThrow().getTargetStatusCode())
                .isEqualTo(CrawlTargetMapper.STATUS_IDLE);
        assertThat(crawlTargetMapper.disableUnwanted("BRICKLINK")).isZero();
        assertThat(crawlTargetMapper.findDisabledWanted("BRICKLINK")).isEmpty();

        // Wanted again: the disabled target is found, not a new one.
        wantListItemMapper.upsert(want("dev", harbor, "U", "C", NOW));
        assertThat(crawlTargetMapper.findWantedWithoutTarget("BRICKLINK")).isEmpty();
        assertThat(crawlTargetMapper.findDisabledWanted("BRICKLINK")).extracting(CrawlTarget::getCrawlTargetId)
                .containsExactly(harborUsed.getCrawlTargetId());
    }

    @Test
    void queueCountsAndRecentFailuresFeedTheMonitoring() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        CanonicalItem other = canonicalItem("S", "6541-1");
        CrawlTarget due = crawlTarget(item, "N", NOW.minusMinutes(1));
        crawlTargetMapper.insert(due);
        CrawlTarget failing = crawlTarget(item, "U", NOW.plusHours(6));
        failing.setConsecutiveFailureCount(2);
        crawlTargetMapper.insert(failing);
        CrawlTarget disabled = crawlTarget(other, "U", NOW.minusDays(1));
        disabled.setTargetStatusCode(CrawlTargetMapper.STATUS_DISABLED);
        disabled.setConsecutiveFailureCount(1);
        crawlTargetMapper.insert(disabled);

        assertThat(crawlTargetMapper.countByTargetStatusCode("BRICKLINK", "IDLE")).isEqualTo(2);
        assertThat(crawlTargetMapper.countByTargetStatusCode("BRICKLINK", "DISABLED")).isOne();
        assertThat(crawlTargetMapper.countDue("BRICKLINK", NOW)).isOne();
        assertThat(crawlTargetMapper.countFailing("BRICKLINK")).isOne();

        crawlAttemptMapper.insert(CrawlAttempt.builder().crawlTargetId(failing.getCrawlTargetId()).startedAt(NOW.minusHours(2))
                .outcomeCode("FAILED_HTTP").errorMessage("maintenance redirect").build());
        crawlAttemptMapper.insert(CrawlAttempt.builder().crawlTargetId(failing.getCrawlTargetId()).startedAt(NOW.minusHours(1))
                .outcomeCode("FAILED_PARSE").build());
        crawlAttemptMapper.insert(CrawlAttempt.builder().crawlTargetId(due.getCrawlTargetId()).startedAt(NOW)
                .outcomeCode("SUCCEEDED").build());
        assertThat(crawlAttemptMapper.findRecentFailures("BRICKLINK", 10)).extracting(CrawlAttempt::getOutcomeCode)
                .containsExactly("FAILED_PARSE", "FAILED_HTTP");
        assertThat(crawlAttemptMapper.findRecentFailures("BRICKLINK", 1)).hasSize(1);
        assertThat(crawlAttemptMapper.findRecentFailures("EBAY", 10)).isEmpty();
    }

    @Test
    void everySuccessfulCrawlAddsASnapshotSoThePriceHistoryIsKept() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        CrawlTarget target = crawlTarget(item, "N", NOW);
        crawlTargetMapper.insert(target);

        MarketSnapshot week1 = snapshot(target, item, NOW.minusWeeks(2));
        MarketSnapshot week2 = snapshot(target, item, NOW.minusWeeks(1));
        MarketSnapshot week3 = snapshot(target, item, NOW);
        assertThat(marketSnapshotMapper.findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
                "BRICKLINK", item.getCanonicalItemId(), "N").orElseThrow().getMarketSnapshotId())
                .isEqualTo(week3.getMarketSnapshotId());
        assertThat(marketSnapshotMapper.findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
                item.getCanonicalItemId(), "N", NOW.minusWeeks(3), NOW))
                .extracting(MarketSnapshot::getMarketSnapshotId)
                .containsExactly(week1.getMarketSnapshotId(), week2.getMarketSnapshotId());
        assertThat(marketSnapshotMapper.findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
                item.getCanonicalItemId(), "U", NOW.minusWeeks(3), NOW.plusDays(1))).isEmpty();
        assertThat(marketSnapshotMapper.findByMarketSnapshotId(week1.getMarketSnapshotId()).orElseThrow().getRequestParameters())
                .contains("\"cond\":\"N\"");
    }

    @Test
    void comparablesAreTheLotsOfTheSameSubConditionCheapestFirst() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        CrawlTarget target = crawlTarget(item, "N", NOW);
        crawlTargetMapper.insert(target);
        MarketSnapshot snapshot = snapshot(target, item, NOW);

        MarketListing sealedHigh = listing(snapshot, "1", "N", "S", "450.00");
        MarketListing sealedLow = listing(snapshot, "2", "N", "S", "399.99");
        MarketListing complete = listing(snapshot, "3", "N", "C", "250.00");
        complete.setShippingPrice(new BigDecimal("20.00"));
        complete.setTotalPrice(new BigDecimal("270.00"));
        complete.setNativePrice(new BigDecimal("231.00"));
        complete.setNativeCurrencyCode("EUR");
        marketListingMapper.insert(sealedHigh);
        marketListingMapper.insert(sealedLow);
        marketListingMapper.insert(complete);

        assertThat(marketListingMapper.findComparablesByMarketSnapshotId(snapshot.getMarketSnapshotId(), "N", "S"))
                .extracting(MarketListing::getExternalListingId).containsExactly("2", "1");
        assertThat(marketListingMapper.findByMarketSnapshotId(snapshot.getMarketSnapshotId())).hasSize(3);

        MarketListing stored = marketListingMapper.findByMarketListingId(complete.getMarketListingId()).orElseThrow();
        assertThat(stored.getListingFormatCode()).isEqualTo("FIXED_PRICE");
        assertThat(stored.getTotalPrice()).isEqualByComparingTo("270.00");
        assertThat(stored.getNativeCurrencyCode()).isEqualTo("EUR");
        assertThat(stored.getMatchConfidence()).isEqualByComparingTo("1.0000");
        assertThat(stored.getRawCompletenessCode()).isEqualTo("C");
    }

    @Test
    void crawlAttemptsLogEveryOutcome() {
        CanonicalItem item = canonicalItem("S", "6390-1");
        CrawlTarget target = crawlTarget(item, "N", NOW);
        crawlTargetMapper.insert(target);

        CrawlAttempt failed = CrawlAttempt.builder().crawlTargetId(target.getCrawlTargetId()).startedAt(NOW.minusHours(1))
                .outcomeCode("RUNNING").build();
        crawlAttemptMapper.insert(failed);
        failed.setFinishedAt(NOW.minusHours(1).plusSeconds(3));
        failed.setOutcomeCode("FAILED_HTTP");
        failed.setErrorMessage("maintenance redirect");
        assertThat(crawlAttemptMapper.finish(failed)).isOne();

        MarketSnapshot snapshot = snapshot(target, item, NOW);
        CrawlAttempt succeeded = CrawlAttempt.builder().crawlTargetId(target.getCrawlTargetId()).startedAt(NOW)
                .finishedAt(NOW.plusSeconds(2)).outcomeCode("SUCCEEDED").marketSnapshotId(snapshot.getMarketSnapshotId()).build();
        crawlAttemptMapper.insert(succeeded);

        assertThat(crawlAttemptMapper.findLatestByCrawlTargetId(target.getCrawlTargetId(), 10))
                .extracting(CrawlAttempt::getOutcomeCode).containsExactly("SUCCEEDED", "FAILED_HTTP");
        assertThat(crawlAttemptMapper.findLatestByCrawlTargetId(target.getCrawlTargetId(), 1)).hasSize(1);
        assertThat(crawlAttemptMapper.findByCrawlAttemptId(failed.getCrawlAttemptId()).orElseThrow().getErrorMessage())
                .isEqualTo("maintenance redirect");
    }

    @Test
    void decisionAndReadinessKeepTheMarketSnapshotTheyCameFrom() {
        seedExternalCatalog();
        ExternalCatalogItem catalogItem = insertExternalCatalogItem("market-snapshot-ref");
        ItemInventory inventory = insertItemInventory("inventory-market-snapshot-ref");
        MarketplaceListing listing = marketplaceListing(inventory.getItemInventoryId(), catalogItem.getExternalCatalogItemId(), 2,
                "listing-market-snapshot-ref");
        marketplaceListingMapper.insert(listing);

        PricingDecision decision = pricingDecision(listing.getMarketplaceListingId(), null);
        decision.setMarketSnapshotId(42L);
        decision.setMarketSnapshotCapturedAt(NOW);
        pricingDecisionMapper.insert(decision);
        PricingDecision storedDecision = pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()).orElseThrow();
        assertThat(storedDecision.getMarketSnapshotId()).isEqualTo(42L);
        assertThat(storedDecision.getMarketSnapshotCapturedAt().toInstant()).isEqualTo(NOW.toInstant());

        storedDecision.setMarketSnapshotId(43L);
        assertThat(pricingDecisionMapper.update(storedDecision)).isOne();
        assertThat(pricingDecisionMapper.findByPricingDecisionId(decision.getPricingDecisionId()).orElseThrow().getMarketSnapshotId())
                .isEqualTo(43L);

        PricingApplyReadiness readiness = PricingApplyReadiness.builder()
                .marketplaceListingId(listing.getMarketplaceListingId())
                .pricingDecisionId(decision.getPricingDecisionId())
                .marketSnapshotId(43L)
                .marketSnapshotCapturedAt(NOW)
                .readinessStatusCode("READY_TO_APPLY")
                .currencyCode("USD")
                .evaluatedAt(NOW)
                .build();
        pricingApplyReadinessMapper.insert(readiness);
        PricingApplyReadiness storedReadiness = pricingApplyReadinessMapper
                .findByPricingApplyReadinessId(readiness.getPricingApplyReadinessId()).orElseThrow();
        assertThat(storedReadiness.getMarketSnapshotId()).isEqualTo(43L);
        assertThat(storedReadiness.getMarketSnapshotCapturedAt().toInstant()).isEqualTo(NOW.toInstant());
    }

    private CanonicalItem canonicalItem(String type, String number) {
        CanonicalItem item = CanonicalItem.builder().itemTypeCode(type).itemNumber(number).itemName("Item " + number).build();
        canonicalItemMapper.insert(item);
        return item;
    }

    private WantListItem want(String environment, CanonicalItem item, String condition, String completeness, ZonedDateTime at) {
        return WantListItem.builder()
                .environmentCode(environment)
                .canonicalItemId(item.getCanonicalItemId())
                .conditionCode(condition)
                .completenessCode(completeness)
                .firstWantedAt(at)
                .lastConfirmedAt(at)
                .build();
    }

    private CrawlTarget crawlTarget(CanonicalItem item, String condition, ZonedDateTime nextDueAt) {
        return CrawlTarget.builder()
                .marketplaceCode("BRICKLINK")
                .canonicalItemId(item.getCanonicalItemId())
                .queryConditionCode(condition)
                .nextDueAt(nextDueAt)
                .build();
    }

    private MarketSnapshot snapshot(CrawlTarget target, CanonicalItem item, ZonedDateTime capturedAt) {
        MarketSnapshot snapshot = MarketSnapshot.builder()
                .crawlTargetId(target.getCrawlTargetId())
                .marketplaceCode("BRICKLINK")
                .canonicalItemId(item.getCanonicalItemId())
                .queryConditionCode(target.getQueryConditionCode())
                .marketplaceItemKey(item.getItemNumber())
                .marketplaceUniqueKey("4997")
                .requestUrl("/ajax/clone/catalogifs.ajax")
                .requestParameters("{\"iconly\":0,\"itemid\":4997,\"rpp\":500,\"cond\":\"" + target.getQueryConditionCode() + "\"}")
                .rawPayloadHash("hash")
                .listingCount(3)
                .capturedAt(capturedAt)
                .build();
        marketSnapshotMapper.insert(snapshot);
        return snapshot;
    }

    private MarketListing listing(MarketSnapshot snapshot, String externalListingId, String condition, String completeness, String price) {
        return MarketListing.builder()
                .marketSnapshotId(snapshot.getMarketSnapshotId())
                .externalListingId(externalListingId)
                .sellerKey("seller-" + externalListingId)
                .sellerName("Store " + externalListingId)
                .sellerCountryCode("US")
                .sellerFeedbackScore(100)
                .rawConditionCode(condition)
                .rawCompletenessCode(completeness)
                .conditionCode(condition)
                .completenessCode(completeness)
                .matchConfidence(new BigDecimal("1.0000"))
                .quantityAvailable(1)
                .itemPrice(new BigDecimal(price))
                .currencyCode("USD")
                .description("Lot " + externalListingId)
                .sourceListingPayload("{}")
                .build();
    }
}
