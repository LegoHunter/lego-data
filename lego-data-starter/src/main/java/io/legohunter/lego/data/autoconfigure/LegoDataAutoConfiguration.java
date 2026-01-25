package io.legohunter.lego.data.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import io.legohunter.lego.data.configuration.DatabaseProperties;
import io.legohunter.lego.data.configuration.SourceDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

/**
 * Auto-configuration for Lego Data.
 *
 * Activated when:
 *  - No DataSource bean is already defined
 *  - Property 'databases.enabled' is present
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({ DataSource.class, HikariDataSource.class })
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(
        name = "lego.databases.site5-dev-lego.url"

)
@EnableConfigurationProperties({
        SourceDataSourceProperties.class,
        DatabaseProperties.class
})
public class LegoDataAutoConfiguration {

    @Bean
    public DataSource dataSource(SourceDataSourceProperties properties) {

        log.info("Creating DataSource using database key [{}]", properties.getDatabaseKeyName());
        DatabaseProperties databaseProperties = Optional.ofNullable(properties.getDatabaseProperties()).orElseThrow(() -> new IllegalStateException("lego.databases configuration map not found"));
        Map<String, DatabaseProperties.Database> databasesMap = databaseProperties.getDatabases();

        log.info("Found [{}] configured database keys : {}", databasesMap.size(), databasesMap.keySet());

        DatabaseProperties.Database database = properties.getDatabase(properties.getDatabaseKeyName()).orElseThrow(() -> new IllegalStateException(String.format("Unable to get database configuration for key [%s]", properties.getDatabaseKeyName())));

        HikariDataSource dataSource = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // Apply Hikari-specific properties if present
        if (properties.getHikari() != null) {
            properties.getHikari().forEach((key, value) -> {
                try {
                    dataSource.addDataSourceProperty(key.toString(), value);
                } catch (Exception ex) {
                    log.warn("Could not apply Hikari property '{}'", key, ex);
                }
            });
        }

        return dataSource;
    }
}
