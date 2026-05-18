package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.enums.PhotoStatus;
import io.legohunter.data.mybatis.mapper.ItemInventoryPhotoMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventoryPhotoDao {
    private final ItemInventoryPhotoMapper itemInventoryPhotoMapper;

    public Set<ItemInventoryPhoto> findAll() {
        return itemInventoryPhotoMapper.findAll();
    }

    public Optional<ItemInventoryPhoto> findByItemInventoryPhotoId(Integer itemInventoryPhotoId) {
        return itemInventoryPhotoMapper.findByItemInventoryPhotoId(itemInventoryPhotoId);
    }

    public Optional<ItemInventoryPhoto> findByUuid(String md5) {
        return itemInventoryPhotoMapper.findByMD5(md5);
    }

    public ItemInventoryPhoto insert(ItemInventoryPhoto itemInventoryPhoto) {
        itemInventoryPhotoMapper.insert(itemInventoryPhoto);
        return itemInventoryPhoto;
    }

    public ItemInventoryPhoto insertPhoto(
            Integer itemInventoryId,
            String md5,
            String filename,
            String s3Bucket,
            String s3Key,
            long fileSize,
            boolean primary,
            PhotoStatus status) {
        return insertPhoto(
                itemInventoryId,
                md5,
                filename,
                s3Bucket,
                s3Key,
                fileSize,
                primary,
                null,
                status
        );
    }

    public ItemInventoryPhoto insertPhoto(
            Integer itemInventoryId,
            String md5,
            String filename,
            String s3Bucket,
            String s3Key,
            long fileSize,
            boolean primary,
            String caption,
            PhotoStatus status) {
        ItemInventoryPhoto itemInventoryPhoto = ItemInventoryPhoto.builder()
                .itemInventoryId(itemInventoryId)
                .md5(md5)
                .fileName(filename)
                .s3Bucket(s3Bucket)
                .s3Key(s3Key)
                .fileSize(fileSize)
                .primary(primary)
                .caption(caption)
                .status(status)
                .build();
        insert(itemInventoryPhoto);
        return itemInventoryPhoto;
    }

    // =========================
    // Deduplication
    // =========================
    public boolean existsByMd5(String md5) {
        return itemInventoryPhotoMapper.findByMD5(md5).isPresent();
    }

    public Optional<ItemInventoryPhoto> findByMd5(String md5) {
        return itemInventoryPhotoMapper.findByMD5(md5);
    }

    public Optional<ItemInventoryPhoto> findByItemInventoryIdAndFileName(Integer itemInventoryId, String fileName) {
        return itemInventoryPhotoMapper.findByItemInventoryIdAndFileName(itemInventoryId, fileName);
    }

    // =========================
    // State machine (CORE)
    // =========================
    public int transitionStatus(String md5, PhotoStatus from, PhotoStatus to) {
        if (!from.canTransitionTo(to)) {
            throw new IllegalStateException("Invalid transition: " + from + " → " + to
            );
        }

        int updated = itemInventoryPhotoMapper.updateStatus(md5, from, to);

        if (updated == 0) {
            throw new IllegalStateException("Stale state or race condition for md5=" + md5);
        }
        return updated;
    }

    // =========================
    // S3 lifecycle
    // =========================
    public int markUploaded(String md5, String bucket, String s3Key, long fileSize) {
        int updated = itemInventoryPhotoMapper.markUploaded(md5, bucket, s3Key, fileSize);
        return updated;
    }

    public int replaceStoredObject(
            Integer itemInventoryPhotoId,
            String fileName,
            String md5,
            String bucket,
            String s3Key,
            long fileSize,
            boolean primary,
            String caption,
            PhotoStatus status) {
        return itemInventoryPhotoMapper.replaceStoredObject(
                itemInventoryPhotoId,
                fileName,
                md5,
                bucket,
                s3Key,
                fileSize,
                primary,
                caption,
                status
        );
    }

    public int updateMetadata(
            Integer itemInventoryPhotoId,
            String fileName,
            boolean primary,
            String caption,
            PhotoStatus status) {
        return itemInventoryPhotoMapper.updateMetadata(
                itemInventoryPhotoId,
                fileName,
                primary,
                caption,
                status
        );
    }

    // =========================
    // Primary photo
    // =========================
    public void setPrimaryForItem(Integer itemInventoryId, String md5) {
        clearPrimaryForItem(itemInventoryId);
        setPrimary(itemInventoryId, md5);
    }

    public void clearPrimaryForItem(Integer itemInventoryId) {
        int updated = itemInventoryPhotoMapper.clearPrimaryForItem(itemInventoryId);
    }

    public void setPrimary(Integer itemInventoryId, String md5) {
        int updated = itemInventoryPhotoMapper.setPrimary(itemInventoryId, md5);
    }

    // =========================
    // Queries
    // =========================
    public Set<ItemInventoryPhoto> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryPhotoMapper.findByItemInventoryId(itemInventoryId);
    }

    public Optional<ItemInventoryPhoto> findPrimaryByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryPhotoMapper.findPrimaryByItemInventoryId(itemInventoryId);
    }

    public Set<ItemInventoryPhoto> findByStatus(PhotoStatus status) {
        return itemInventoryPhotoMapper.findByStatus(status);
    }

    public long countByStatus(PhotoStatus status) {
        return itemInventoryPhotoMapper.findByStatus(status).size();
    }

    // =========================
    // Retry / recovery
    // =========================
    public Set<ItemInventoryPhoto> findFailed() {
        return findByStatus(PhotoStatus.FAILED);
    }

    public int resetToUploaded(String md5) {
        return 0;
    }

    // =========================
    // Maintenance
    // =========================
    public int deleteByMd5(String md5) {
        return itemInventoryPhotoMapper.deleteByMd5(md5);
    }

    public int deleteByMd5AndStorage(String md5, String bucket, String s3Key) {
        return itemInventoryPhotoMapper.deleteByMd5AndStorage(md5, bucket, s3Key);
    }

    public Set<ItemInventoryPhoto> findWithoutS3Key() {
        return itemInventoryPhotoMapper.findWithoutS3Key();
    }

    public void setPrimaryPhoto(Integer itemInventoryId, String md5) {
        clearPrimaryForItem(itemInventoryId);
        setPrimary(itemInventoryId, md5);
    }
}
