package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImageAlbumImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ExternalImageAlbumImageMapper {
    String ALL_COLUMNS = """
            external_image_album_id,
            external_image_id,
            sort_order,
            is_primary,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album_image")
    @ResultMap("externalImageAlbumImageResultMap")
    Set<ExternalImageAlbumImage> findAll();

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image_album_image
            WHERE external_image_album_id = #{externalImageAlbumId}
              AND external_image_id = #{externalImageId}
            """)
    @ResultMap("externalImageAlbumImageResultMap")
    Optional<ExternalImageAlbumImage> findByExternalImageAlbumIdAndExternalImageId(Long externalImageAlbumId, Long externalImageId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album_image WHERE external_image_album_id = #{externalImageAlbumId}")
    @ResultMap("externalImageAlbumImageResultMap")
    Set<ExternalImageAlbumImage> findByExternalImageAlbumId(Long externalImageAlbumId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album_image WHERE external_image_id = #{externalImageId}")
    @ResultMap("externalImageAlbumImageResultMap")
    Set<ExternalImageAlbumImage> findByExternalImageId(Long externalImageId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image_album_image
            WHERE external_image_album_id = #{externalImageAlbumId}
              AND is_primary = TRUE
            """)
    @ResultMap("externalImageAlbumImageResultMap")
    Optional<ExternalImageAlbumImage> findPrimaryByExternalImageAlbumId(Long externalImageAlbumId);

    @Insert("""
            INSERT INTO external_image_album_image (
                external_image_album_id,
                external_image_id,
                sort_order,
                is_primary,
                created_at,
                updated_at
            )
            VALUES (
                #{externalImageAlbumId},
                #{externalImageId},
                #{sortOrder},
                #{primary},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    void insert(ExternalImageAlbumImage externalImageAlbumImage);

    @Update("""
            UPDATE external_image_album_image
            SET
                sort_order = #{sortOrder},
                is_primary = #{primary},
                updated_at = CURRENT_TIMESTAMP
            WHERE external_image_album_id = #{externalImageAlbumId}
              AND external_image_id = #{externalImageId}
            """)
    int update(ExternalImageAlbumImage externalImageAlbumImage);

    @Insert("""
            INSERT INTO external_image_album_image (
                external_image_album_id,
                external_image_id,
                sort_order,
                is_primary,
                created_at,
                updated_at
            )
            VALUES (
                #{externalImageAlbumId},
                #{externalImageId},
                #{sortOrder},
                #{primary},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                sort_order = VALUES(sort_order),
                is_primary = VALUES(is_primary),
                updated_at = CURRENT_TIMESTAMP
            """)
    void upsert(ExternalImageAlbumImage externalImageAlbumImage);

    @Update("""
            UPDATE external_image_album_image
            SET
                is_primary = FALSE,
                updated_at = CURRENT_TIMESTAMP
            WHERE external_image_album_id = #{externalImageAlbumId}
            """)
    int clearPrimaryForExternalImageAlbum(Long externalImageAlbumId);

    @Delete("""
            DELETE FROM external_image_album_image
            WHERE external_image_album_id = #{externalImageAlbumId}
              AND external_image_id = #{externalImageId}
            """)
    int deleteByExternalImageAlbumIdAndExternalImageId(Long externalImageAlbumId, Long externalImageId);

    @Delete("DELETE FROM external_image_album_image WHERE external_image_album_id = #{externalImageAlbumId}")
    int deleteByExternalImageAlbumId(Long externalImageAlbumId);

    @Delete("DELETE FROM external_image_album_image WHERE external_image_id = #{externalImageId}")
    int deleteByExternalImageId(Long externalImageId);
}
