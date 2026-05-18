package io.legohunter.data.dto;

import lombok.Data;

@Data
public class Category {
    private Integer externalServiceId;
    private Integer externalCategoryId;
    private String categoryName;
    private Integer parentId;
}
