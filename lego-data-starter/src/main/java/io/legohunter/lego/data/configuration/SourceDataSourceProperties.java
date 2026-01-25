package io.legohunter.lego.data.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;
import java.util.Properties;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource")
public class SourceDataSourceProperties extends DataSourceProperties {

    @Autowired
    private DatabaseProperties databaseProperties;

    private Properties hikari;

    private String databaseKeyName;

    @Override
    public String determinePassword() {
        return Optional.ofNullable(super.determinePassword())
                .orElseGet(() -> getDatabase(getDatabaseKeyName()).map(DatabaseProperties.Database::getPassword)
                        .orElse(null));
    }

    @Override
    public String determineUrl() {
        return getDatabase(getDatabaseKeyName()).map(DatabaseProperties.Database::getUrl).orElseGet(super::determineUrl);
    }

    @Override
    public String determineUsername() {
        return getDatabase(getDatabaseKeyName()).map(DatabaseProperties.Database::getUsername).orElseGet(super::determineUsername);
    }

    @Override
    public String determineDriverClassName() {
        return getDatabase(getDatabaseKeyName()).map(DatabaseProperties.Database::getDriverClassName).orElseGet(super::determineDriverClassName);
    }

    public Optional<DatabaseProperties.Database> getDatabase(String key) {
        return Optional.ofNullable(databaseProperties)
                .map(d -> d.getDatabases().get(key));
    }
}
