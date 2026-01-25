package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInventory {
    private Integer itemInventoryId;
    private String uuid;
    private Integer itemId;
    private Integer boxNumber;
    private Integer quantity;
    private String description;
    private Boolean active;
    private Boolean forSale;
    private String newOrUsed;
    private String completeness;
    private Integer itemConditionId;
    private Integer boxConditionId;
    private Integer instructionsConditionId;
    private Boolean sealed;
    private Boolean builtOnce;
}
