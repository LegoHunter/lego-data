package io.legohunter.data.environment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentDatabasePolicyTest {

    private static final String PREFIX = "lego.test";

    private static List<String> violations(String code, boolean production, String schema) {
        return EnvironmentDatabasePolicy.violations(PREFIX, code, production, schema);
    }

    @ParameterizedTest(name = "{0} on {2}")
    @CsvSource({
            "sandbox, false, lego_sandbox",
            "dev,     false, lego_dev",
            "prod,    true,  lego_prod",
            "local,   false, lego_sandbox",
            "local,   false, lego_dev",
            "local,   false, TESTDB",
            "sandbox, false, LEGO_SANDBOX",
            "' Dev ', false, ' lego_dev '"
    })
    void acceptsAnEnvironmentOnItsOwnDatabase(String code, boolean production, String schema) {
        assertThat(violations(code, production, schema)).isEmpty();
    }

    @ParameterizedTest(name = "{0} on {1}")
    @CsvSource({
            "sandbox, lego_dev,     lego_sandbox",
            "sandbox, lego_prod,    lego_sandbox",
            "dev,     lego_sandbox, lego_dev",
            "dev,     lego_prod,    lego_dev",
            "sandbox, TESTDB,       lego_sandbox"
    })
    void rejectsALowerEnvironmentOnAnotherDatabase(String code, String schema, String required) {
        assertThat(violations(code, false, schema)).containsExactly(
                "lego.test.environment-code '%s' must be connected to database schema '%s', not '%s'".formatted(code, required, schema));
    }

    @ParameterizedTest
    @ValueSource(strings = {"lego_sandbox", "lego_dev", "TESTDB", "lego_prod_legacy"})
    void rejectsProdOnAnyDatabaseButLegoProd(String schema) {
        assertThat(violations("prod", true, schema)).containsExactly(
                "lego.test.environment-code 'prod' must be connected to database schema 'lego_prod', not '%s'".formatted(schema));
    }

    @ParameterizedTest
    @ValueSource(strings = {"lego_prod", "LEGO_PROD"})
    void rejectsLocalOnTheProdDatabase(String schema) {
        assertThat(violations("local", false, schema)).containsExactly(
                "lego.test.environment-code 'local' is not PROD but is connected to PROD's database schema '%s'".formatted(schema));
    }

    @Test
    void rejectsProductionFlagOnALowerEnvironment() {
        // The PROD flag switches off every lower-environment guard, whatever database is connected.
        assertThat(violations("sandbox", true, "lego_sandbox")).containsExactly(
                "lego.test.production is true but environment-code 'sandbox' is not PROD");
        assertThat(violations("local", true, "lego_prod")).containsExactly(
                "lego.test.production is true but environment-code 'local' is not PROD");
    }

    @Test
    void rejectsProdWithoutTheProductionFlag() {
        assertThat(violations("prod", false, "lego_prod")).containsExactly(
                "lego.test.production is false but environment-code 'prod' is PROD");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"sandbx", "production", "  "})
    void rejectsAnUnknownEnvironmentCode(String code) {
        assertThat(violations(code, false, "lego_sandbox")).containsExactly(
                "lego.test.environment-code '%s' is not a known environment (local, sandbox, dev, prod)".formatted(code));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void rejectsAnUnknownConnectedSchema(String schema) {
        assertThat(violations("sandbox", false, schema)).containsExactly(
                "could not determine the connected database schema, so environment 'sandbox' cannot be verified");
    }

    @Test
    void messagesNameSettingsNotTickets() {
        assertThat(violations("sandbox", false, "lego_prod"))
                .allSatisfy(message -> assertThat(message).doesNotContainPattern("[A-Z]+-\\d+"));
    }
}
