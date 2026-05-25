package io.legohunter.data.dto;

import io.legohunter.data.enums.ExternalSyncStatus;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ExternalImage {
    private Long externalImageId;
    private Integer externalServiceId;
    private Integer itemInventoryPhotoId;
    private String externalServiceImageId;
    private String title;
    private String imageUrl;
    private String md5AtUpload;
    private String metadataHashAtSync;
    private ExternalSyncStatus syncStatus;
    private String errorMessage;
    private ZonedDateTime uploadedAt;
    private ZonedDateTime lastSyncedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
