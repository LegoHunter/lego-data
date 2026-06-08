package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInventoryExternalCatalogItem {
    private Integer itemInventoryId;
    private Integer externalCatalogItemId;
    private Boolean primary;
    private ZonedDateTime createdAt;
    private ExternalCatalogItem externalCatalogItem;
}


