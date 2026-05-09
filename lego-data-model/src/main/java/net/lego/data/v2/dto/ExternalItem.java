package net.lego.data.v2.dto;

import lombok.Data;

@Data
public class ExternalItem {
    private Integer externalItemId;
    private String externalNumber;
    private Long uniqueId;
    private String itemType;
    private String name;
    private String url;
    private Integer serviceId;
    private Integer categoryId;
    private Integer yearReleased;
    private ExternalService externalService;
    private Category category;
}
