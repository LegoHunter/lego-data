package net.lego.data.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalItem {
    private Integer itemId;
    private String number;
    private Long uniqueId;
    private String itemType;
    private String name;
    private String url;
    private Integer serviceId;
    private Integer categoryId;
    private Integer yearReleased;
    private ExternalServiceItem externalServiceItem;
}
