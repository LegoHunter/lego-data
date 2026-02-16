package io.legohunter.lego.data.builder;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DataSourceBuilder {

    public DataSource dataSource(@NonNull String databaseKeyName, Map<String, Object> databasesMap, String hikariConfigPropertyName, Environment environment) {

        Map<String, Object> databaseConfigurationMap = Optional.of(databasesMap)
                .map(m -> m.get(databaseKeyName))
                .map(m -> (Map<String, Object>) m)
                .orElseThrow(() -> new IllegalArgumentException("The database key [%s] was not found in the database key names %s".formatted(databaseKeyName, databasesMap.keySet())));

        log.info("Configuring datasource for database key name [{}] from [{}] database key names {}", databaseKeyName, databasesMap.size(), databasesMap.keySet());

        // Bind lego.databases[key] -> DataSourceProperties
        Binder dbBinder = new Binder(new MapConfigurationPropertySource(databaseConfigurationMap));
        DataSourceProperties dsp = dbBinder
                .bind("", DataSourceProperties.class)
                .orElseThrow(() -> new IllegalStateException("Something bad happened"));

        // Let Spring build the real HikariDataSource
        HikariDataSource ds = dsp
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // Overlay spring.datasource.hikari directly
        Binder.get(environment)
                .bind(hikariConfigPropertyName, Bindable.ofInstance(ds));

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
