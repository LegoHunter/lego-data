package io.legohunter.data.dao;

import io.legohunter.data.dto.PhotoUploadBatch;
import io.legohunter.data.dto.PhotoUploadBatchFile;
import io.legohunter.data.mybatis.mapper.PhotoUploadBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PhotoUploadBatchDao {
    private final PhotoUploadBatchMapper photoUploadBatchMapper;

    /** Stores a presented batch and its files, all pending, in one transaction owned by the caller. */
    public void insertBatch(PhotoUploadBatch batch, List<String> fileNames) {
        photoUploadBatchMapper.insertBatch(batch);
        for (String name : fileNames) {
            photoUploadBatchMapper.insertFile(PhotoUploadBatchFile.builder()
                    .batchId(batch.getBatchId())
                    .fileName(name)
                    .state(PhotoUploadBatchFile.PENDING)
                    .build());
        }
    }

    public Optional<PhotoUploadBatch> findBatch(String batchId) {
        return photoUploadBatchMapper.findBatch(batchId);
    }

    public List<PhotoUploadBatchFile> findFiles(String batchId) {
        return photoUploadBatchMapper.findFilesByBatchId(batchId);
    }

    /** True only when the file was still pending and is now decided. A second outcome for the same file is ignored. */
    public boolean recordOutcome(String batchId, String fileName, String state, String reason,
                                 Integer itemInventoryPhotoId, String md5) {
        return photoUploadBatchMapper.recordOutcome(batchId, fileName, state, reason, itemInventoryPhotoId, md5) == 1;
    }
}
