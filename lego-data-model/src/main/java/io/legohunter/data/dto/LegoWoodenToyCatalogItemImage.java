package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

/**
 * One picture of a wooden toys catalog entry (HOMELAB-157). An entry can have several.
 *
 * <p>{@link #image} holds the bytes and is filled only by the query that reads one picture's bytes. The queries that
 * list an entry's pictures leave it null, so listing pictures never loads megabytes.
 *
 * <p>These pictures belong to LEGO or to whoever made them. They stay inside the system and are never published.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegoWoodenToyCatalogItemImage {
    /** A picture captured from lego.com. A capture may replace or remove it. */
    public static final String SOURCE_LEGO_COM = "LEGO_COM";
    /** A picture the owner added from another source. A capture never changes it. */
    public static final String SOURCE_OWNER = "OWNER";

    private Long legoWoodenToyCatalogItemImageId;
    private Integer externalCatalogItemId;
    private Integer sortOrder;
    private String imageSource;
    @ToString.Exclude
    private byte[] image;
    private String contentType;
    private String fileName;
    private Integer width;
    private Integer height;
    private Long byteSize;
    /** SHA-256 of the bytes, lower-case hex. Unique within an entry. */
    private String sha256;
    /** The picture's id on lego.com. Null for a picture the owner added. */
    private String sourceAssetId;
    private String sourceUrl;
    /** For a picture the owner added: who made or posted it, and where. */
    private String sourceNote;
    private ZonedDateTime createdAt;
}
