package io.legohunter.data.dto;

import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
public class ExternalService {
    private Integer externalServiceId;
    private String serviceCode;
    private String displayName;
    private String serviceUrl;
    private Integer externalServiceTypeId;
    private ExternalServiceType externalServiceType;
    private Set<ExternalServiceCapability> capabilities;

    @Getter
    public enum Service {
        BRICKLINK(2, "BRICKLINK"),
        EBAY(3, "EBAY"),
        REBRICKABLE(9, "REBRICKABLE"),
        FLICKR(10, "FLICKR");

        private final Integer externalServiceId;
        private final String serviceCode;

        Service(Integer externalServiceId, String serviceCode) {
            this.externalServiceId = externalServiceId;
            this.serviceCode = serviceCode;
        }
    }
}
