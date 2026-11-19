package io.legohunter.data.bricklink;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkLotTextTest {
    private static final String ALBUM = "https://www.flickr.com/photos/205153404@N04/sets/72177720336783526/";
    private static final String LINK = "<a href=\"" + ALBUM + "\">View all photos</a>.";

    @Test
    void shortTextIsTheDescriptionOtherwiseTheTitle() {
        assertThat(BricklinkLotText.shortText("  Box has shelf wear  ", "Test Car")).isEqualTo("Box has shelf wear");
        assertThat(BricklinkLotText.shortText("", "Test Car")).isEqualTo("Test Car");
        assertThat(BricklinkLotText.shortText(null, " Test Car ")).isEqualTo("Test Car");
        assertThat(BricklinkLotText.shortText("  ", null)).isNull();
    }

    @Test
    void albumLinkPrefersTheShortLink() {
        assertThat(BricklinkLotText.albumLink("https://flic.kr/s/abc", ALBUM)).isEqualTo("https://flic.kr/s/abc");
        assertThat(BricklinkLotText.albumLink(" ", ALBUM + " ")).isEqualTo(ALBUM);
        assertThat(BricklinkLotText.albumLink(null, null)).isNull();
    }

    @Test
    void textOnlyIsLeftAsTyped() {
        assertThat(BricklinkLotText.description(" Test Car ", null, false, true).text()).isEqualTo("Test Car");
        assertThat(BricklinkLotText.description("Test Car", null, true, false).text()).isEqualTo("Test Car");
    }

    @Test
    void textAndLink() {
        assertThat(BricklinkLotText.description("Test Car", ALBUM, false, true).text()).isEqualTo("Test Car. " + LINK);
    }

    @Test
    void textLinkAndNoteWhenAPhotoHasACaption() {
        assertThat(BricklinkLotText.description("Test Car", ALBUM, true, true).text())
                .isEqualTo("Test Car. " + LINK + " See Extended Description for additional Item Condition notes.");
    }

    @Test
    void noNoteWhereTheExtendedDescriptionIsNotWritten() {
        assertThat(BricklinkLotText.description("Test Car", ALBUM, true, false).text()).isEqualTo("Test Car. " + LINK);
    }

    @Test
    void noteWithoutALinkYet() {
        assertThat(BricklinkLotText.description("Test Car", null, true, true).text())
                .isEqualTo("Test Car. See Extended Description for additional Item Condition notes.");
    }

    @Test
    void aSentenceKeepsItsOwnEnding() {
        assertThat(BricklinkLotText.description("Sealed!", ALBUM, false, true).text()).isEqualTo("Sealed! " + LINK);
        assertThat(BricklinkLotText.description("Complete?", ALBUM, false, true).text()).isEqualTo("Complete? " + LINK);
        assertThat(BricklinkLotText.description("Complete.", ALBUM, false, true).text()).isEqualTo("Complete. " + LINK);
    }

    @Test
    void nothingToShowWithoutShortText() {
        BricklinkLotText.Description description = BricklinkLotText.description(null, ALBUM, true, true);
        assertThat(description.present()).isFalse();
        assertThat(description.tooLong()).isFalse();
    }

    @Test
    void aQuoteInTheAddressCannotEndTheAttribute() {
        assertThat(BricklinkLotText.description("Test Car", "https://x.test/a\"b", false, true).text())
                .contains("href=\"https://x.test/a%22b\"");
    }

    @Test
    void exactly255FitsAnd256IsTooLong() {
        String fits = "x".repeat(255);
        BricklinkLotText.Description atLimit = BricklinkLotText.description(fits, null, false, true);
        assertThat(atLimit.length()).isEqualTo(255);
        assertThat(atLimit.tooLong()).isFalse();

        BricklinkLotText.Description over = BricklinkLotText.description(fits + "x", null, false, true);
        assertThat(over.length()).isEqualTo(256);
        assertThat(over.tooLong()).isTrue();
        assertThat(over.tooLongMessage()).contains("256 characters", "allows 255", "at least 1 characters");
    }

    @Test
    void theLengthIsCountedAsBricklinkStoresIt() {
        // 2026-11-19: "M2 & check" came back as "M2 &amp; check" and the stored text was cut at 255.
        assertThat(BricklinkLotText.bricklinkLength("a & b")).isEqualTo(9);
        assertThat(BricklinkLotText.bricklinkLength("say \"hi\" it's <3>")).isEqualTo(3 + 1 + 6 + 2 + 6 + 1 + 2 + 5 + 1 + 1 + 4 + 1 + 4);
        // The link's own tag is stored as sent.
        assertThat(BricklinkLotText.bricklinkLength(LINK)).isEqualTo(LINK.length());
        assertThat(BricklinkLotText.bricklinkLength("A & B. " + LINK)).isEqualTo(11 + LINK.length());

        // 252 characters as typed, 256 as stored: too long, although it would pass BrickLink's own check.
        assertThat(BricklinkLotText.description("x".repeat(250) + " &", null, false, true).tooLong()).isTrue();
        assertThat(BricklinkLotText.description("x".repeat(249) + " &", null, false, true).tooLong()).isFalse();
        assertThat(BricklinkLotText.description("x".repeat(247) + " & y", null, false, true).length()).isEqualTo(255);
    }

    @Test
    void extendedDescriptionIsTheCaptionsOnePerLineInOrderEachOnce() {
        assertThat(BricklinkLotText.extendedDescription(Arrays.asList(
                " Box has a dent. ", null, "  ", "Sticker sheet applied.", "Box has a dent.")))
                .isEqualTo("Box has a dent.\nSticker sheet applied.");
        assertThat(BricklinkLotText.extendedDescription(List.of())).isEmpty();
        assertThat(BricklinkLotText.extendedDescription(null)).isEmpty();
    }

    @Test
    void hashIsSha256Hex() {
        assertThat(BricklinkLotText.hash("")).isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        assertThat(BricklinkLotText.hash(null)).isEqualTo(BricklinkLotText.hash(""));
        assertThat(BricklinkLotText.hash("a")).hasSize(64).isNotEqualTo(BricklinkLotText.hash("b"));
    }
}
