package net.lego.data.v2.enums;

import org.junit.jupiter.api.Test;

import static net.lego.data.v2.enums.PhotoStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class PhotoStatusTest {
    @Test
    void transitionFails() {
        assertThat(UPLOADED.canTransitionTo(UPLOADED)).isTrue();
        assertThat(UPLOADED.canTransitionTo(PROCESSED)).isTrue();
        assertThat(UPLOADED.canTransitionTo(FAILED)).isTrue();

        assertThat(PROCESSED.canTransitionTo(UPLOADED)).isFalse();
        assertThat(PROCESSED.canTransitionTo(PROCESSED)).isTrue();
        assertThat(PROCESSED.canTransitionTo(FAILED)).isFalse();

        assertThat(FAILED.canTransitionTo(UPLOADED)).isTrue();
        assertThat(FAILED.canTransitionTo(PROCESSED)).isTrue();
        assertThat(FAILED.canTransitionTo(FAILED)).isTrue();
    }

}