package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ExternalImageAlbumImage {
    private Long externalImageAlbumId;
    private Long externalImageId;
    private Integer sortOrder;
    private Boolean primary;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
