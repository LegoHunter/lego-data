package net.lego.data.v2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Carrier {
    private String carrierCode;
    private String carrierName;
    private String trackingUrlPattern;
}
