package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExternalItemInventory {
    private Integer externalItemId;
    private Integer itemInventoryId;
    private Boolean fixedPrice;
    private Integer orderId;
    private String extendedDescription;
    private String extraDescription;
    private String internalComments;
    private Instant updateTimestamp;
    private Instant lastSynchronizedTimestamp;
}