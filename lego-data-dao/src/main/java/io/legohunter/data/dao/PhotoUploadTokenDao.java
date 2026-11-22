package io.legohunter.data.dao;

import io.legohunter.data.dto.PhotoUploadToken;
import io.legohunter.data.mybatis.mapper.PhotoUploadTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PhotoUploadTokenDao {
    private final PhotoUploadTokenMapper photoUploadTokenMapper;

    public PhotoUploadToken insert(PhotoUploadToken token) {
        photoUploadTokenMapper.insert(token);
        return token;
    }

    public Optional<PhotoUploadToken> findByTokenHmac(String tokenHmac) {
        return photoUploadTokenMapper.findByTokenHmac(tokenHmac);
    }

    /** True only for the caller that retired the token; a concurrent second retire returns false. */
    public boolean retire(long photoUploadTokenId, ZonedDateTime retiredAt) {
        return photoUploadTokenMapper.retire(photoUploadTokenId, retiredAt) == 1;
    }

    public boolean revoke(long photoUploadTokenId, ZonedDateTime revokedAt) {
        return photoUploadTokenMapper.revoke(photoUploadTokenId, revokedAt) == 1;
    }

    /** Revokes every token of a login family. Returns how many rows changed. */
    public int revokeFamily(String familyId, ZonedDateTime revokedAt) {
        return photoUploadTokenMapper.revokeFamily(familyId, revokedAt);
    }

    /** Revokes every live token issued to a name in an environment. Returns how many rows changed. */
    public int revokeByName(String environment, String tokenName, ZonedDateTime revokedAt) {
        return photoUploadTokenMapper.revokeByName(environment, tokenName, revokedAt);
    }
}
