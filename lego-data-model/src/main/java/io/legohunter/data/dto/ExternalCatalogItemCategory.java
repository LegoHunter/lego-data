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
public class ExternalCatalogItemCategory {
    private Integer externalCatalogItemId;
    private Integer externalCategoryId;
    private Boolean primary;
    private ZonedDateTime createdAt;
}


