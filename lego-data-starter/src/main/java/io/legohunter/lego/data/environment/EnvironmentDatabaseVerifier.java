package io.legohunter.lego.data.environment;

import io.legohunter.data.environment.EnvironmentDatabasePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Refuses to start the application when {@code lego.environment} does not match the database it is
 * connected to: PROD only on {@code lego_prod}, SANDBOX only on {@code lego_sandbox}, DEV only on
 * {@code lego_dev}, and local on anything except {@code lego_prod} (see
 * {@link EnvironmentDatabasePolicy}).
 *
 * <p>It fails closed: if the schema cannot be read, for example because the database is unreachable,
 * the environment cannot be verified and the application does not start. Spring creates every
 * singleton before it starts Kafka listeners and schedulers, so a failure here stops startup before
 * anything writes.
 */
@Slf4j
public class EnvironmentDatabaseVerifier implements InitializingBean {

    static final String CODE_SETTING = "lego.environment.code";
    static final String PRODUCTION_SETTING = "lego.environment.production";

    private final LegoEnvironmentProperties environment;
    private final DataSource dataSource;

    public EnvironmentDatabaseVerifier(LegoEnvironmentProperties environment, DataSource dataSource) {
        this.environment = environment;
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        String schema = connectedSchema(dataSource);
        List<String> violations = EnvironmentDatabasePolicy.violations(
                CODE_SETTING,
                PRODUCTION_SETTING,
                environment.getCode(),
                environment.isProduction(),
                schema);
        if (!violations.isEmpty()) {
            throw new IllegalStateException("Configured environment does not match the connected database:\n - "
                    + String.join("\n - ", violations));
        }
        log.info("Environment '{}' (production={}) verified against database schema '{}'",
                environment.getCode(), environment.isProduction(), schema);
    }

    static String connectedSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DATABASE()")) {
            return resultSet.next() ? resultSet.getString(1) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot verify the configured environment: reading the connected database schema failed: "
                    + e.getMessage(), e);
        }
    }
}
