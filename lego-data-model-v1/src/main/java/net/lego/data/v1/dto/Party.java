package net.lego.data.v1.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Party {
    private Long partyId;
    private String partyFirstName;
    private String partyMiddleInitial;
    private String partyLastName;
    private String partyAddress1;
    private String partyAddress2;
    private String partyCity;
    private String partyState;
    private String partyPostalCode;
    private String partyCountryCode;
    private String partyCountry;
    private String partyPhone;
    private String partyEmail;
    private String partyType;
    private String partyPassword;
    private LocalDateTime partyActivationDate;
    private String partyActiveIndicator;
}
