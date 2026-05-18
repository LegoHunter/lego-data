package net.bricklink.data.lego.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class BricklinkSaleItem {
    private Integer blSaleItemId;
    private Long blItemId;
    private Integer inventoryId;
    private Integer quantity;
    private String newOrUsed;
    private String completeness;
    private Double unitPrice;
    private String description;
    private Boolean hasExtendedDescription;
    private ZonedDateTime dateCreated;
    private String countryCode;
    private String status;
}