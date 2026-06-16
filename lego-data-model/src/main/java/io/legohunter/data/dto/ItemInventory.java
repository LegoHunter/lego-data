package io.legohunter.data.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
public class ItemInventory {
    private Integer itemInventoryId;
    private String uuid;
    private Integer boxNumber;
    private BigDecimal purchasePrice;
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
    private String inventoryStateCode;
    private ZonedDateTime inventoryStateChangedAt;
    private String saleIntentCode;
    private ZonedDateTime saleIntentUpdatedAt;
    private String saleIntentNote;
    private Set<ItemInventoryExternalCatalogItem> externalCatalogItems;
}
