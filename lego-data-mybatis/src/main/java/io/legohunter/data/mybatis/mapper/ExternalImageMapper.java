package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.enums.ExternalSyncStatus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Insert("""
            INSERT INTO external_image (
                external_service_id,
                item_inventory_photo_id,
                external_service_image_id,
                title,
                image_url,
                md5_at_upload,
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
