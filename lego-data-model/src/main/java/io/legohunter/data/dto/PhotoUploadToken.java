package io.legohunter.data.dto;

import io.legohunter.data.enums.PhotoUploadTokenKind;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/** One issued photo-upload token. Only the HMAC of the token is stored (HOMELAB-134). */
@Data
@Builder
public class PhotoUploadToken {
    private Long photoUploadTokenId;
    private String environment;
    private String tokenName;
    private PhotoUploadTokenKind tokenKind;
    private String tokenHmac;
    private String familyId;
    private ZonedDateTime expiresAt;
    private ZonedDateTime retiredAt;
    private ZonedDateTime revokedAt;
    private ZonedDateTime createdAt;
}
