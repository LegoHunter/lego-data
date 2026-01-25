package net.lego.data.v1.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class BricklinkInventory {
    private Integer blInventoryId;
    private String uuid;
    private Integer boxId;
    private Integer boxIndex;
    private Long inventoryId;
    private String blItemNo;
    private String orderId;
    private String itemType;
    private Integer colorId;
    private String colorName;
    private Integer quantity;
    private String newOrUsed;
    private String completeness;
    private Double unitPrice;
    private Integer bindId;
    private String description;
    private String remarks;
    private Integer bulk;
    private Boolean isRetain;
    private Boolean isStockRoom;
    private String stockRoomId;
    private LocalDateTime dateCreated;
    private Double myCost;
    private Integer saleRate;
    private Integer tierQuantity1;
    private Integer tierQuantity2;
    private Integer tierQuantity3;
    private Double tierPrice1;
    private Double tierPrice2;
    private Double tierPrice3;
    private Double myWeight;
    private Boolean sealed;
    private Boolean builtOnce;
    private Boolean forSale;
    private Boolean fixedPrice;
    private Integer boxConditionId;
    private Integer instructionsConditionId;
    private String internalComments;
    private Instant updateTimestamp;
    private Instant lastSynchronizedTimestamp;
    private String extendedDescription;
    private String extraDescription;
}
