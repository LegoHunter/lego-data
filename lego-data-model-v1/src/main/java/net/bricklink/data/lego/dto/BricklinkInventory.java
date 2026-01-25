package net.bricklink.data.lego.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Slf4j
public class BricklinkInventory {
    private Integer blInventoryId;
    private String uuid;
    private Integer boxId;
    private Integer boxIndex;
    private Integer itemId;
    private String itemName;
    private String itemNumber;
    private Long blItemId;
    private String blItemNo;
    private Long inventoryId;
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
    private String boxConditionCode;
    private Integer instructionsConditionId;
    private String instructionsConditionCode;
    private String extendedDescription;
    private String extraDescription;
    private String internalComments;
    private Instant updateTimestamp;
    private Instant lastSynchronizedTimestamp;

    public boolean shouldSynchronize() {
        Optional<Instant> lastSynchronizedTimestamp = Optional.ofNullable(getLastSynchronizedTimestamp());
        Optional<Instant> updateTimestamp = Optional.ofNullable(getUpdateTimestamp());
        return (lastSynchronizedTimestamp.isEmpty() || updateTimestamp.isEmpty() || lastSynchronizedTimestamp.get().isBefore(getUpdateTimestamp()));
    }

    public boolean getSealed() {
        return Optional.ofNullable(sealed).orElse(false);
    }

    public boolean canBeAvailableForSale() {
        boolean isForSale = Optional.ofNullable(this.getForSale()).orElse(false);
        boolean hasInventoryId = Optional.ofNullable(this.getInventoryId()).isPresent();
        boolean hasInstructionsCondition = Optional.ofNullable(this.getInstructionsConditionId()).isPresent();
        boolean hasBoxCondition = Optional.ofNullable(this.getBoxConditionId()).isPresent();
        boolean hasUnitPrice = Optional.ofNullable(this.getUnitPrice()).map(d -> d > 0.00d).orElse(false);
        boolean hasOrder = Optional.ofNullable(this.getOrderId()).isPresent();
        boolean canBeForSale = isForSale && hasInventoryId && hasInstructionsCondition && hasBoxCondition && hasUnitPrice && !hasOrder;
        if (!canBeForSale) {
            log.warn("[{} - {}] isForSale [{}], hasInventoryId [{}], hasInstructionsCondition [{}], hasBoxCondition [{}], hasUnitPrice [{}], hasOrder [{}]", this.getBlItemNo(), this.getUuid(), isForSale, hasInventoryId, hasInstructionsCondition, hasBoxCondition, hasUnitPrice, hasOrder);
        }
        return canBeForSale;
    }

    public static BricklinkInventory fromKeywords(final Map<String, String> keywords) {
        BricklinkInventory bricklinkInventory = new BricklinkInventory();
        Optional.ofNullable(keywords.get("uuid"))
                .ifPresent(bricklinkInventory::setUuid);
        Optional.ofNullable(keywords.get("bl"))
                .ifPresent(bricklinkInventory::setBlItemNo);
        Optional.ofNullable(keywords.get("sealed"))
                .ifPresent(v -> bricklinkInventory.setSealed(Boolean.valueOf(v)));
        Optional.ofNullable(keywords.get("bc"))
                .ifPresent(bricklinkInventory::setBoxConditionCode);
        Optional.ofNullable(keywords.get("ic"))
                .ifPresent(bricklinkInventory::setInstructionsConditionCode);
        Optional.ofNullable(keywords.get("bo"))
                .ifPresent(v -> bricklinkInventory.setBuiltOnce(Boolean.valueOf(v)));
        return bricklinkInventory;
    }
}