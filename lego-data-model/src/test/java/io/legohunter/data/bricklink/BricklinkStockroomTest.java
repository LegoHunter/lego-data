package io.legohunter.data.bricklink;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkStockroomTest {

    @ParameterizedTest
    @CsvSource({"A, A", "b, B", "' c ', C"})
    void parsesAStockroomId(String id, BricklinkStockroom expected) {
        assertThat(BricklinkStockroom.fromId(id)).contains(expected);
        assertThat(expected.id()).isEqualTo(expected.name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"D", "AB", " ", "1"})
    void rejectsAnythingElse(String id) {
        assertThat(BricklinkStockroom.fromId(id)).isEmpty();
    }
}
