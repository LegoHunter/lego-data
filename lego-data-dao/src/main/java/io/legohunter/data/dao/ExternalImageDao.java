package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

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

    public List<Integer> findItemInventoryIdsNeedingSync(Integer externalServiceId, boolean includeFailed, int limit) {
        int effectiveLimit = Math.max(1, limit);
        LinkedHashSet<Integer> itemInventoryIds = new LinkedHashSet<>();
        addUntilLimit(
                itemInventoryIds,
                effectiveLimit,
                (serviceId, queryLimit) -> externalImageMapper.findItemInventoryIdsMissingExternalImages(serviceId, queryLimit),
                externalServiceId
        );
        addUntilLimit(
                itemInventoryIds,
                effectiveLimit,
                (serviceId, queryLimit) -> externalImageMapper.findItemInventoryIdsMissingOrUnsyncedAlbums(serviceId, queryLimit),
                externalServiceId
        );
        addUntilLimit(
                itemInventoryIds,
                effectiveLimit,
                (serviceId, queryLimit) -> externalImageMapper.findItemInventoryIdsWithUnsyncedImages(serviceId, includeFailed, queryLimit),
                externalServiceId
        );
        addUntilLimit(
                itemInventoryIds,
                effectiveLimit,
                (serviceId, queryLimit) -> externalImageMapper.findItemInventoryIdsWithMetadataDrift(serviceId, queryLimit),
                externalServiceId
        );
        return List.copyOf(itemInventoryIds);
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

    public boolean hasSyncedMetadataHash(Integer externalServiceId, Integer itemInventoryPhotoId, String metadataHash) {
        return findByExternalServiceIdAndItemInventoryPhotoId(externalServiceId, itemInventoryPhotoId)
                .map(ExternalImage::getMetadataHashAtSync)
                .filter(metadataHash::equals)
                .isPresent();
    }

    private void addUntilLimit(
            LinkedHashSet<Integer> itemInventoryIds,
            int limit,
            BiFunction<Integer, Integer, List<Integer>> query,
            Integer externalServiceId
    ) {
        if (itemInventoryIds.size() >= limit) {
            return;
        }
        itemInventoryIds.addAll(query.apply(externalServiceId, limit));
        if (itemInventoryIds.size() > limit) {
            List<Integer> limited = itemInventoryIds.stream()
                    .limit(limit)
                    .toList();
            itemInventoryIds.clear();
            itemInventoryIds.addAll(limited);
        }
    }
}
