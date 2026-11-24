package io.legohunter.lego.data.pricing;

import io.legohunter.data.dao.CanonicalItemDao;
import io.legohunter.data.dao.CrawlAttemptDao;
import io.legohunter.data.dao.CrawlTargetDao;
import io.legohunter.data.dao.MarketListingDao;
import io.legohunter.data.dao.MarketSnapshotDao;
import io.legohunter.data.dao.MarketplaceItemDao;
import io.legohunter.data.dao.PricingCrawlerDaos;
import io.legohunter.lego.data.autoconfigure.LegoDataAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Provides {@link PricingCrawlerDaos}: on the crawler's own connection when
 * {@code lego.marketplace-pricing.crawler.database-key-name} is set, otherwise the application's ordinary DAOs
 * (HOMELAB-126).
 */
@AutoConfiguration(after = LegoDataAutoConfiguration.class)
@ConditionalOnProperty(name = "lego.data.enabled", havingValue = "true")
public class PricingCrawlerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = PricingCrawlerConnection.DATABASE_KEY_NAME_PROPERTY)
    public PricingCrawlerConnection pricingCrawlerConnection(Environment environment, ObjectProvider<ConfigurationCustomizer> customizers) {
        return PricingCrawlerConnection.open(
                environment.getProperty(PricingCrawlerConnection.DATABASE_KEY_NAME_PROPERTY),
                environment,
                customizers.orderedStream().toList());
    }

    @Bean
    public PricingCrawlerDaos pricingCrawlerDaos(
            ObjectProvider<PricingCrawlerConnection> ownConnection,
            CanonicalItemDao canonicalItemDao,
            MarketplaceItemDao marketplaceItemDao,
            CrawlTargetDao crawlTargetDao,
            CrawlAttemptDao crawlAttemptDao,
            MarketSnapshotDao marketSnapshotDao,
            MarketListingDao marketListingDao
    ) {
        PricingCrawlerConnection connection = ownConnection.getIfAvailable();
        if (connection != null) {
            return connection.daos();
        }
        return new PricingCrawlerDaos(canonicalItemDao, marketplaceItemDao, crawlTargetDao, crawlAttemptDao,
                marketSnapshotDao, marketListingDao, false);
    }
}
