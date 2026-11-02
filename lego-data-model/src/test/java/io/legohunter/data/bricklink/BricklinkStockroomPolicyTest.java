package io.legohunter.data.bricklink;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkStockroomPolicyTest {

    private static final String PREFIX = "lego.test";
    private static final String SETTING = "non-prod-stock-room-id";

    @ParameterizedTest
    @CsvSource({"sandbox, C", "local, C", "dev, B", "' Sandbox ', ' c '", "DEV, b"})
    void acceptsEachLowerEnvironmentInItsOwnStockroom(String environment, String stockroom) {
        assertThat(violations(environment, false, stockroom)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sandbox", "local", "dev"})
    void rejectsStockroomAInEveryLowerEnvironment(String environment) {
        assertThat(violations(environment, false, "A"))
                .singleElement().asString()
                .contains("lego.test.non-prod-stock-room-id must not be A for environment '" + environment + "'");
        assertThat(violations(environment, false, " a ")).hasSize(1);
    }

    @ParameterizedTest
    @CsvSource({"sandbox, B", "local, B", "dev, C"})
    void rejectsAnotherEnvironmentsStockroom(String environment, String stockroom) {
        assertThat(violations(environment, false, stockroom))
                .singleElement().asString()
                .contains("for environment '" + environment + "' must be");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void rejectsAMissingStockroomOutsideProd(String stockroom) {
        assertThat(violations("sandbox", false, stockroom))
                .singleElement().asString()
                .contains("lego.test.non-prod-stock-room-id must be set for environment 'sandbox' (expected C)");
    }

    @ParameterizedTest
    @ValueSource(strings = {"D", "AB", "1", "stockroom-c"})
    void rejectsValuesThatAreNotBricklinkStockrooms(String stockroom) {
        assertThat(violations("sandbox", false, stockroom))
                .singleElement().asString().contains("is not a BrickLink stockroom");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"sandbx", "test", "some-new-env", "production"})
    void rejectsUnknownEnvironmentCodesSoATypoCannotBorrowAnotherStockroom(String environment) {
        // Before the shared enum, an unknown code could use B or C, so a typo such as "sandbx"
        // could quietly use DEV's stockroom B.
        assertThat(violations(environment, false, "B"))
                .singleElement().asString()
                .contains("lego.test.environment-code")
                .contains("is not a known environment (local, sandbox, dev, prod)");
    }

    @Test
    void rejectsAProductionFlagThatDisagreesWithTheEnvironment() {
        assertThat(violations("sandbox", true, "C"))
                .singleElement().asString().contains("lego.test.production is true but environment-code 'sandbox' is not PROD");
        assertThat(violations("prod", false, "C"))
                .singleElement().asString().contains("lego.test.production is false but environment-code 'prod' is PROD");
    }

    @Test
    void prodHasNoNonProdStockroomRule() {
        assertThat(violations("prod", true, null)).isEmpty();
        assertThat(violations("prod", true, "A")).isEmpty();
    }

    @Test
    void messagesNameNoTicket() {
        assertThat(violations("sandbox", false, "A")).allSatisfy(message -> assertThat(message).doesNotContain("HOMELAB"));
    }

    private static List<String> violations(String environment, boolean production, String stockroom) {
        return BricklinkStockroomPolicy.violations(PREFIX, SETTING, environment, production, stockroom);
    }
}
