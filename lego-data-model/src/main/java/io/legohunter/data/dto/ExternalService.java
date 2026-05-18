package io.legohunter.data.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class ExternalService {
    private Integer externalServiceId;
    private String externalServiceName;
    private String externalServiceUrl;
    private Integer externalServiceTypeId;

    @Getter
    public enum ExternalServiceType {
        BRICKLINK(2),
        REBRICKABLE(9);

        private final Integer externalServiceId;

        ExternalServiceType(Integer externalServiceId) {
            this.externalServiceId = externalServiceId;
        }
    }
}
