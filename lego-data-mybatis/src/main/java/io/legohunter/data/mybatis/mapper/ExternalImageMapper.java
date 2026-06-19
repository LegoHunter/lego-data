package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.enums.ExternalSyncStatus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExternalImageMapper {
    String ALL_COLUMNS = """
            external_image_id,
            external_service_id,
            item_inventory_photo_id,
            external_service_image_id,
            title,
            image_url,
            md5_at_upload,
            metadata_hash_at_sync,
            sync_status,
            error_message,
            uploaded_at,
            last_synced_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image")
    @ResultMap("externalImageResultMap")
    Set<ExternalImage> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image WHERE external_image_id = #{externalImageId}")
    @ResultMap("externalImageResultMap")
    Optional<ExternalImage> findByExternalImageId(Long externalImageId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image
            WHERE external_service_id = #{externalServiceId}
              AND item_inventory_photo_id = #{itemInventoryPhotoId}
            """)
    @ResultMap("externalImageResultMap")
    Optional<ExternalImage> findByExternalServiceIdAndItemInventoryPhotoId(Integer externalServiceId, Integer itemInventoryPhotoId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM external_image
            WHERE external_service_id = #{externalServiceId}
              AND external_service_image_id = #{externalServiceImageId}
            """)
    @ResultMap("externalImageResultMap")
    Optional<ExternalImage> findByExternalServiceIdAndExternalServiceImageId(Integer externalServiceId, String externalServiceImageId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image WHERE item_inventory_photo_id = #{itemInventoryPhotoId}")
    @ResultMap("externalImageResultMap")
    Set<ExternalImage> findByItemInventoryPhotoId(Integer itemInventoryPhotoId);

    @Select("SELECT " + ALL_COLUMNS + " FROM external_image WHERE sync_status = #{syncStatus.name}")
    @ResultMap("externalImageResultMap")
    Set<ExternalImage> findBySyncStatus(ExternalSyncStatus syncStatus);

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            LEFT JOIN external_image image
              ON image.item_inventory_photo_id = photo.item_inventory_photo_id
             AND image.external_service_id = #{externalServiceId}
            WHERE photo.status = 'PROCESSED'
              AND (
                    image.external_image_id IS NULL
                 OR image.external_service_image_id IS NULL
                 OR image.external_service_image_id = ''
              )
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsMissingExternalImageLinks(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            LEFT JOIN external_image_album album
              ON album.item_inventory_id = photo.item_inventory_id
             AND album.external_service_id = #{externalServiceId}
            WHERE photo.status = 'PROCESSED'
              AND (
                    album.external_image_album_id IS NULL
                 OR album.external_album_id IS NULL
                 OR album.external_album_id = ''
              )
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsMissingAlbumLinks(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            INNER JOIN external_image image
              ON image.item_inventory_photo_id = photo.item_inventory_photo_id
             AND image.external_service_id = #{externalServiceId}
            INNER JOIN external_image_album album
              ON album.item_inventory_id = photo.item_inventory_id
             AND album.external_service_id = #{externalServiceId}
            LEFT JOIN external_image_album_image membership
              ON membership.external_image_album_id = album.external_image_album_id
             AND membership.external_image_id = image.external_image_id
            WHERE photo.status = 'PROCESSED'
              AND image.sync_status = 'SYNCED'
              AND image.external_service_image_id IS NOT NULL
              AND image.external_service_image_id <> ''
              AND album.sync_status = 'SYNCED'
              AND album.external_album_id IS NOT NULL
              AND album.external_album_id <> ''
              AND membership.external_image_id IS NULL
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsMissingAlbumMemberships(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            INNER JOIN external_image image
              ON image.item_inventory_photo_id = photo.item_inventory_photo_id
             AND image.external_service_id = #{externalServiceId}
            WHERE photo.status = 'PROCESSED'
              AND (
                    image.sync_status = 'PENDING'
                 OR EXISTS (
                        SELECT 1
                        FROM external_image_album album
                        WHERE album.item_inventory_id = photo.item_inventory_id
                          AND album.external_service_id = #{externalServiceId}
                          AND album.sync_status = 'PENDING'
                    )
              )
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsWithPendingSyncRows(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            LEFT JOIN external_image image
              ON image.item_inventory_photo_id = photo.item_inventory_photo_id
             AND image.external_service_id = #{externalServiceId}
            LEFT JOIN external_image_album album
              ON album.item_inventory_id = photo.item_inventory_id
             AND album.external_service_id = #{externalServiceId}
            WHERE photo.status = 'PROCESSED'
              AND (
                    image.sync_status = 'FAILED'
                 OR album.sync_status = 'FAILED'
              )
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsWithFailedSyncRows(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Select("""
            SELECT DISTINCT photo.item_inventory_id
            FROM item_inventory_photo photo
            INNER JOIN external_image image
              ON image.item_inventory_photo_id = photo.item_inventory_photo_id
             AND image.external_service_id = #{externalServiceId}
            WHERE photo.status = 'PROCESSED'
              AND image.external_service_image_id IS NOT NULL
              AND (
                    (photo.metadata_hash IS NULL AND image.metadata_hash_at_sync IS NOT NULL)
                 OR (photo.metadata_hash IS NOT NULL AND image.metadata_hash_at_sync IS NULL)
                 OR photo.metadata_hash <> image.metadata_hash_at_sync
              )
            ORDER BY photo.item_inventory_id
            LIMIT #{limit}
            """)
    List<Integer> findItemInventoryIdsWithMetadataDrift(
            @Param("externalServiceId") Integer externalServiceId,
            @Param("limit") int limit
    );

    @Insert("""
            INSERT INTO external_image (
                external_service_id,
                item_inventory_photo_id,
                external_service_image_id,
                title,
                image_url,
                md5_at_upload,
                metadata_hash_at_sync,
                sync_status,
                error_message,
                uploaded_at,
                last_synced_at,
                created_at,
                updated_at
            )
            VALUES (
                #{externalServiceId},
                #{itemInventoryPhotoId},
                #{externalServiceImageId},
                #{title},
                #{imageUrl},
                #{md5AtUpload},
                #{metadataHashAtSync},
                #{syncStatus},
                #{errorMessage},
                #{uploadedAt},
                #{lastSyncedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "externalImageId")
    void insert(ExternalImage externalImage);

    @Update("""
            UPDATE external_image
            SET
                external_service_id = #{externalServiceId},
                item_inventory_photo_id = #{itemInventoryPhotoId},
                external_service_image_id = #{externalServiceImageId},
                title = #{title},
                image_url = #{imageUrl},
                md5_at_upload = #{md5AtUpload},
                metadata_hash_at_sync = #{metadataHashAtSync},
                sync_status = #{syncStatus},
                error_message = #{errorMessage},
                uploaded_at = #{uploadedAt},
                last_synced_at = #{lastSyncedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE external_image_id = #{externalImageId}
            """)
    int update(ExternalImage externalImage);

    @Insert("""
            INSERT INTO external_image (
                external_service_id,
                item_inventory_photo_id,
                external_service_image_id,
                title,
                image_url,
                md5_at_upload,
                metadata_hash_at_sync,
                sync_status,
                error_message,
                uploaded_at,
                last_synced_at,
                created_at,
                updated_at
            )
            VALUES (
                #{externalServiceId},
                #{itemInventoryPhotoId},
                #{externalServiceImageId},
                #{title},
                #{imageUrl},
                #{md5AtUpload},
                #{metadataHashAtSync},
                #{syncStatus},
                #{errorMessage},
                #{uploadedAt},
                #{lastSyncedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                external_service_image_id = VALUES(external_service_image_id),
                title = VALUES(title),
                image_url = VALUES(image_url),
                md5_at_upload = VALUES(md5_at_upload),
                metadata_hash_at_sync = VALUES(metadata_hash_at_sync),
                sync_status = VALUES(sync_status),
                error_message = VALUES(error_message),
                uploaded_at = VALUES(uploaded_at),
                last_synced_at = VALUES(last_synced_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "externalImageId")
    void upsert(ExternalImage externalImage);

    @Delete("DELETE FROM external_image WHERE external_image_id = #{externalImageId}")
    int deleteByExternalImageId(Long externalImageId);

    @Delete("""
            DELETE FROM external_image
            WHERE external_service_id = #{externalServiceId}
              AND item_inventory_photo_id = #{itemInventoryPhotoId}
            """)
    int deleteByExternalServiceIdAndItemInventoryPhotoId(Integer externalServiceId, Integer itemInventoryPhotoId);
}
