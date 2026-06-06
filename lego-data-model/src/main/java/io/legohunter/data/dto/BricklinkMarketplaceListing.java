package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BricklinkMarketplaceListing {
    private Integer marketplaceListingId;
    private Integer bricklinkInventoryId;
    private String bricklinkInventoryStatusCode;
    private ZonedDateTime bricklinkDateCreated;
    private Integer colorId;
    private String colorName;
    private Integer bindId;
    private Integer bulk;
    private Boolean isRetain;
    private Boolean isStockRoom;
    private String stockRoomId;
    private Integer saleRate;
    private Integer tierQuantity1;
    private BigDecimal tierPrice1;
    private Integer tierQuantity2;
    private BigDecimal tierPrice2;
    private Integer tierQuantity3;
    private BigDecimal tierPrice3;
    private BigDecimal myWeight;
    private String remarks;
    private Integer lastRemoteQuantity;
}


