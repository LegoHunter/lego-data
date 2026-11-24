package io.legohunter.lego.data.pricing;

import io.legohunter.data.dao.CanonicalItemDao;
import io.legohunter.data.dao.CrawlAttemptDao;
import io.legohunter.data.dao.CrawlTargetDao;
import io.legohunter.data.dao.MarketListingDao;
import io.legohunter.data.dao.MarketSnapshotDao;
import io.legohunter.data.dao.MarketplaceItemDao;
import io.legohunter.data.dao.PricingCrawlerDaos;
import io.legohunter.data.dto.CanonicalItem;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/** Which connection the price crawler's DAOs run on, and that its own connection really reads and commits. */
class PricingCrawlerAutoConfigurationTest {
    private static final String H2_URL = "jdbc:h2:mem:pricing_crawler;MODE=MySQL;DB_CLOSE_DELAY=-1";

    private final CanonicalItemDao sharedCanonicalItemDao = mock(CanonicalItemDao.class);

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PricingCrawlerAutoConfiguration.class))
            .withBean(CanonicalItemDao.class, () -> sharedCanonicalItemDao)
            .withBean(MarketplaceItemDao.class, () -> mock(MarketplaceItemDao.class))
            .withBean(CrawlTargetDao.class, () -> mock(CrawlTargetDao.class))
            .withBean(CrawlAttemptDao.class, () -> mock(CrawlAttemptDao.class))
            .withBean(MarketSnapshotDao.class, () -> mock(MarketSnapshotDao.class))
            .withBean(MarketListingDao.class, () -> mock(MarketListingDao.class))
            .withBean(ConfigurationCustomizer.class, () -> configuration ->
                    configuration.getVariables().setProperty("marketplacePricingSchema", "marketplace_pricing"));

    @Test
    void withoutACrawlerKeyTheCrawlerUsesTheApplicationsDaos() {
        runner.withPropertyValues("lego.data.enabled=true")
                .run(context -> {
                    assertThat(context).hasNotFailed().doesNotHaveBean(PricingCrawlerConnection.class);
                    PricingCrawlerDaos daos = context.getBean(PricingCrawlerDaos.class);
                    assertThat(daos.ownConnection()).isFalse();
                    assertThat(daos.canonicalItemDao()).isSameAs(sharedCanonicalItemDao);
                });
    }

    @Test
    void withACrawlerKeyTheCrawlerReadsAndCommitsOnItsOwnConnection() throws Exception {
        try (Connection setup = DriverManager.getConnection(H2_URL, "sa", "");
             Statement statement = setup.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS marketplace_pricing");
            statement.execute("DROP TABLE IF EXISTS marketplace_pricing.canonical_item");
            statement.execute("""
                    CREATE TABLE marketplace_pricing.canonical_item (
                        canonical_item_id INT AUTO_INCREMENT PRIMARY KEY,
                        item_type_code VARCHAR(8) NOT NULL,
                        item_number VARCHAR(64) NOT NULL,
                        item_name VARCHAR(255),
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                    )""");

            runner.withPropertyValues(
                            "lego.data.enabled=true",
                            "lego.marketplace-pricing.crawler.database-key-name=crawler-key",
                            "lego.databases.crawler-key.url=" + H2_URL,
                            "lego.databases.crawler-key.username=sa",
                            "lego.databases.crawler-key.password=",
                            "lego.databases.crawler-key.driver-class-name=org.h2.Driver",
                            // What the services set for the environment pool; the crawler pool starts from it.
                            "spring.datasource.hikari.auto-commit=false",
                            "spring.datasource.hikari.maximum-pool-size=40",
                            "spring.datasource.hikari.minimum-idle=10",
                            "spring.datasource.hikari.pool-name=environment-pool")
                    .run(context -> {
                        assertThat(context).hasNotFailed().hasSingleBean(PricingCrawlerConnection.class);
                        // The second pool must not show up as a DataSource bean: the application has exactly one.
                        assertThat(context).doesNotHaveBean(DataSource.class);

                        PricingCrawlerDaos daos = context.getBean(PricingCrawlerDaos.class);
                        assertThat(daos.ownConnection()).isTrue();
                        assertThat(daos.canonicalItemDao()).isNotSameAs(sharedCanonicalItemDao);

                        CanonicalItem created = daos.canonicalItemDao().insert(CanonicalItem.builder()
                                .itemTypeCode("S").itemNumber("10194-1").itemName("Emerald Night").build());
                        assertThat(created.getCanonicalItemId()).isNotNull();
                        assertThat(daos.canonicalItemDao().findByItemTypeCodeAndItemNumber("S", "10194-1"))
                                .get().extracting(CanonicalItem::getItemName).isEqualTo("Emerald Night");

                        // Committed: another connection sees the row although the pool does not auto-commit.
                        try (ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM marketplace_pricing.canonical_item")) {
                            rows.next();
                            assertThat(rows.getInt(1)).isEqualTo(1);
                        }
                    });
        }
    }

    @Test
    void anUnknownCrawlerKeyStopsStartup() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.marketplace-pricing.crawler.database-key-name=missing-key")
                .run(context -> assertThat(context).getFailure().rootCause()
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("The database key [missing-key] was not found"));
    }

    @Test
    void doesNothingWhenLegoDataIsOff() {
        runner.run(context -> assertThat(context).hasNotFailed().doesNotHaveBean(PricingCrawlerDaos.class));
    }
}
