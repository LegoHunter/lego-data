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
