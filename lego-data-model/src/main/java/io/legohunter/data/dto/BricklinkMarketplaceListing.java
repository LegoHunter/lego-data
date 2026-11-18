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
    private String environmentCode;
    private String systemRemarksHash;
    private ZonedDateTime lastRemoteVerifiedAt;
    private String lastRemoteSafetyStatusCode;
    private String lastRemoteSafetyMessage;
    /**
     * The md5 of the {@code item_inventory_photo} last stored as the lot's custom image; null when
     * none has been stored (HOMELAB-92). Read-only here: the general insert and update leave it
     * alone, so a stale copy of this row cannot overwrite it. It is written by
     * {@code updateLotImage} only.
     */
    private String lotImageMd5;
    /** When the web gateway confirmed the photo in {@link #lotImageMd5} on the lot. Written with it. */
    private ZonedDateTime lotImageSyncedAt;
}


