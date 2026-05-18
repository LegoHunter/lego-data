package net.lego.data.v1.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BricklinkItem {
    private Integer itemId;
    private String blItemNumber;
    private Integer blItemId;
}
