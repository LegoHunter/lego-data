package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInventoryState {
    private String inventoryStateCode;
    private String inventoryStateName;
    private String inventoryStateDescription;
    private Boolean active;
    private Integer sortOrder;
}
