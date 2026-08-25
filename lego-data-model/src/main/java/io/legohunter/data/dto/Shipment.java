package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Shipment {
    private Long shipmentId;
    private String externalShipmentId;
    private LocalDate shipmentDate;
    private String shipmentTrackingNumber;
    private String carrierCode;
    private String fulfillmentPlatformCode;
    private String serviceCode;
    private BigDecimal shipmentCost;
    private BigDecimal insuranceCost;
    private String currencyCode;
}
