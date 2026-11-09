package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.EbayListingItemSpecific;
import io.legohunter.data.dto.EbayMarketplaceListing;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.MarketSnapshot;
import io.legohunter.data.dto.CrawlTarget;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.dto.PricingDataQualityGap;
import io.legohunter.data.dto.WantedCatalogItem;
import io.legohunter.data.dto.PricingDataQualitySummary;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingSnapshot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class MarketplaceListingMapperTest extends MapperTestSupport {
    private static final ZonedDateTime CRAWL_AT = ZonedDateTime.parse("2026-06-18T14:00:00Z");


    @Test
    void marketplaceListingSupportsCrudAndExternalListingLookup() {
        ListingFixture fixture = listingFixture("listing-1");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "MARKETPLACE_LISTING"));
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "PRICE_GUIDE"));
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), 2, "BL-1");

        marketplaceListingMapper.insert(listing);
        listing.setTitle("Updated listing");
        listing.setUnitPrice(new BigDecimal("24.99"));
        marketplaceListingMapper.update(listing);

        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getTitle()).isEqualTo("Updated listing");
                    assertThat(found.getUnitPrice()).isEqualByComparingTo("24.99");
                    assertHydratedCatalogItem(found);
                });
        assertThat(marketplaceListingMapper.findByListingExternalServiceIdAndExternalListingId(2, "BL-1")).isPresent();
        assertThat(marketplaceListingMapper.findByItemInventoryId(fixture.inventory().getItemInventoryId()))
                .hasSize(1)
                .first()
                .satisfies(this::assertHydratedCatalogItem);
        assertThat(marketplaceListingMapper.findByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .hasSize(1)
                .first()
                .satisfies(found -> assertThat(found.getExternalListingId()).isEqualTo("BL-1"));
        assertThat(marketplaceListingMapper.findAll()).hasSize(1);

        listing.setListingStatusCode("ENDED");
        marketplaceListingMapper.upsert(listing);
        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getListingStatusCode()).isEqualTo("ENDED"));

        assertThat(marketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId())).isEmpty();
    }

    @Test
    void pricingDecisionCandidatesSkipNonFixedListingsWithoutConditionData() {
        ListingFixture unpriceableFixture = listingFixture("listing-unpriceable");
        unpriceableFixture.inventory().setNewOrUsed(null);
        unpriceableFixture.inventory().setCompleteness(null);
        itemInventoryMapper.update(unpriceableFixture.inventory());
        MarketplaceListing unpriceableListing = marketplaceListing(unpriceableFixture.inventory().getItemInventoryId(), unpriceableFixture.item().getExternalCatalogItemId(), 2, "BL-UNPRICEABLE");
        unpriceableListing.setFixedPrice(false);
        marketplaceListingMapper.insert(unpriceableListing);

        ListingFixture priceableFixture = listingFixture("listing-priceable");
        MarketplaceListing priceableListing = marketplaceListing(priceableFixture.inventory().getItemInventoryId(), priceableFixture.item().getExternalCatalogItemId(), 2, "BL-PRICEABLE");
        priceableListing.setFixedPrice(false);
        marketplaceListingMapper.insert(priceableListing);

        ListingFixture fixedPriceFixture = listingFixture("listing-fixed");
        fixedPriceFixture.inventory().setNewOrUsed(null);
        fixedPriceFixture.inventory().setCompleteness(null);
        itemInventoryMapper.update(fixedPriceFixture.inventory());
        MarketplaceListing fixedPriceListing = marketplaceListing(fixedPriceFixture.inventory().getItemInventoryId(), fixedPriceFixture.item().getExternalCatalogItemId(), 2, "BL-FIXED");
        fixedPriceListing.setFixedPrice(true);
        marketplaceListingMapper.insert(fixedPriceListing);

        ListingFixture draftFixture = listingFixture("listing-draft-priceable");
        MarketplaceListing draftListing = marketplaceListing(draftFixture.inventory().getItemInventoryId(), draftFixture.item().getExternalCatalogItemId(), 2, "BL-DRAFT-PRICEABLE");
        draftListing.setListingStatusCode("DRAFT");
        draftListing.setFixedPrice(false);
        marketplaceListingMapper.insert(draftListing);

        assertThat(marketplaceListingMapper.findByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-UNPRICEABLE", "BL-PRICEABLE", "BL-FIXED");
        assertThat(marketplaceListingMapper.findByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ACTIVE", "DRAFT"), 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-UNPRICEABLE", "BL-PRICEABLE", "BL-FIXED", "BL-DRAFT-PRICEABLE");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-PRICEABLE", "BL-FIXED");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ACTIVE", "DRAFT"), 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-PRICEABLE", "BL-FIXED", "BL-DRAFT-PRICEABLE");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 1))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-PRICEABLE");
    }

    @Test
    void pricingDecisionCandidatesCanRequireMatchingCurrentSnapshot() {
        ListingFixture noSnapshotFixture = listingFixture("listing-no-snapshot");
        MarketplaceListing noSnapshotListing = marketplaceListing(noSnapshotFixture.inventory().getItemInventoryId(), noSnapshotFixture.item().getExternalCatalogItemId(), 2, "BL-NO-SNAPSHOT");
        noSnapshotListing.setFixedPrice(false);
        marketplaceListingMapper.insert(noSnapshotListing);

        ListingFixture snapshotFixture = listingFixture("listing-with-snapshot");
        MarketplaceListing snapshotListing = marketplaceListing(snapshotFixture.inventory().getItemInventoryId(), snapshotFixture.item().getExternalCatalogItemId(), 2, "BL-WITH-SNAPSHOT");
        snapshotListing.setFixedPrice(false);
        marketplaceListingMapper.insert(snapshotListing);
        insertMarketSnapshot(snapshotFixture.item(), "U", CRAWL_AT);

        ListingFixture fixedPriceFixture = listingFixture("listing-fixed-without-snapshot");
        fixedPriceFixture.inventory().setNewOrUsed(null);
        fixedPriceFixture.inventory().setCompleteness(null);
        itemInventoryMapper.update(fixedPriceFixture.inventory());
        MarketplaceListing fixedPriceListing = marketplaceListing(fixedPriceFixture.inventory().getItemInventoryId(), fixedPriceFixture.item().getExternalCatalogItemId(), 2, "BL-FIXED-NO-SNAPSHOT");
        fixedPriceListing.setFixedPrice(true);
        marketplaceListingMapper.insert(fixedPriceListing);

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-NO-SNAPSHOT", "BL-WITH-SNAPSHOT", "BL-FIXED-NO-SNAPSHOT");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-WITH-SNAPSHOT", "BL-FIXED-NO-SNAPSHOT");
    }

    @Test
    void pricingDecisionCandidatesSkipAlreadyDecidedListingsUntilNewMatchingSnapshotExists() {
        ListingFixture fixture = listingFixture("listing-decision-dedupe");
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), 2, "BL-DEDUPE");
        listing.setFixedPrice(false);
        marketplaceListingMapper.insert(listing);
        MarketSnapshot firstSnapshot = insertMarketSnapshot(fixture.item(), "U", CRAWL_AT);
        // A snapshot of the other condition never makes a used listing a candidate.
        insertMarketSnapshot(fixture.item(), "N", CRAWL_AT.plusHours(2));

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-DEDUPE");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-DEDUPE");

        PricingDecision decision = pricingDecision(listing.getMarketplaceListingId(), null);
        decision.setMarketSnapshotId(firstSnapshot.getMarketSnapshotId());
        decision.setMarketSnapshotCapturedAt(firstSnapshot.getCapturedAt());
        pricingDecisionMapper.insert(decision);

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .isEmpty();
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .isEmpty();

        insertMarketSnapshot(fixture.item(), "U", CRAWL_AT.plusHours(1));

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-DEDUPE");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-DEDUPE");
    }

    @Test
    void pricingDecisionCandidatesSkipFixedPriceListingsUntilCurrentPriceChanges() {
        ListingFixture fixture = listingFixture("listing-fixed-dedupe");
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), 2, "BL-FIXED-DEDUPE");
        listing.setFixedPrice(true);
        listing.setUnitPrice(new BigDecimal("25.00"));
        marketplaceListingMapper.insert(listing);
        PricingSnapshot snapshot = pricingSnapshot(
                null,
                listing.getMarketplaceListingId(),
                fixture.item().getExternalCatalogItemId(),
                2,
                CRAWL_AT
        );
        snapshot.setItemConditionCode("U");
        snapshot.setCompletenessCode("C");
        pricingSnapshotMapper.insert(snapshot);

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-FIXED-DEDUPE");

        PricingDecision fixedPriceDecision = pricingDecision(listing.getMarketplaceListingId(), null);
        fixedPriceDecision.setDecisionStatusCode("SKIPPED");
        fixedPriceDecision.setReasonCode("FIXED_PRICE_OVERRIDE");
        fixedPriceDecision.setComputedPrice(null);
        fixedPriceDecision.setFinalPrice(new BigDecimal("25.00"));
        fixedPriceDecision.setComparableCount(0);
        pricingDecisionMapper.insert(fixedPriceDecision);

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .isEmpty();
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .isEmpty();

        listing.setUnitPrice(new BigDecimal("30.00"));
        marketplaceListingMapper.update(listing);

        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-FIXED-DEDUPE");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactly("BL-FIXED-DEDUPE");
    }

    @Test
    void pricingCrawlSchedulingCandidatesSkipOpenAndFutureScheduledWork() {
        ListingFixture availableFixture = listingFixture("crawl-available");
        MarketplaceListing availableListing = marketplaceListing(
                availableFixture.inventory().getItemInventoryId(),
                availableFixture.item().getExternalCatalogItemId(),
                2,
                "BL-CRAWL-AVAILABLE"
        );
        marketplaceListingMapper.insert(availableListing);

        ListingFixture pendingFixture = listingFixture("crawl-pending");
        MarketplaceListing pendingListing = marketplaceListing(
                pendingFixture.inventory().getItemInventoryId(),
                pendingFixture.item().getExternalCatalogItemId(),
                2,
                "BL-CRAWL-PENDING"
        );
        marketplaceListingMapper.insert(pendingListing);
        pricingCrawlWorkItemMapper.insert(pricingCrawlWorkItem(
                pendingListing.getMarketplaceListingId(),
                pendingFixture.item().getExternalCatalogItemId(),
                2,
                CRAWL_AT.minusMinutes(1)
        ));

        ListingFixture futureFixture = listingFixture("crawl-future");
        MarketplaceListing futureListing = marketplaceListing(
                futureFixture.inventory().getItemInventoryId(),
                futureFixture.item().getExternalCatalogItemId(),
                2,
                "BL-CRAWL-FUTURE"
        );
        marketplaceListingMapper.insert(futureListing);
        PricingCrawlWorkItem futureWorkItem = pricingCrawlWorkItem(
                futureListing.getMarketplaceListingId(),
                futureFixture.item().getExternalCatalogItemId(),
                2,
                CRAWL_AT.plusHours(1)
        );
        futureWorkItem.setWorkStatusCode("SUCCEEDED");
        pricingCrawlWorkItemMapper.insert(futureWorkItem);

        ListingFixture dueFixture = listingFixture("crawl-due");
        MarketplaceListing dueListing = marketplaceListing(
                dueFixture.inventory().getItemInventoryId(),
                dueFixture.item().getExternalCatalogItemId(),
                2,
                "BL-CRAWL-DUE"
        );
        marketplaceListingMapper.insert(dueListing);
        PricingCrawlWorkItem dueWorkItem = pricingCrawlWorkItem(
                dueListing.getMarketplaceListingId(),
                dueFixture.item().getExternalCatalogItemId(),
                2,
                CRAWL_AT.minusHours(1)
        );
        dueWorkItem.setWorkStatusCode("SUCCEEDED");
        pricingCrawlWorkItemMapper.insert(dueWorkItem);

        ListingFixture draftFixture = listingFixture("crawl-draft");
        MarketplaceListing draftListing = marketplaceListing(
                draftFixture.inventory().getItemInventoryId(),
                draftFixture.item().getExternalCatalogItemId(),
                2,
                "BL-CRAWL-DRAFT"
        );
        draftListing.setListingStatusCode("DRAFT");
        marketplaceListingMapper.insert(draftListing);

        assertThat(marketplaceListingMapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                2,
                "ACTIVE",
                "PENDING",
                "CLAIMED",
                CRAWL_AT,
                10
        )).extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-CRAWL-AVAILABLE", "BL-CRAWL-DUE");
        assertThat(marketplaceListingMapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCodes(
                2,
                Set.of("ACTIVE", "DRAFT"),
                "PENDING",
                "CLAIMED",
                CRAWL_AT,
                10
        )).extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-CRAWL-AVAILABLE", "BL-CRAWL-DUE", "BL-CRAWL-DRAFT");
    }

    @Test
    void pricingCrawlSchedulingCandidatesSkipListingsWithoutPriceableCondition() {
        insertedListingWithCondition("crawl-priceable", "BL-CRAWL-PRICEABLE", "USED", "COMPLETE");
        insertedListingWithCondition("crawl-short-codes", "BL-CRAWL-SHORT-CODES", " n ", "S");
        insertedListingWithCondition("crawl-no-condition", "BL-CRAWL-NO-CONDITION", null, "COMPLETE");
        insertedListingWithCondition("crawl-blank-condition", "BL-CRAWL-BLANK-CONDITION", "  ", "COMPLETE");
        insertedListingWithCondition("crawl-unknown-condition", "BL-CRAWL-UNKNOWN-CONDITION", "MINT", "COMPLETE");
        insertedListingWithCondition("crawl-no-completeness", "BL-CRAWL-NO-COMPLETENESS", "NEW", null);
        insertedListingWithCondition("crawl-blank-completeness", "BL-CRAWL-BLANK-COMPLETENESS", "NEW", " ");

        assertThat(marketplaceListingMapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                2,
                "ACTIVE",
                "PENDING",
                "CLAIMED",
                CRAWL_AT,
                10
        )).extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-CRAWL-PRICEABLE", "BL-CRAWL-SHORT-CODES");
        assertThat(marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(2, "ACTIVE", 10))
                .extracting(MarketplaceListing::getExternalListingId)
                .containsExactlyInAnyOrder("BL-CRAWL-PRICEABLE", "BL-CRAWL-SHORT-CODES");
    }

    @Test
    void wantedCatalogItemsGroupPriceableListingsByItemConditionAndCompleteness() {
        insertedListingWithCondition("want-a", "BL-WANT-A1", "USED", "COMPLETE");
        MarketplaceListing sealed = insertedListingWithCondition("want-b", "BL-WANT-B", "NEW", "SEALED");
        insertedListingWithCondition("want-c", "BL-WANT-C", "MINT", "COMPLETE");          // unrecognised condition
        MarketplaceListing ended = insertedListingWithCondition("want-d", "BL-WANT-D", "USED", "COMPLETE");
        ended.setListingStatusCode("ENDED");
        marketplaceListingMapper.update(ended);

        List<WantedCatalogItem> wanted = marketplaceListingMapper.findWantedCatalogItemsByListingExternalServiceIdAndListingStatusCodes(
                2, Set.of("ACTIVE", "DRAFT"));

        assertThat(wanted).extracting(WantedCatalogItem::getItemNumber, WantedCatalogItem::getConditionCode,
                        WantedCatalogItem::getCompletenessCode, WantedCatalogItem::getListingCount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("want-a", "U", "C", 1),
                        org.assertj.core.groups.Tuple.tuple("want-b", "N", "S", 1));
        assertThat(wanted).allSatisfy(item -> {
            assertThat(item.getItemTypeCode()).isEqualTo("SET");
            assertThat(item.getItemName()).isEqualTo("Catalog item");
        });
        assertThat(sealed.getListingStatusCode()).isEqualTo("ACTIVE");
        assertThat(marketplaceListingMapper.findWantedCatalogItemsByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ENDED")))
                .extracting(WantedCatalogItem::getItemNumber).containsExactly("want-d");
    }

    @Test
    void pricingDataQualityGapsFlagEachMissingField() {
        seedConditionGrade(8, "MS", "Missing");
        insertedListingWithCondition("dq-clean", "BL-DQ-CLEAN", "USED", "COMPLETE");

        MarketplaceListing missingCondition = insertedListingWithCondition("dq-condition", "BL-DQ-CONDITION", "MINT", null);

        ListingFixture newWithoutGrades = listingFixture("dq-new-no-grades");
        newWithoutGrades.inventory().setNewOrUsed("NEW");
        newWithoutGrades.inventory().setCompleteness("SEALED");
        newWithoutGrades.inventory().setBoxConditionId(null);
        newWithoutGrades.inventory().setInstructionsConditionId(null);
        itemInventoryMapper.update(newWithoutGrades.inventory());
        marketplaceListingMapper.insert(marketplaceListing(newWithoutGrades.inventory().getItemInventoryId(), newWithoutGrades.item().getExternalCatalogItemId(), 2, "BL-DQ-NEW-NO-GRADES"));

        ListingFixture usedWithoutGrades = listingFixture("dq-used-no-grades");
        usedWithoutGrades.inventory().setBoxConditionId(null);
        usedWithoutGrades.inventory().setInstructionsConditionId(null);
        itemInventoryMapper.update(usedWithoutGrades.inventory());
        MarketplaceListing usedListing = marketplaceListing(usedWithoutGrades.inventory().getItemInventoryId(), usedWithoutGrades.item().getExternalCatalogItemId(), 2, "BL-DQ-USED-NO-GRADES");
        marketplaceListingMapper.insert(usedListing);
        // The shared crawl target for this item and condition has failed once since its last success.
        crawlTargetMapper.insert(CrawlTarget.builder()
                .marketplaceCode("BRICKLINK")
                .canonicalItemId(canonicalItemFor(usedWithoutGrades.item()).getCanonicalItemId())
                .queryConditionCode("U")
                .nextDueAt(CRAWL_AT)
                .consecutiveFailureCount(1)
                .lastErrorMessage("No exact BrickLink catalog item match")
                .build());

        ListingFixture usedGradedMissing = listingFixture("dq-used-graded-missing");
        usedGradedMissing.inventory().setBoxConditionId(8);
        usedGradedMissing.inventory().setInstructionsConditionId(8);
        itemInventoryMapper.update(usedGradedMissing.inventory());
        marketplaceListingMapper.insert(marketplaceListing(usedGradedMissing.inventory().getItemInventoryId(), usedGradedMissing.item().getExternalCatalogItemId(), 2, "BL-DQ-USED-GRADED-MISSING"));

        ListingFixture catalogGaps = listingFixture("dq-catalog");
        catalogGaps.item().setItemTypeCode(" ");
        catalogGaps.item().setExternalUniqueKey(null);
        externalCatalogItemMapper.update(catalogGaps.item());
        marketplaceListingMapper.insert(marketplaceListing(catalogGaps.inventory().getItemInventoryId(), catalogGaps.item().getExternalCatalogItemId(), 2, "BL-DQ-CATALOG"));

        ListingFixture noCatalog = listingFixture("dq-no-catalog");
        marketplaceListingMapper.insert(marketplaceListing(noCatalog.inventory().getItemInventoryId(), null, 2, "BL-DQ-NO-CATALOG"));

        MarketplaceListing endedListing = insertedListingWithCondition("dq-ended", "BL-DQ-ENDED", null, null);
        endedListing.setListingStatusCode("ENDED");
        marketplaceListingMapper.update(endedListing);

        Set<PricingDataQualityGap> gaps = marketplaceListingMapper.findPricingDataQualityGapsByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ACTIVE"), 10);

        assertThat(gaps).extracting(PricingDataQualityGap::getExternalListingId)
                .containsExactlyInAnyOrder("BL-DQ-CONDITION", "BL-DQ-USED-NO-GRADES", "BL-DQ-CATALOG", "BL-DQ-NO-CATALOG");
        assertThat(gap(gaps, "BL-DQ-CONDITION")).satisfies(gap -> {
            assertThat(gap.getMarketplaceListingId()).isEqualTo(missingCondition.getMarketplaceListingId());
            assertThat(gap.getNewOrUsed()).isEqualTo("MINT");
            assertThat(gap.isMissingCondition()).isTrue();
            assertThat(gap.isMissingCompleteness()).isTrue();
            assertThat(gap.isMissingBoxGrade()).isFalse();
            assertThat(gap.getLatestCrawlStatusCode()).isNull();
        });
        assertThat(gap(gaps, "BL-DQ-USED-NO-GRADES")).satisfies(gap -> {
            assertThat(gap.isMissingCondition()).isFalse();
            assertThat(gap.isMissingBoxGrade()).isTrue();
            assertThat(gap.isMissingInstructionsGrade()).isTrue();
            assertThat(gap.getBoxConditionId()).isNull();
            assertThat(gap.getInstructionsConditionId()).isNull();
            assertThat(gap.getInstructionsConditionCode()).isNull();
            assertThat(gap.getLatestCrawlStatusCode()).isEqualTo("FAILING");
        });
        assertThat(gap(gaps, "BL-DQ-CATALOG")).satisfies(gap -> {
            assertThat(gap.isMissingItemType()).isTrue();
            assertThat(gap.isMissingInternalItemId()).isTrue();
            assertThat(gap.isMissingItemNumber()).isFalse();
            assertThat(gap.isMissingCatalogItem()).isFalse();
            assertThat(gap.isMissingBoxGrade()).isFalse();
        });
        assertThat(gap(gaps, "BL-DQ-NO-CATALOG")).satisfies(gap -> {
            assertThat(gap.isMissingCatalogItem()).isTrue();
            assertThat(gap.isMissingItemType()).isFalse();
            assertThat(gap.isMissingInternalItemId()).isFalse();
        });
        assertThat(marketplaceListingMapper.findPricingDataQualityGapsByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ACTIVE"), 1))
                .hasSize(1);

        PricingDataQualitySummary summary = marketplaceListingMapper.summarizePricingDataQualityByListingExternalServiceIdAndListingStatusCodes(2, Set.of("ACTIVE"));
        assertThat(summary.getListingsChecked()).isEqualTo(7);
        assertThat(summary.getListingsWithGaps()).isEqualTo(4);
        assertThat(summary.getMissingCondition()).isEqualTo(1);
        assertThat(summary.getMissingCompleteness()).isEqualTo(1);
        assertThat(summary.getMissingCatalogItem()).isEqualTo(1);
        assertThat(summary.getMissingItemNumber()).isZero();
        assertThat(summary.getMissingItemType()).isEqualTo(1);
        assertThat(summary.getMissingInternalItemId()).isEqualTo(1);
        assertThat(summary.getMissingBoxGrade()).isEqualTo(1);
        assertThat(summary.getMissingInstructionsGrade()).isEqualTo(1);

        PricingDataQualitySummary emptySummary = marketplaceListingMapper.summarizePricingDataQualityByListingExternalServiceIdAndListingStatusCodes(2, Set.of("DRAFT"));
        assertThat(emptySummary.getListingsChecked()).isZero();
        assertThat(emptySummary.getListingsWithGaps()).isZero();
    }

    private MarketplaceListing insertedListingWithCondition(String itemKey, String externalListingId, String newOrUsed, String completeness) {
        ListingFixture fixture = listingFixture(itemKey);
        fixture.inventory().setNewOrUsed(newOrUsed);
        fixture.inventory().setCompleteness(completeness);
        itemInventoryMapper.update(fixture.inventory());
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), 2, externalListingId);
        listing.setFixedPrice(false);
        marketplaceListingMapper.insert(listing);
        return listing;
    }

    private void seedConditionGrade(int conditionId, String code, String description) {
        conditionMapper.findConditionById(conditionId)
                .orElseGet(() -> {
                    Condition condition = Condition.builder()
                            .conditionId(conditionId)
                            .conditionCode(code)
                            .conditionDescription(description)
                            .conditionText(description)
                            .build();
                    conditionMapper.insert(condition);
                    return condition;
                });
    }

    private static PricingDataQualityGap gap(Set<PricingDataQualityGap> gaps, String externalListingId) {
        return gaps.stream()
                .filter(gap -> externalListingId.equals(gap.getExternalListingId()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void bricklinkMarketplaceListingSupportsCrudAndInventoryLookup() {
        MarketplaceListing listing = insertedMarketplaceListing("bricklink-listing", "BL-2", 2);
        BricklinkMarketplaceListing bricklink = bricklinkMarketplaceListing(listing.getMarketplaceListingId(), 2001);

        bricklinkMarketplaceListingMapper.insert(bricklink);
        bricklink.setColorName("Red");
        bricklinkMarketplaceListingMapper.update(bricklink);

        assertThat(bricklinkMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getColorName()).isEqualTo("Red");
                    assertThat(found.getEnvironmentCode()).isEqualTo("sandbox");
                    assertThat(found.getSystemRemarksHash()).isEqualTo("hash-1");
                    assertThat(found.getLastRemoteSafetyStatusCode()).isEqualTo("VERIFIED");
                });
        assertThat(bricklinkMarketplaceListingMapper.findByBricklinkInventoryId(2001)).isPresent();
        assertThat(bricklinkMarketplaceListingMapper.findAll()).hasSize(1);

        bricklink.setLastRemoteQuantity(7);
        bricklink.setLastRemoteSafetyStatusCode("BLOCKED");
        bricklink.setLastRemoteSafetyMessage("Missing system block");
        bricklinkMarketplaceListingMapper.upsert(bricklink);
        assertThat(bricklinkMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getLastRemoteQuantity()).isEqualTo(7);
                    assertThat(found.getLastRemoteSafetyStatusCode()).isEqualTo("BLOCKED");
                    assertThat(found.getLastRemoteSafetyMessage()).isEqualTo("Missing system block");
                });

        assertThat(bricklinkMarketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
    }

    @Test
    void ebayMarketplaceListingAndItemSpecificsSupportCrud() {
        MarketplaceListing listing = insertedMarketplaceListing("ebay-listing", "EBAY-1", 3);
        EbayMarketplaceListing ebay = ebayMarketplaceListing(listing.getMarketplaceListingId(), "ebay-item-1");

        ebayMarketplaceListingMapper.insert(ebay);
        ebay.setListingFormat("AUCTION");
        ebayMarketplaceListingMapper.update(ebay);

        assertThat(ebayMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getListingFormat()).isEqualTo("AUCTION"));
        assertThat(ebayMarketplaceListingMapper.findByEbayItemId("ebay-item-1")).isPresent();
        assertThat(ebayMarketplaceListingMapper.findAll()).hasSize(1);

        ebay.setDuration("DAYS_7");
        ebayMarketplaceListingMapper.upsert(ebay);
        assertThat(ebayMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getDuration()).isEqualTo("DAYS_7"));

        EbayListingItemSpecific specific = ebayListingItemSpecific(listing.getMarketplaceListingId(), "Brand", "LEGO");
        ebayListingItemSpecificMapper.insert(specific);
        assertThat(ebayListingItemSpecificMapper.findByMarketplaceListingIdAndNameAndValue(listing.getMarketplaceListingId(), "Brand", "LEGO")).isPresent();
        assertThat(ebayListingItemSpecificMapper.findByMarketplaceListingId(listing.getMarketplaceListingId())).hasSize(1);
        assertThat(ebayListingItemSpecificMapper.findByName("Brand")).hasSize(1);
        assertThat(ebayListingItemSpecificMapper.findAll()).hasSize(1);
        ebayListingItemSpecificMapper.upsert(specific);
        assertThat(ebayListingItemSpecificMapper.delete(listing.getMarketplaceListingId(), "Brand", "LEGO")).isOne();

        assertThat(ebayMarketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
    }

    private MarketplaceListing insertedMarketplaceListing(String itemKey, String externalListingId, Integer serviceId) {
        ListingFixture fixture = listingFixture(itemKey);
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), serviceId, externalListingId);
        marketplaceListingMapper.insert(listing);
        return listing;
    }

    private ListingFixture listingFixture(String itemKey) {
        seedExternalCatalog();
        ExternalCatalogItem item = insertExternalCatalogItem(itemKey);
        ExternalCategory category = externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "5").orElseThrow();
        externalCatalogItemCategoryMapper.insert(externalCatalogItemCategory(item.getExternalCatalogItemId(), category.getExternalCategoryId()));
        ItemInventory inventory = insertItemInventory("uuid-" + itemKey);
        return new ListingFixture(item, inventory);
    }

    private void assertHydratedCatalogItem(MarketplaceListing listing) {
        assertThat(listing.getExternalCatalogItem()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalItemKey()).isEqualTo("listing-1");
        assertThat(listing.getExternalCatalogItem().getExternalService()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalService().getServiceCode()).isEqualTo("BRICKLINK");
        assertThat(listing.getExternalCatalogItem().getExternalService().getExternalServiceType()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalService().getExternalServiceType().getExternalServiceTypeName()).isEqualTo("MARKETPLACE");
        assertThat(listing.getExternalCatalogItem().getExternalService().getCapabilities())
                .extracting("capabilityCode")
                .containsExactlyInAnyOrder("MARKETPLACE_LISTING", "PRICE_GUIDE");
        assertThat(listing.getExternalCatalogItem().getCategories())
                .extracting(ExternalCategory::getExternalCategoryKey)
                .containsExactly("5");
    }

    private record ListingFixture(ExternalCatalogItem item, ItemInventory inventory) {
    }
}
