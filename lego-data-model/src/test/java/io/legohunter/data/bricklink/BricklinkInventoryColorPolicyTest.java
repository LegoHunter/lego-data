package io.legohunter.data.bricklink;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkInventoryColorPolicyTest {
    @Test
    void resolvesMissingSetColorToNotApplicable() {
        assertThat(BricklinkInventoryColorPolicy.resolve("SET", null))
                .isEqualTo(new BricklinkInventoryColorPolicy.Resolution(true, 0, null, null));
    }

    @Test
    void acceptsSetAliasWithNotApplicableColor() {
        assertThat(BricklinkInventoryColorPolicy.resolve("s", 0).valid()).isTrue();
    }

    @Test
    void rejectsColoredSet() {
        BricklinkInventoryColorPolicy.Resolution result = BricklinkInventoryColorPolicy.resolve("SET", 1);

        assertThat(result.valid()).isFalse();
        assertThat(result.errorCode()).isEqualTo(BricklinkInventoryColorPolicy.INVALID_COLOR_ID);
    }

    @Test
    void requiresPositiveColorForColorSpecificItemType() {
        assertThat(BricklinkInventoryColorPolicy.resolve("PART", null).errorCode())
                .isEqualTo(BricklinkInventoryColorPolicy.MISSING_COLOR_ID);
        assertThat(BricklinkInventoryColorPolicy.resolve("P", 0).errorCode())
                .isEqualTo(BricklinkInventoryColorPolicy.INVALID_COLOR_ID);
        assertThat(BricklinkInventoryColorPolicy.resolve("PART", 5).effectiveColorId()).isEqualTo(5);
    }
}
