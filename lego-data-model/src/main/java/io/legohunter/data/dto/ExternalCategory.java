package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCategory {
    private Integer externalCategoryId;
    private Integer externalServiceId;
    private String externalCategoryKey;
    private String categoryName;
    private Integer parentExternalCategoryId;
}


