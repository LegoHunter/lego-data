package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInventorySaleIntent {
    private String saleIntentCode;
    private String saleIntentName;
    private String saleIntentDescription;
    private Boolean active;
    private Integer sortOrder;
}
