package io.legohunter.data.dao;

/**
 * The DAOs the shared price crawler works with, all bound to one database connection.
 *
 * <p>The crawler is the only writer of most of the shared {@code marketplace_pricing} schema, so it can be given its
 * own MySQL account and connection instead of the environment's (HOMELAB-126). When
 * {@code lego.marketplace-pricing.crawler.database-key-name} is set, these DAOs run on that connection; otherwise they
 * are the application's ordinary DAOs on the environment connection.
 *
 * @param ownConnection true when these DAOs run on the crawler's own connection
 */
public record PricingCrawlerDaos(
        CanonicalItemDao canonicalItemDao,
        MarketplaceItemDao marketplaceItemDao,
        CrawlTargetDao crawlTargetDao,
        CrawlAttemptDao crawlAttemptDao,
        MarketSnapshotDao marketSnapshotDao,
        MarketListingDao marketListingDao,
        boolean ownConnection
) {
}
