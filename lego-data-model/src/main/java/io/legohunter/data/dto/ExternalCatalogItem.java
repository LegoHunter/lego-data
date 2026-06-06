package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCatalogItem {
    private Integer externalCatalogItemId;
    private Integer externalServiceId;
    private String externalItemKey;
    private String externalUniqueKey;
    private String itemName;
    private String itemTypeCode;
    private String itemUrl;
    private Integer yearReleased;
    private ExternalService externalService;
    private Set<ExternalCategory> categories;
}


