package io.legohunter.data.dto;

import io.legohunter.data.enums.ExternalSyncStatus;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ExternalImageAlbum {
    private Long externalImageAlbumId;
    private Integer externalServiceId;
    private Integer itemInventoryId;
    private String externalAlbumId;
    private String title;
    private String albumUrl;
    private String shortUrl;
    private ExternalSyncStatus syncStatus;
    private String errorMessage;
    private ZonedDateTime lastSyncedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
