package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ItemInventorySearchCriteria {
    private String itemNumber;
    private String description;
    private Integer boxNumber;
    private String inventoryStateCode;
    private String saleIntentCode;
    private Boolean active;
    private String newOrUsed;
    private String completeness;
    private String itemConditionCode;
    private String boxConditionCode;
    private String instructionsConditionCode;
    private LocalDate transactionDateFrom;
    private LocalDate transactionDateTo;
    @Builder.Default
    private Integer limit = 100;
    @Builder.Default
    private Integer offset = 0;
}
