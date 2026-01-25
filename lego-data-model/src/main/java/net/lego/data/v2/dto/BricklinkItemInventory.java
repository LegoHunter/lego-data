package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class BricklinkItemInventory {
    private final Integer bricklinkItemInventoryId;
    private final Integer externalItemId;
    private final Integer itemInventoryId;
    private final Long inventoryId;
    private final String itemType;
    private final Integer colorId;
    private final String colorName;
    private final Integer quantity;
    private final Double unitPrice;
    private final Integer bindId;
    private final String description;
    private final String remarks;
    private final Integer bulk;
    private final Boolean isRetain;
    private final Boolean isStockRoom;
    private final String stockRoomId;
    private final ZonedDateTime dateCreated;
    private final Double myCost;
    private final Integer saleRate;
    private final Integer tierQuantity1;
    private final Integer tierQuantity2;
    private final Integer tierQuantity3;
    private final Double tierPrice1;
    private final Double tierPrice2;
    private final Double tierPrice3;
    private final Double myWeight;
}