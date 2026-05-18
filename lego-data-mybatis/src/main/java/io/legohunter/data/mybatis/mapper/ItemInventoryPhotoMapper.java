package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.enums.PhotoStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemInventoryPhotoMapper {
    String ALL_COLUMNS = """                    
            item_inventory_photo_id,
            item_inventory_id,
            s3_bucket,
            s3_key,
            md5,
            file_name,
            file_size,
            is_primary,
            caption,
            status,
            created_at,
            updated_at,
            uploaded_at            
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo")
    @ResultMap("itemInventoryPhotoResultMap")
    Set<ItemInventoryPhoto> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE item_inventory_photo_id = #{itemInventoryPhotoId}")
    @ResultMap("itemInventoryPhotoResultMap")
    Optional<ItemInventoryPhoto> findByItemInventoryPhotoId(Integer itemInventoryPhotoId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE md5 = #{md5}")
    @ResultMap("itemInventoryPhotoResultMap")
    Optional<ItemInventoryPhoto> findByMD5(String md5);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM item_inventory_photo
            WHERE item_inventory_id = #{itemInventoryId}
              AND file_name = #{fileName}
            """)
    @ResultMap("itemInventoryPhotoResultMap")
    Optional<ItemInventoryPhoto> findByItemInventoryIdAndFileName(Integer itemInventoryId, String fileName);

    @Insert("""
            INSERT INTO item_inventory_photo (item_inventory_id, s3_bucket,s3_key,md5,file_name,file_size,is_primary,caption,status,created_at) \
            VALUES (#{itemInventoryId}, #{s3Bucket}, #{s3Key}, #{md5}, #{fileName}, #{fileSize}, #{primary}, #{caption}, #{status}, CURRENT_TIMESTAMP) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "itemInventoryPhotoId")
    void insert(ItemInventoryPhoto itemInventoryPhoto);

    @Update("""
            UPDATE item_inventory_photo SET
                status = #{to}
            WHERE md5 = #{md5}
            AND status = #{from}
            """)
    int updateStatus(String md5, PhotoStatus from, PhotoStatus to);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE item_inventory_id = #{itemInventoryId}")
    @ResultMap("itemInventoryPhotoResultMap")
    Set<ItemInventoryPhoto> findByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE item_inventory_id = #{itemInventoryId} AND is_primary = true")
    @ResultMap("itemInventoryPhotoResultMap")
    Optional<ItemInventoryPhoto> findPrimaryByItemInventoryId(Integer itemInventoryId);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE status = #{status.name}")
    @ResultMap("itemInventoryPhotoResultMap")
    Set<ItemInventoryPhoto> findByStatus(PhotoStatus status);

    @Select("SELECT " + ALL_COLUMNS + " FROM item_inventory_photo WHERE s3_key is null")
    @ResultMap("itemInventoryPhotoResultMap")
    Set<ItemInventoryPhoto> findWithoutS3Key();

    @Update("""
            UPDATE item_inventory_photo SET
                is_primary = 0,
                updated_at = NOW() 
            WHERE item_inventory_id = #{itemInventoryId}
            AND is_primary = TRUE;
            """)
    int clearPrimaryForItem(Integer itemInventoryId);

    @Update("""
            UPDATE item_inventory_photo SET
                is_primary = CASE WHEN (md5 = #{md5} AND status = 'PROCESSED') THEN 1 ELSE 0 END,
                updated_at = CURRENT_TIMESTAMP
            WHERE item_inventory_id = #{itemInventoryId}
            """)
    int setPrimary(Integer itemInventoryId, String md5);

    @Delete("DELETE FROM item_inventory_photo WHERE md5 = #{md5}")
    int deleteByMd5(String md5);

    @Update("""
            UPDATE item_inventory_photo
            SET
                file_name = #{fileName},
                s3_bucket = #{bucket},
                s3_key = #{s3Key},
                md5 = #{md5},
                file_size = #{fileSize},
                is_primary = #{primary},
                caption = #{caption},
                status = #{status},
                uploaded_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
            WHERE item_inventory_photo_id = #{itemInventoryPhotoId}
            """)
    int replaceStoredObject(
            Integer itemInventoryPhotoId,
            String fileName,
            String md5,
            String bucket,
            String s3Key,
            long fileSize,
            boolean primary,
            String caption,
            PhotoStatus status);

    @Update("""
            UPDATE item_inventory_photo
            SET
                file_name = #{fileName},
                is_primary = #{primary},
                caption = #{caption},
                status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE item_inventory_photo_id = #{itemInventoryPhotoId}
            """)
    int updateMetadata(
            Integer itemInventoryPhotoId,
            String fileName,
            boolean primary,
            String caption,
            PhotoStatus status);

    @Delete("""
            DELETE FROM item_inventory_photo
            WHERE md5 = #{md5}
              AND s3_bucket = #{bucket}
              AND s3_key = #{s3Key}
            """)
    int deleteByMd5AndStorage(String md5, String bucket, String s3Key);

    @Update("""
            UPDATE item_inventory_photo
            SET
                s3_bucket = #{bucket},
                s3_key = #{s3Key},
                file_size = #{fileSize},
                uploaded_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
            WHERE md5 = #{md5}
              AND status = 'UPLOADED'
            """)
    int markUploaded(String md5, String bucket, String s3Key, long fileSize);
}
