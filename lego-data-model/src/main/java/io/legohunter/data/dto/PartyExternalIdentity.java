package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PartyExternalIdentity {
    private Long partyExternalIdentityId;
    private Long partyId;
    private Integer transactionPlatformId;
    private String externalPartyId;
    private ZonedDateTime createdAt;
}
