package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PhotoUploadToken;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface PhotoUploadTokenMapper {
    String ALL_COLUMNS = """
            photo_upload_token_id,
            environment,
            token_name,
            token_kind,
            token_hmac,
            family_id,
            expires_at,
            retired_at,
            revoked_at,
            created_at
            """;

    @Insert("""
            INSERT INTO photo_upload_token (environment, token_name, token_kind, token_hmac, family_id, expires_at)
            VALUES (#{environment}, #{tokenName}, #{tokenKind}, #{tokenHmac}, #{familyId}, #{expiresAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "photoUploadTokenId", keyColumn = "photo_upload_token_id")
    int insert(PhotoUploadToken token);

    @Select("SELECT " + ALL_COLUMNS + " FROM photo_upload_token WHERE token_hmac = #{tokenHmac}")
    @Results(id = "photoUploadTokenResultMap", value = {
            @Result(property = "photoUploadTokenId", column = "photo_upload_token_id", id = true),
            @Result(property = "environment", column = "environment"),
            @Result(property = "tokenName", column = "token_name"),
            @Result(property = "tokenKind", column = "token_kind"),
            @Result(property = "tokenHmac", column = "token_hmac"),
            @Result(property = "familyId", column = "family_id"),
            @Result(property = "expiresAt", column = "expires_at"),
            @Result(property = "retiredAt", column = "retired_at"),
            @Result(property = "revokedAt", column = "revoked_at"),
            @Result(property = "createdAt", column = "created_at")
    })
    Optional<PhotoUploadToken> findByTokenHmac(@Param("tokenHmac") String tokenHmac);

    /** Marks a refresh token as retired. Returns 1 only for the first caller, so two refreshes cannot both win. */
    @Update("""
            UPDATE photo_upload_token
            SET retired_at = #{retiredAt}
            WHERE photo_upload_token_id = #{photoUploadTokenId}
              AND retired_at IS NULL
              AND revoked_at IS NULL
            """)
    int retire(@Param("photoUploadTokenId") long photoUploadTokenId, @Param("retiredAt") ZonedDateTime retiredAt);

    @Update("""
            UPDATE photo_upload_token
            SET revoked_at = #{revokedAt}
            WHERE photo_upload_token_id = #{photoUploadTokenId}
              AND revoked_at IS NULL
            """)
    int revoke(@Param("photoUploadTokenId") long photoUploadTokenId, @Param("revokedAt") ZonedDateTime revokedAt);
}
