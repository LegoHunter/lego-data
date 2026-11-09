package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.CanonicalItem;
import io.legohunter.data.dto.CrawlAttempt;
import io.legohunter.data.dto.CrawlTarget;
import io.legohunter.data.dto.MarketListing;
import io.legohunter.data.dto.MarketSnapshot;
import io.legohunter.data.dto.Marketplace;
import io.legohunter.data.dto.MarketplaceItem;
import io.legohunter.data.dto.WantListItem;
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

/**
 * One crawl through the shared marketplace_pricing DAOs (HOMELAB-31): an environment wants an item,
 * the crawler finds the target, claims it, stores a snapshot and its lots, and logs the attempt.
 */
@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/marketplace_pricing_schema.ddl"
})
class MarketplacePricingDaoTest {
    private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-11-09T12:00:00Z");

    @Autowired MarketplaceDao marketplaceDao;
    @Autowired CanonicalItemDao canonicalItemDao;
    @Autowired MarketplaceItemDao marketplaceItemDao;
    @Autowired WantListItemDao wantListItemDao;
    @Autowired CrawlTargetDao crawlTargetDao;
    @Autowired CrawlAttemptDao crawlAttemptDao;
    @Autowired MarketSnapshotDao marketSnapshotDao;
    @Autowired MarketListingDao marketListingDao;

    @Test
    void oneCrawlFromWantListToComparables() {
        assertThat(marketplaceDao.findAll()).extracting(Marketplace::getMarketplaceCode).containsExactly("BRICKLINK", "EBAY");
        assertThat(marketplaceDao.findByMarketplaceCode("BRICKLINK")).isPresent();

        CanonicalItem item = canonicalItemDao.insert(CanonicalItem.builder().itemTypeCode("S").itemNumber("6390-1").itemName("Main Street").build());
        assertThat(item.getCreatedAt()).isNotNull();
        assertThat(canonicalItemDao.findByItemTypeCodeAndItemNumber("S", "6390-1")).contains(item);
        item.setItemName("Main Street");
        assertThat(canonicalItemDao.update(item).getItemName()).isEqualTo("Main Street");

        MarketplaceItem marketplaceItem = marketplaceItemDao.insert(MarketplaceItem.builder().marketplaceCode("BRICKLINK")
                .canonicalItemId(item.getCanonicalItemId()).marketplaceItemKey("6390-1").matchMethodCode("CATALOG_ID").build());
        marketplaceItem.setMarketplaceUniqueKey("4997");
        marketplaceItem.setResolvedAt(NOW);
        assertThat(marketplaceItemDao.update(marketplaceItem).getMarketplaceUniqueKey()).isEqualTo("4997");
        assertThat(marketplaceItemDao.findByMarketplaceCodeAndCanonicalItemId("BRICKLINK", item.getCanonicalItemId())).isPresent();

        WantListItem want = WantListItem.builder().environmentCode("sandbox").canonicalItemId(item.getCanonicalItemId())
                .conditionCode("N").completenessCode("S").firstWantedAt(NOW).lastConfirmedAt(NOW).build();
        wantListItemDao.upsert(want);
        assertThat(wantListItemDao.findByEnvironmentCode("sandbox")).hasSize(1);
        assertThat(wantListItemDao.findByCanonicalItemId(item.getCanonicalItemId())).hasSize(1);

        CrawlTarget target = crawlTargetDao.insert(CrawlTarget.builder().marketplaceCode("BRICKLINK")
                .canonicalItemId(item.getCanonicalItemId()).queryConditionCode("N").nextDueAt(NOW.minusMinutes(1)).build());
        assertThat(target.getTargetStatusCode()).isEqualTo("IDLE");
        assertThat(target.getConsecutiveFailureCount()).isZero();
        assertThat(crawlTargetDao.findDue("BRICKLINK", NOW, 5)).extracting(CrawlTarget::getCrawlTargetId).containsExactly(target.getCrawlTargetId());
        assertThat(crawlTargetDao.claim(target.getCrawlTargetId(), "crawler-1", NOW, NOW.plusMinutes(10))).isTrue();
        assertThat(crawlTargetDao.claim(target.getCrawlTargetId(), "crawler-2", NOW, NOW.plusMinutes(10))).isFalse();

        CrawlAttempt attempt = crawlAttemptDao.insert(CrawlAttempt.builder().crawlTargetId(target.getCrawlTargetId())
                .startedAt(NOW).outcomeCode("RUNNING").build());

        MarketSnapshot snapshot = marketSnapshotDao.insert(MarketSnapshot.builder().crawlTargetId(target.getCrawlTargetId())
                .marketplaceCode("BRICKLINK").canonicalItemId(item.getCanonicalItemId()).queryConditionCode("N")
                .marketplaceItemKey("6390-1").marketplaceUniqueKey("4997").listingCount(2).capturedAt(NOW).build());
        MarketListing sealed = marketListingDao.insert(MarketListing.builder().marketSnapshotId(snapshot.getMarketSnapshotId())
                .externalListingId("1").conditionCode("N").completenessCode("S").itemPrice(new BigDecimal("399.99")).currencyCode("USD").build());
        marketListingDao.insert(MarketListing.builder().marketSnapshotId(snapshot.getMarketSnapshotId())
                .externalListingId("2").conditionCode("N").completenessCode("C").itemPrice(new BigDecimal("250.00")).currencyCode("USD").build());

        attempt.setFinishedAt(NOW.plusSeconds(2));
        attempt.setOutcomeCode("SUCCEEDED");
        attempt.setMarketSnapshotId(snapshot.getMarketSnapshotId());
        assertThat(crawlAttemptDao.finish(attempt).getOutcomeCode()).isEqualTo("SUCCEEDED");
        assertThat(crawlAttemptDao.findLatestByCrawlTargetId(target.getCrawlTargetId(), 5)).hasSize(1);

        CrawlTarget claimed = crawlTargetDao.findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
                "BRICKLINK", item.getCanonicalItemId(), "N").orElseThrow();
        claimed.setTargetStatusCode("IDLE");
        claimed.setClaimedAt(null);
        claimed.setClaimExpiresAt(null);
        claimed.setClaimedBy(null);
        claimed.setLastAttemptAt(NOW);
        claimed.setLastSuccessAt(NOW);
        claimed.setLastMarketSnapshotId(snapshot.getMarketSnapshotId());
        claimed.setNextDueAt(NOW.plusDays(3));
        assertThat(crawlTargetDao.update(claimed).getLastMarketSnapshotId()).isEqualTo(snapshot.getMarketSnapshotId());

        assertThat(marketSnapshotDao.findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode("BRICKLINK", item.getCanonicalItemId(), "N"))
                .contains(snapshot);
        assertThat(marketSnapshotDao.findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(item.getCanonicalItemId(), "N",
                NOW.minusDays(1), NOW.plusDays(1))).containsExactly(snapshot);
        assertThat(marketListingDao.findByMarketSnapshotId(snapshot.getMarketSnapshotId())).hasSize(2);
        assertThat(marketListingDao.findComparablesByMarketSnapshotId(snapshot.getMarketSnapshotId(), "N", "S")).containsExactly(sealed);

        assertThat(wantListItemDao.deleteByEnvironmentCodeAndLastConfirmedAtBefore("sandbox", NOW.minusDays(1))).isZero();
        assertThat(wantListItemDao.delete(want)).isOne();
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class TestConfiguration {
    }
}
