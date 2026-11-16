package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;
import io.legohunter.data.enums.PhotoStatus;

import java.time.ZonedDateTime;

@Data
@Builder
public class ItemInventoryPhoto {
    private Integer itemInventoryPhotoId;
    private Integer itemInventoryId;
    private String s3Bucket;
    private String s3Key;
    private String md5;
    private String metadataHash;
    private String fileName;
    private Long fileSize;
    private Boolean primary;
    private String caption;
    /** This photo's own {@code sealed} keyword: true, false, or null when the photo has none (HOMELAB-96). */
    private Boolean sealedKeyword;
    /** This photo's own {@code bo} (built once) keyword: true, false, or null when the photo has none. */
    private Boolean builtOnceKeyword;
    /** Every keyword as read from the photo, joined with "; ". A record of what the photo said. */
    private String keywords;
    private PhotoStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime uploadedAt;
}

