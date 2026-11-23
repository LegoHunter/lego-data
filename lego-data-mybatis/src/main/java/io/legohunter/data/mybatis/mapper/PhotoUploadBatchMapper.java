package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PhotoUploadBatch;
import io.legohunter.data.dto.PhotoUploadBatchFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface PhotoUploadBatchMapper {
    String FILE_COLUMNS = """
            photo_upload_batch_file_id,
            batch_id,
            file_name,
            state,
            reason,
            item_inventory_photo_id,
            md5,
            created_at,
            updated_at
            """;

    @Insert("INSERT INTO photo_upload_batch (batch_id, environment, token_name) VALUES (#{batchId}, #{environment}, #{tokenName})")
    int insertBatch(PhotoUploadBatch batch);

    @Select("SELECT batch_id, environment, token_name, created_at FROM photo_upload_batch WHERE batch_id = #{batchId}")
    @Results(id = "photoUploadBatchResultMap", value = {
            @Result(property = "batchId", column = "batch_id", id = true),
            @Result(property = "environment", column = "environment"),
            @Result(property = "tokenName", column = "token_name"),
            @Result(property = "createdAt", column = "created_at")
    })
    Optional<PhotoUploadBatch> findBatch(@Param("batchId") String batchId);

    @Insert("INSERT INTO photo_upload_batch_file (batch_id, file_name, state) VALUES (#{batchId}, #{fileName}, #{state})")
    int insertFile(PhotoUploadBatchFile file);

    @Select("SELECT " + FILE_COLUMNS + " FROM photo_upload_batch_file WHERE batch_id = #{batchId} ORDER BY photo_upload_batch_file_id")
    @Results(id = "photoUploadBatchFileResultMap", value = {
            @Result(property = "photoUploadBatchFileId", column = "photo_upload_batch_file_id", id = true),
            @Result(property = "batchId", column = "batch_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "state", column = "state"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "itemInventoryPhotoId", column = "item_inventory_photo_id"),
            @Result(property = "md5", column = "md5"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<PhotoUploadBatchFile> findFilesByBatchId(@Param("batchId") String batchId);

    /** Records an outcome once: only a file still pending is changed. Returns 1 when it was changed. */
    @Update("""
            UPDATE photo_upload_batch_file
            SET state = #{state},
                reason = #{reason},
                item_inventory_photo_id = #{itemInventoryPhotoId},
                md5 = #{md5}
            WHERE batch_id = #{batchId}
              AND file_name = #{fileName}
              AND state = 'pending'
            """)
    int recordOutcome(@Param("batchId") String batchId, @Param("fileName") String fileName,
                      @Param("state") String state, @Param("reason") String reason,
                      @Param("itemInventoryPhotoId") Integer itemInventoryPhotoId, @Param("md5") String md5);
}
