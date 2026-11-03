package io.legohunter.lego.data.environment;

import io.legohunter.lego.data.autoconfigure.LegoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * Binds {@code lego.environment} for every service that uses the lego-data starter, and checks it
 * against the connected database at startup.
 *
 * <p>The properties are always available, so services can read the environment even without a
 * database. The check runs when lego-data is enabled ({@code lego.data.enabled=true}) and a
 * {@link DataSource} exists, unless {@code lego.environment.verify-database=false}. It is created
 * eagerly, even under global lazy initialization, so it cannot be skipped.
 */
@AutoConfiguration(after = LegoDataAutoConfiguration.class,
        afterName = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@EnableConfigurationProperties(LegoEnvironmentProperties.class)
public class LegoEnvironmentAutoConfiguration {

    @Bean
    @Lazy(false)
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(name = "lego.data.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "lego.environment.verify-database", havingValue = "true", matchIfMissing = true)
    public EnvironmentDatabaseVerifier environmentDatabaseVerifier(LegoEnvironmentProperties environment, DataSource dataSource) {
        return new EnvironmentDatabaseVerifier(environment, dataSource);
    }
}
