package io.legohunter.lego.data.pricing;

import com.zaxxer.hikari.HikariDataSource;
import io.legohunter.data.dao.CanonicalItemDao;
import io.legohunter.data.dao.CrawlAttemptDao;
import io.legohunter.data.dao.CrawlTargetDao;
import io.legohunter.data.dao.MarketListingDao;
import io.legohunter.data.dao.MarketSnapshotDao;
import io.legohunter.data.dao.MarketplaceItemDao;
import io.legohunter.data.dao.PricingCrawlerDaos;
import io.legohunter.data.mybatis.mapper.CanonicalItemMapper;
import io.legohunter.data.mybatis.mapper.CrawlAttemptMapper;
import io.legohunter.data.mybatis.mapper.CrawlTargetMapper;
import io.legohunter.data.mybatis.mapper.MarketListingMapper;
import io.legohunter.data.mybatis.mapper.MarketSnapshotMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceItemMapper;
import io.legohunter.lego.data.builder.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

/**
 * The price crawler's own connection to MySQL: a small pool signed in as the crawler's account, and the crawler's
 * DAOs bound to it (HOMELAB-126).
 *
 * <p>It is deliberately not a {@code DataSource} or {@code SqlSessionFactory} bean. The application has exactly one
 * of each, the environment's, and Spring Boot's MyBatis, transaction and scheduler-lock setup all rely on that. This
 * class keeps the second pool to itself and hands out only the DAOs.
 *
 * <p>The pool starts from the environment pool's settings ({@code spring.datasource.hikari}), so it runs each session
 * in UTC and commits the same way, then takes its own name and a small size. {@code lego.marketplace-pricing.crawler.hikari}
 * overrides any of it.
 */
@Slf4j
public class PricingCrawlerConnection implements AutoCloseable {
    public static final String DATABASE_KEY_NAME_PROPERTY = "lego.marketplace-pricing.crawler.database-key-name";
    static final String HIKARI_PROPERTIES = "lego.marketplace-pricing.crawler.hikari";
    static final String ENVIRONMENT_HIKARI_PROPERTIES = "spring.datasource.hikari";
    static final String POOL_NAME = "marketplace-pricing-crawler-pool";
    static final int MAXIMUM_POOL_SIZE = 3;
    static final int MINIMUM_IDLE = 1;

    private final HikariDataSource dataSource;
    private final PricingCrawlerDaos daos;

    private PricingCrawlerConnection(HikariDataSource dataSource, PricingCrawlerDaos daos) {
        this.dataSource = dataSource;
        this.daos = daos;
    }

    /**
     * Opens the connection named by {@code databaseKeyName} under {@code lego.databases}.
     *
     * @param customizers the application's MyBatis customizers, applied here too so both connections read the same
     *                    settings (among them the pricing schema name)
     */
    public static PricingCrawlerConnection open(String databaseKeyName, Environment environment, List<ConfigurationCustomizer> customizers) {
        if (databaseKeyName == null || databaseKeyName.isBlank()) {
            throw new IllegalStateException(DATABASE_KEY_NAME_PROPERTY + " is set but empty");
        }
        Map<String, Object> databases = Binder.get(environment)
                .bind("lego.databases", Bindable.mapOf(String.class, Object.class))
                .orElse(Map.of());

        HikariDataSource dataSource = (HikariDataSource) new DataSourceBuilder()
                .dataSource(databaseKeyName.trim(), databases, ENVIRONMENT_HIKARI_PROPERTIES, environment);
        dataSource.setPoolName(POOL_NAME);
        dataSource.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        dataSource.setMinimumIdle(MINIMUM_IDLE);
        Binder.get(environment).bind(HIKARI_PROPERTIES, Bindable.ofInstance(dataSource));

        try {
            Configuration configuration = new Configuration();
            customizers.forEach(customizer -> customizer.customize(configuration));
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setConfiguration(configuration);
            SqlSessionFactory factory = factoryBean.getObject();
            // Each mapper's XML sits beside its interface, so adding the interface loads both.
            for (Class<?> mapper : List.of(CanonicalItemMapper.class, MarketplaceItemMapper.class, CrawlTargetMapper.class,
                    CrawlAttemptMapper.class, MarketSnapshotMapper.class, MarketListingMapper.class)) {
                factory.getConfiguration().addMapper(mapper);
            }
            SqlSessionTemplate template = new SqlSessionTemplate(factory);
            PricingCrawlerDaos daos = new PricingCrawlerDaos(
                    new CanonicalItemDao(template.getMapper(CanonicalItemMapper.class)),
                    new MarketplaceItemDao(template.getMapper(MarketplaceItemMapper.class)),
                    new CrawlTargetDao(template.getMapper(CrawlTargetMapper.class)),
                    new CrawlAttemptDao(template.getMapper(CrawlAttemptMapper.class)),
                    new MarketSnapshotDao(template.getMapper(MarketSnapshotMapper.class)),
                    new MarketListingDao(template.getMapper(MarketListingMapper.class)),
                    true);
            log.info("The price crawler uses its own connection: database key [{}], user={}, poolName={}, maxPoolSize={}",
                    databaseKeyName.trim(), dataSource.getUsername(), dataSource.getPoolName(), dataSource.getMaximumPoolSize());
            return new PricingCrawlerConnection(dataSource, daos);
        } catch (Exception e) {
            dataSource.close();
            throw new IllegalStateException("Could not set up the price crawler's connection for database key ["
                    + databaseKeyName.trim() + "]", e);
        }
    }

    public PricingCrawlerDaos daos() {
        return daos;
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
