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
    private String fileName;
    private Long fileSize;
    private Boolean primary;
    private String caption;
    private PhotoStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime uploadedAt;
}

