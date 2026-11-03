package io.legohunter.data.enums;

import io.legohunter.data.bricklink.BricklinkStockroom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class LegoEnvironmentTest {

    @Test
    void eachEnvironmentOwnsTheAgreedStockroom() {
        assertThat(LegoEnvironment.LOCAL.stockroom()).isEqualTo(BricklinkStockroom.C);
        assertThat(LegoEnvironment.SANDBOX.stockroom()).isEqualTo(BricklinkStockroom.C);
        assertThat(LegoEnvironment.DEV.stockroom()).isEqualTo(BricklinkStockroom.B);
        assertThat(LegoEnvironment.PROD.stockroom()).isEqualTo(BricklinkStockroom.A);
    }

    @Test
    void everyEnvironmentOwnsAStockroom() {
        assertThat(LegoEnvironment.values()).allSatisfy(environment -> assertThat(environment.stockroom()).isNotNull());
    }

    @Test
    void onlyProdIsProduction() {
        assertThat(Arrays.stream(LegoEnvironment.values()).filter(LegoEnvironment::isProduction))
                .containsExactly(LegoEnvironment.PROD);
    }

    @Test
    void everyEnvironmentExceptProdForcesItsLotsIntoItsStockroom() {
        // PROD's lots are public by default; forcing them into stockroom A would hide the whole store.
        assertThat(LegoEnvironment.PROD.forcesStockroom()).isFalse();
        assertThat(Arrays.stream(LegoEnvironment.values()).filter(LegoEnvironment::forcesStockroom))
                .containsExactlyInAnyOrder(LegoEnvironment.LOCAL, LegoEnvironment.SANDBOX, LegoEnvironment.DEV);
    }

    @Test
    void onlyProdOwnsStockroomA() {
        assertThat(Arrays.stream(LegoEnvironment.values()).filter(environment -> environment.stockroom() == BricklinkStockroom.A))
                .containsExactly(LegoEnvironment.PROD);
    }

    @Test
    void eachEnvironmentNamesItsDatabaseSchema() {
        assertThat(LegoEnvironment.SANDBOX.databaseSchema()).contains("lego_sandbox");
        assertThat(LegoEnvironment.DEV.databaseSchema()).contains("lego_dev");
        assertThat(LegoEnvironment.PROD.databaseSchema()).contains("lego_prod");
        // A developer run may use any non-PROD schema, including the in-memory test database.
        assertThat(LegoEnvironment.LOCAL.databaseSchema()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"local, LOCAL", "sandbox, SANDBOX", "dev, DEV", "prod, PROD", "' Sandbox ', SANDBOX", "DEV, DEV"})
    void findsAnEnvironmentByItsCode(String code, LegoEnvironment expected) {
        assertThat(LegoEnvironment.fromCode(code)).contains(expected);
        assertThat(expected.code()).isEqualTo(expected.name().toLowerCase());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"sandbx", "production", "test", "staging", "  "})
    void anUnknownCodeIsNotAnEnvironment(String code) {
        assertThat(LegoEnvironment.fromCode(code)).isEmpty();
    }
}
