package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * The fields LEGO publishes for a wooden toy, one per catalog entry (HOMELAB-157).
 *
 * <p>The entry itself is an {@link ExternalCatalogItem} of the {@code LEGO} service with item type
 * {@link #ITEM_TYPE_CODE}, keyed by the entry's lego.com address slug. This holds what that table has no column for.
 * Product Number is not the key: LEGO reused numbers for different toys.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegoWoodenToyCatalogItem {
    /** {@code item_type_code} of a wooden toys catalog entry. */
    public static final String ITEM_TYPE_CODE = "W";
    /** An entry captured from lego.com. A capture may change it. */
    public static final String SOURCE_LEGO_COM = "LEGO_COM";
    /** An entry the owner added for a toy the catalog does not list. A capture never changes it. */
    public static final String SOURCE_OWNER = "OWNER";

    private Integer externalCatalogItemId;
    private String entrySource;
    private String productNumber;
    private String productNumberSortable;
    /** Alternate Number as written: a note on renumbering, not a number. */
    private String alternateNumberText;
    private String originalTitle;
    private String englishTitle;
    /** As written. It can hold two years or a decade, so it is text. */
    private String launchYearText;
    /** As written. It can hold two years or N/A, so it is text. */
    private String exitYearText;
    private String averageSizeText;
    private String seriesText;
    private String notesOriginal;
    private String notesEnglish;
    private Integer sourceVersion;
    private ZonedDateTime sourceUpdatedAt;
    private ZonedDateTime capturedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
