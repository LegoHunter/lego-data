package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private int itemId;
    private String itemNumber;
    private String itemName;
    private String notes;
    private Boolean isObsolete;
}
