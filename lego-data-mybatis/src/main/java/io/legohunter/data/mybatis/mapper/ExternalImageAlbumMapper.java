package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.enums.ExternalSyncStatus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface ExternalImageAlbumMapper {
    String ALL_COLUMNS = """
            external_image_album_id,
            external_service_id,
            item_inventory_id,
            external_album_id,
            title,
            album_url,
            short_url,
            sync_status,
            error_message,
            last_synced_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album")
    @ResultMap("externalImageAlbumResultMap")
    Set<ExternalImageAlbum> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album WHERE external_image_album_id = #{externalImageAlbumId}")
    @ResultMap("externalImageAlbumResultMap")
    Optional<ExternalImageAlbum> findByExternalImageAlbumId(Long externalImageAlbumId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image_album
            WHERE external_service_id = #{externalServiceId}
              AND item_inventory_id = #{itemInventoryId}
            """)
    @ResultMap("externalImageAlbumResultMap")
    Optional<ExternalImageAlbum> findByExternalServiceIdAndItemInventoryId(Integer externalServiceId, Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image_album
            WHERE external_service_id = #{externalServiceId}
              AND external_album_id = #{externalAlbumId}
            """)
    @ResultMap("externalImageAlbumResultMap")
    Optional<ExternalImageAlbum> findByExternalServiceIdAndExternalAlbumId(Integer externalServiceId, String externalAlbumId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image_album WHERE sync_status = #{syncStatus.name}")
    @ResultMap("externalImageAlbumResultMap")
    Set<ExternalImageAlbum> findBySyncStatus(ExternalSyncStatus syncStatus);

    @Insert("""
            INSERT INTO external_image_album (
                external_service_id,
                item_inventory_id,
                external_album_id,
                title,
                album_url,
                short_url,
                sync_status,
                error_message,
                last_synced_at,
                created_at,
                updated_at
            )
            VALUES (
                #{externalServiceId},
                #{itemInventoryId},
                #{externalAlbumId},
                #{title},
                #{albumUrl},
                #{shortUrl},
                #{syncStatus},
                #{errorMessage},
                #{lastSyncedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "externalImageAlbumId")
    void insert(ExternalImageAlbum externalImageAlbum);

    @Update("""
            UPDATE external_image_album
            SET
                external_service_id = #{externalServiceId},
                item_inventory_id = #{itemInventoryId},
                external_album_id = #{externalAlbumId},
                title = #{title},
                album_url = #{albumUrl},
                short_url = #{shortUrl},
                sync_status = #{syncStatus},
                error_message = #{errorMessage},
                last_synced_at = #{lastSyncedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE external_image_album_id = #{externalImageAlbumId}
            """)
    int update(ExternalImageAlbum externalImageAlbum);

    @Insert("""
            INSERT INTO external_image_album (
                external_service_id,
                item_inventory_id,
                external_album_id,
                title,
                album_url,
                short_url,
                sync_status,
                error_message,
                last_synced_at,
                created_at,
                updated_at
            )
            VALUES (
                #{externalServiceId},
                #{itemInventoryId},
                #{externalAlbumId},
                #{title},
                #{albumUrl},
                #{shortUrl},
                #{syncStatus},
                #{errorMessage},
                #{lastSyncedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                external_album_id = VALUES(external_album_id),
                title = VALUES(title),
                album_url = VALUES(album_url),
                short_url = VALUES(short_url),
                sync_status = VALUES(sync_status),
                error_message = VALUES(error_message),
                last_synced_at = VALUES(last_synced_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "externalImageAlbumId")
    void upsert(ExternalImageAlbum externalImageAlbum);

    @Delete("DELETE FROM external_image_album WHERE external_image_album_id = #{externalImageAlbumId}")
    int deleteByExternalImageAlbumId(Long externalImageAlbumId);

    @Delete("""
            DELETE FROM external_image_album
            WHERE external_service_id = #{externalServiceId}
              AND item_inventory_id = #{itemInventoryId}
            """)
    int deleteByExternalServiceIdAndItemInventoryId(Integer externalServiceId, Integer itemInventoryId);
}
