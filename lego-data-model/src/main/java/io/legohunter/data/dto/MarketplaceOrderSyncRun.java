package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceOrderSyncRun {
    private Integer marketplaceOrderSyncRunId;
    private String marketplaceCode;
    private String syncJobName;
    private String syncDirection;
    private String syncStatusCode;
    private ZonedDateTime startedAt;
    private ZonedDateTime completedAt;
    private Integer ordersDiscovered;
    private Integer ordersFetched;
    private Integer ordersFailed;
    private String errorMessage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
