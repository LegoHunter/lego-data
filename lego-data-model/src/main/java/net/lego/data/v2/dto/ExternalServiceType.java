package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalServiceType {
    private Integer externalServiceTypeId;
    private String externalServiceTypeName;
    private String externalServiceTypeDescription;
}
