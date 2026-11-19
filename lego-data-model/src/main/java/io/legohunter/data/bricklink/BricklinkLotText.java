package io.legohunter.data.bricklink;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * What a BrickLink lot shows as its Description and its Extended Description (HOMELAB-82). The one rule
 * for both services: lego-data-service checks a listing against it when the owner edits the listing, and
 * lego-data-ingress builds the lot from it, updates it, and checks it in reconciliation.
 *
 * <p>The Description holds only, each as a sentence (a text on its own is left as typed):
 * <ol>
 *   <li>the listing's short text: its description, otherwise its title;</li>
 *   <li>a clickable link to the item's photo album, when it has one;</li>
 *   <li>{@value #EXTENDED_DESCRIPTION_NOTE}, when at least one photo has a caption and this environment
 *       writes the Extended Description.</li>
 * </ol>
 * It must fit BrickLink's {@value #DESCRIPTION_MAX_LENGTH} characters. Nothing is ever trimmed: a text
 * that is too long is reported, and the owner shortens the listing's text.
 *
 * <p>The Extended Description holds the captions, one per line, in photo order, each once.
 *
 * <p>Pure functions, no I/O: the callers read the listing, the album and the captions.
 */
public final class BricklinkLotText {
    /** BrickLink refuses a longer Description with 409 RESOURCE_UPDATE_NOT_ALLOWED (measured 2026-11-14). */
    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final String EXTENDED_DESCRIPTION_NOTE = "See Extended Description for additional Item Condition notes.";
    /** The words of the album link. */
    public static final String ALBUM_LINK_TEXT = "View all photos";

    /**
     * A plain anchor. BrickLink keeps the tag, so buyers get a clickable link, but it drops a {@code target}
     * attribute (measured 2026-11-19), so none is sent: the text read back must equal the text sent.
     */
    private static final String ALBUM_LINK = "<a href=\"%s\">" + ALBUM_LINK_TEXT + "</a>";
    private static final String ALBUM_LINK_START = "<a href=\"";
    private static final String ALBUM_LINK_END = "\">" + ALBUM_LINK_TEXT + "</a>";

    private BricklinkLotText() {
    }

    /** The listing's own text for the lot: its description, otherwise its title; null when it has neither. */
    public static String shortText(String listingDescription, String listingTitle) {
        if (hasText(listingDescription)) {
            return listingDescription.strip();
        }
        return hasText(listingTitle) ? listingTitle.strip() : null;
    }

    /** The album address to link to: the short link when there is one, otherwise the album's own address. */
    public static String albumLink(String shortUrl, String albumUrl) {
        if (hasText(shortUrl)) {
            return shortUrl.strip();
        }
        return hasText(albumUrl) ? albumUrl.strip() : null;
    }

    /**
     * The lot's Description.
     *
     * @param shortText               from {@link #shortText}; null when the listing has neither description nor title
     * @param albumLink               from {@link #albumLink}; null when the item has no album address yet
     * @param anyCaption              whether at least one of the item's processed photos has a caption
     * @param extendedDescriptionSync whether this environment writes the Extended Description; without it the
     *                                note would point buyers at an empty field
     * @return the text, or {@link Description#NONE} when there is nothing to show (no short text)
     */
    public static Description description(String shortText, String albumLink, boolean anyCaption, boolean extendedDescriptionSync) {
        if (!hasText(shortText)) {
            return Description.NONE;
        }
        List<String> more = new ArrayList<>();
        if (hasText(albumLink)) {
            more.add(ALBUM_LINK.formatted(escapeAttribute(albumLink.strip())) + ".");
        }
        if (anyCaption && extendedDescriptionSync) {
            more.add(EXTENDED_DESCRIPTION_NOTE);
        }
        if (more.isEmpty()) {
            // On its own the text is shown as the owner typed it, as before HOMELAB-82: "Summer Cottage".
            return new Description(shortText.strip());
        }
        List<String> sentences = new ArrayList<>();
        sentences.add(sentence(shortText.strip()));
        sentences.addAll(more);
        return new Description(String.join(" ", sentences));
    }

    /**
     * The lot's Extended Description: the non-blank captions, stripped, one per line, in the order given
     * (photo order), each only once. Empty when there is no caption.
     */
    public static String extendedDescription(List<String> captionsInPhotoOrder) {
        Set<String> captions = new LinkedHashSet<>();
        if (captionsInPhotoOrder != null) {
            captionsInPhotoOrder.stream()
                    .filter(BricklinkLotText::hasText)
                    .map(String::strip)
                    .forEach(captions::add);
        }
        return String.join("\n", captions);
    }

    /** SHA-256 (64 hex characters) of a text, as recorded for the Extended Description last sent. */
    public static String hash(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(Objects.requireNonNullElse(text, "").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    /**
     * The number of characters the text takes on BrickLink, which is what must fit {@value #DESCRIPTION_MAX_LENGTH}.
     *
     * <p>Measured 2026-11-19 on a SANDBOX lot: BrickLink accepts a text of 255 characters as sent, stores it with
     * {@code &} written as {@code &amp;}, and then silently cuts the stored text at 255 characters, mid-link if need
     * be. So the escaped length is what counts. The album link's tag is stored as sent. For the rest of the text every
     * character BrickLink may escape is counted at its escaped length ({@code &} 5, {@code "} 6, {@code '} 5,
     * {@code <} and {@code >} 4): a count that can only be too high, never too low. The album link is counted as
     * sent, with an {@code &} in its address counted as 5.
     */
    public static int bricklinkLength(String text) {
        if (text == null) {
            return 0;
        }
        int length = 0;
        int index = 0;
        while (index < text.length()) {
            int linkStart = text.indexOf(ALBUM_LINK_START, index);
            int linkEnd = linkStart < 0 ? -1 : text.indexOf(ALBUM_LINK_END, linkStart);
            if (linkStart < 0 || linkEnd < 0) {
                length += escapedLength(text.substring(index));
                break;
            }
            length += escapedLength(text.substring(index, linkStart));
            String link = text.substring(linkStart, linkEnd + ALBUM_LINK_END.length());
            length += link.length() + 4 * (int) link.chars().filter(c -> c == '&').count();
            index = linkEnd + ALBUM_LINK_END.length();
        }
        return length;
    }

    private static int escapedLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            length += switch (text.charAt(i)) {
                case '&' -> 5;
                case '"' -> 6;
                case '\'' -> 5;
                case '<', '>' -> 4;
                default -> 1;
            };
        }
        return length;
    }

    private static String sentence(String text) {
        char last = text.charAt(text.length() - 1);
        return last == '.' || last == '!' || last == '?' ? text : text + ".";
    }

    /** An address inside {@code href="..."}: a double quote would end the attribute. */
    private static String escapeAttribute(String url) {
        return url.replace("\"", "%22");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * A composed Description.
     *
     * @param text the text to send, or null when there is nothing to show
     */
    public record Description(String text) {
        public static final Description NONE = new Description(null);

        public boolean present() {
            return text != null;
        }

        /** The length BrickLink counts. */
        public int length() {
            return bricklinkLength(text);
        }

        /** Whether BrickLink would refuse it. */
        public boolean tooLong() {
            return length() > DESCRIPTION_MAX_LENGTH;
        }

        /** The error the owner sees: what to fix and by how much. */
        public String tooLongMessage() {
            return "The BrickLink lot Description would be %d characters; BrickLink allows %d. Shorten the listing's description (or title) by at least %d characters."
                    .formatted(length(), DESCRIPTION_MAX_LENGTH, length() - DESCRIPTION_MAX_LENGTH);
        }
    }
}
