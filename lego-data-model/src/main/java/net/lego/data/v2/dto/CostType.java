package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CostType {
    private String costTypeCode;
    private String costTypeName;
    private String costTypeDescription;
}
