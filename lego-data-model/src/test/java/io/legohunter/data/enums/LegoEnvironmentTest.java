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
    void eachLowerEnvironmentOwnsTheAgreedStockroomAndProdNone() {
        assertThat(LegoEnvironment.LOCAL.lowerEnvironmentStockroom()).contains(BricklinkStockroom.C);
        assertThat(LegoEnvironment.SANDBOX.lowerEnvironmentStockroom()).contains(BricklinkStockroom.C);
        assertThat(LegoEnvironment.DEV.lowerEnvironmentStockroom()).contains(BricklinkStockroom.B);
        assertThat(LegoEnvironment.PROD.lowerEnvironmentStockroom()).isEmpty();
    }

    @Test
    void onlyProdIsProduction() {
        assertThat(Arrays.stream(LegoEnvironment.values()).filter(LegoEnvironment::isProduction))
                .containsExactly(LegoEnvironment.PROD);
    }

    @Test
    void noLowerEnvironmentUsesProdsStockroomA() {
        assertThat(Arrays.stream(LegoEnvironment.values()))
                .allSatisfy(environment -> assertThat(environment.lowerEnvironmentStockroom()).isNotEqualTo(java.util.Optional.of(BricklinkStockroom.A)));
    }

    @Test
    void everyLowerEnvironmentHasAStockroom() {
        assertThat(Arrays.stream(LegoEnvironment.values()).filter(environment -> !environment.isProduction()))
                .allSatisfy(environment -> assertThat(environment.lowerEnvironmentStockroom()).isPresent());
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
