package io.legohunter.lego.data.autoconfigure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Auto-configuration for Lego Data.
 * <p>
 * Activated when:
 * - No DataSource bean is already defined
 * - Property 'databases.enabled' is present
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({DataSource.class, HikariDataSource.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        name = "lego.data.enabled",
        havingValue = "true"

)
//@EnableConfigurationProperties(DatabaseProperties.class)
@RequiredArgsConstructor
public class LegoDataAutoConfiguration {

    private final Environment environment;

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("lego.databases")
    public Map<String, Object> databasesMap() {
        return new HashMap<>();
    }

    @Bean
    public DataSource dataSource(Map<String, Object> databasesMap) {

        String databaseKeyName = environment.getProperty("spring.datasource.database-key-name");
        if (databaseKeyName == null) {
            throw new IllegalStateException("spring.datasource.database-key-name property not set");
        }

        Map<String, Object> databaseConfigurationMap = Optional.of(databasesMap)
                .map(m -> m.get(databaseKeyName))
                .map(m -> (Map<String, Object>) m)
                .orElseThrow();

        // 1️⃣ Bind lego.databases[key] -> DataSourceProperties
        Binder dbBinder = new Binder(new MapConfigurationPropertySource(databaseConfigurationMap));
        DataSourceProperties dsp = dbBinder
                .bind("", DataSourceProperties.class)
                .orElseThrow(() -> new IllegalStateException("Something bad happened"));

        // 2️⃣ Let Spring build the real HikariDataSource
        HikariDataSource ds = dsp
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // 3️⃣ Overlay spring.datasource.hikari directly
        Binder.get(environment)
                .bind("spring.datasource.hikari", Bindable.ofInstance(ds));

        log.info(
                "Created HikariDataSource jdbcUrl={}, user={}, poolName={}, minIdle={}, maxPoolSize={}",
                ds.getJdbcUrl(),
                ds.getUsername(),
                ds.getPoolName(),
                ds.getMinimumIdle(),
                ds.getMaximumPoolSize()
        );

        return ds;
    }


}
