package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class TransactionPartySnapshot {
    private Long transactionPartySnapshotId;
    private Long transactionId;
    private Long partyId;
    private String partyRoleCode;
    private String displayName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private String country;
    private String phone;
    private String email;
    private ZonedDateTime capturedAt;
}
