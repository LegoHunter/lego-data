package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalService {
    private Integer externalServiceId;
    private String externalServiceName;
    private String externalServiceUrl;
    private Integer externalServiceTypeId;
}
