package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.PhotoUploadToken;
import io.legohunter.data.enums.PhotoUploadTokenKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {"/scripts/db/h2/photo_upload_token_schema.ddl"})
class PhotoUploadTokenDaoTest {

    @Autowired PhotoUploadTokenDao photoUploadTokenDao;

    @Test
    void insertedTokenIsFoundByItsHmac() {
        PhotoUploadToken saved = photoUploadTokenDao.insert(refreshToken("hmac-found"));

        assertThat(saved.getPhotoUploadTokenId()).isNotNull();
        PhotoUploadToken found = photoUploadTokenDao.findByTokenHmac("hmac-found").orElseThrow();
        assertThat(found.getTokenKind()).isEqualTo(PhotoUploadTokenKind.REFRESH);
        assertThat(found.getEnvironment()).isEqualTo("sandbox");
        assertThat(found.getRetiredAt()).isNull();
        assertThat(found.getRevokedAt()).isNull();
    }

    @Test
    void unknownHmacFindsNothing() {
        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-missing")).isEmpty();
    }

    @Test
    void aRefreshTokenCanBeRetiredOnlyOnce() {
        PhotoUploadToken saved = photoUploadTokenDao.insert(refreshToken("hmac-retire"));
        ZonedDateTime now = ZonedDateTime.now();

        assertThat(photoUploadTokenDao.retire(saved.getPhotoUploadTokenId(), now)).isTrue();
        assertThat(photoUploadTokenDao.retire(saved.getPhotoUploadTokenId(), now)).isFalse();
        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-retire").orElseThrow().getRetiredAt()).isNotNull();
    }

    @Test
    void aRevokedTokenCannotBeRetired() {
        PhotoUploadToken saved = photoUploadTokenDao.insert(refreshToken("hmac-revoked"));
        ZonedDateTime now = ZonedDateTime.now();

        assertThat(photoUploadTokenDao.revoke(saved.getPhotoUploadTokenId(), now)).isTrue();
        assertThat(photoUploadTokenDao.retire(saved.getPhotoUploadTokenId(), now)).isFalse();
    }

    @Test
    void revokingAFamilyRevokesEveryLiveTokenOfTheLoginOnly() {
        PhotoUploadToken first = photoUploadTokenDao.insert(refreshToken("hmac-fam-1"));
        PhotoUploadToken second = photoUploadTokenDao.insert(PhotoUploadToken.builder()
                .environment("sandbox").tokenName("owner-laptop").tokenKind(PhotoUploadTokenKind.ACCESS)
                .tokenHmac("hmac-fam-2").familyId(first.getFamilyId())
                .expiresAt(ZonedDateTime.now().plusHours(1)).build());
        PhotoUploadToken otherLogin = photoUploadTokenDao.insert(refreshToken("hmac-other"));

        assertThat(photoUploadTokenDao.revokeFamily(first.getFamilyId(), ZonedDateTime.now())).isEqualTo(2);

        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-fam-1").orElseThrow().getRevokedAt()).isNotNull();
        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-fam-2").orElseThrow().getRevokedAt()).isNotNull();
        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-other").orElseThrow().getRevokedAt()).isNull();
        assertThat(second.getFamilyId()).isEqualTo(first.getFamilyId());
        assertThat(otherLogin.getFamilyId()).isNotEqualTo(first.getFamilyId());
    }

    @Test
    void revokingByNameStaysWithinTheEnvironment() {
        photoUploadTokenDao.insert(PhotoUploadToken.builder()
                .environment("sandbox").tokenName("name-revoke-test").tokenKind(PhotoUploadTokenKind.REFRESH)
                .tokenHmac("hmac-name-sandbox").familyId(UUID.randomUUID().toString())
                .expiresAt(ZonedDateTime.now().plusHours(12)).build());
        photoUploadTokenDao.insert(PhotoUploadToken.builder()
                .environment("dev").tokenName("name-revoke-test").tokenKind(PhotoUploadTokenKind.REFRESH)
                .tokenHmac("hmac-name-dev").familyId(UUID.randomUUID().toString())
                .expiresAt(ZonedDateTime.now().plusHours(12)).build());

        assertThat(photoUploadTokenDao.revokeByName("sandbox", "name-revoke-test", ZonedDateTime.now())).isEqualTo(1);

        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-name-sandbox").orElseThrow().getRevokedAt()).isNotNull();
        assertThat(photoUploadTokenDao.findByTokenHmac("hmac-name-dev").orElseThrow().getRevokedAt()).isNull();
    }

    private PhotoUploadToken refreshToken(String hmac) {
        return PhotoUploadToken.builder()
                .environment("sandbox")
                .tokenName("owner-laptop")
                .tokenKind(PhotoUploadTokenKind.REFRESH)
                .tokenHmac(hmac)
                .familyId(UUID.randomUUID().toString())
                .expiresAt(ZonedDateTime.now().plusHours(12))
                .build();
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}
