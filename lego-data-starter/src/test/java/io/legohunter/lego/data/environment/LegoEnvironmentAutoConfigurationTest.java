package io.legohunter.lego.data.environment;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * When the environment check runs, and that it stops startup on a mismatch. The rule combinations are
 * covered in lego-data-model's {@code EnvironmentDatabasePolicyTest}.
 */
class LegoEnvironmentAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LegoEnvironmentAutoConfiguration.class));

    @Test
    void startsWhenTheEnvironmentMatchesTheConnectedDatabase() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=sandbox")
                .withBean(DataSource.class, () -> h2("lego_sandbox"))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(EnvironmentDatabaseVerifier.class));
    }

    @Test
    void startsProdOnTheProdDatabase() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=prod", "lego.environment.production=true")
                .withBean(DataSource.class, () -> h2("lego_prod"))
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void refusesToStartWhenTheEnvironmentDoesNotMatch() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=sandbox")
                .withBean(DataSource.class, () -> h2("lego_dev"))
                .run(context -> assertThat(context).getFailure()
                        .rootCause()
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Configured environment does not match the connected database:\n"
                                + " - lego.environment.code 'sandbox' must be connected to database schema 'lego_sandbox', not 'LEGO_DEV'"));
    }

    @Test
    void defaultsToLocalWhichMayUseAnyDatabaseButProds() {
        runner.withPropertyValues("lego.data.enabled=true")
                .withBean(DataSource.class, () -> h2("lego_sandbox"))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(LegoEnvironmentProperties.class).getCode()).isEqualTo("local");
                    assertThat(context.getBean(LegoEnvironmentProperties.class).isProduction()).isFalse();
                });
        runner.withPropertyValues("lego.data.enabled=true")
                .withBean(DataSource.class, () -> h2("lego_prod"))
                .run(context -> assertThat(context).getFailure()
                        .rootCause()
                        .hasMessageContaining("lego.environment.code 'local' is not PROD but is connected to PROD's database schema 'LEGO_PROD'"));
    }

    @Test
    void refusesToStartWhenTheDatabaseCannotBeReached() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=sandbox")
                .withBean(DataSource.class, LegoEnvironmentAutoConfigurationTest::unreachable)
                .run(context -> assertThat(context).getFailure()
                        .rootCause()
                        .hasMessage("Communications link failure"));
    }

    @Test
    void canBeSwitchedOffForToolsThatTargetSeveralDatabases() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=sandbox", "lego.environment.verify-database=false")
                .withBean(DataSource.class, () -> h2("lego_prod"))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(EnvironmentDatabaseVerifier.class));
    }

    @Test
    void doesNotRunWhenLegoDataIsDisabled() {
        runner.withPropertyValues("lego.environment.code=sandbox")
                .withBean(DataSource.class, () -> h2("lego_prod"))
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(EnvironmentDatabaseVerifier.class));
    }

    @Test
    void bindsTheEnvironmentEvenWithoutADatabase() {
        runner.withPropertyValues("lego.data.enabled=true", "lego.environment.code=dev")
                .run(context -> {
                    assertThat(context).hasNotFailed().doesNotHaveBean(EnvironmentDatabaseVerifier.class);
                    assertThat(context.getBean(LegoEnvironmentProperties.class).getCode()).isEqualTo("dev");
                });
    }

    private static DataSource h2(String name) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + name);
        dataSource.setUser("sa");
        return dataSource;
    }

    private static DataSource unreachable() {
        return (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(), new Class<?>[]{DataSource.class},
                (proxy, method, args) -> {
                    throw new SQLException("Communications link failure");
                });
    }
}
