package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CostType {
    @EqualsAndHashCode.Include
    private String costTypeCode;
    private String costTypeName;
    private String costTypeDescription;
}
