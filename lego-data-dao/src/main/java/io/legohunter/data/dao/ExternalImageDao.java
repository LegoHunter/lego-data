package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalImageDao {
    private final ExternalImageMapper externalImageMapper;

    public Set<ExternalImage> findAll() {
        return externalImageMapper.findAll();
    }

    public Optional<ExternalImage> findByExternalImageId(Long externalImageId) {
        return externalImageMapper.findByExternalImageId(externalImageId);
    }

    public Optional<ExternalImage> findByExternalServiceIdAndItemInventoryPhotoId(Integer externalServiceId, Integer itemInventoryPhotoId) {
        return externalImageMapper.findByExternalServiceIdAndItemInventoryPhotoId(externalServiceId, itemInventoryPhotoId);
    }

    public Optional<ExternalImage> findByExternalServiceIdAndExternalServiceImageId(Integer externalServiceId, String externalServiceImageId) {
        return externalImageMapper.findByExternalServiceIdAndExternalServiceImageId(externalServiceId, externalServiceImageId);
    }

    public Set<ExternalImage> findByItemInventoryPhotoId(Integer itemInventoryPhotoId) {
        return externalImageMapper.findByItemInventoryPhotoId(itemInventoryPhotoId);
    }

    public Set<ExternalImage> findBySyncStatus(ExternalSyncStatus syncStatus) {
        return externalImageMapper.findBySyncStatus(syncStatus);
    }

    public ExternalImage insert(ExternalImage externalImage) {
        externalImageMapper.insert(externalImage);
        return externalImage;
    }

    public int update(ExternalImage externalImage) {
        return externalImageMapper.update(externalImage);
    }

    public ExternalImage upsert(ExternalImage externalImage) {
        externalImageMapper.upsert(externalImage);
        return externalImage;
    }

    public int deleteByExternalImageId(Long externalImageId) {
        return externalImageMapper.deleteByExternalImageId(externalImageId);
    }

    public int deleteByExternalServiceIdAndItemInventoryPhotoId(Integer externalServiceId, Integer itemInventoryPhotoId) {
        return externalImageMapper.deleteByExternalServiceIdAndItemInventoryPhotoId(externalServiceId, itemInventoryPhotoId);
    }

    public boolean hasUploadedMd5(Integer externalServiceId, Integer itemInventoryPhotoId, String md5) {
        return findByExternalServiceIdAndItemInventoryPhotoId(externalServiceId, itemInventoryPhotoId)
                .map(ExternalImage::getMd5AtUpload)
                .filter(md5::equals)
                .isPresent();
    }
}
