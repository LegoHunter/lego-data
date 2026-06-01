package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.LinkedHashMap;
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
        return findItemInventorySyncCandidates(externalServiceId, includeFailed, limit).stream()
                .map(ImageHostingSyncCandidate::itemInventoryId)
                .toList();
    }

    public List<ImageHostingSyncCandidate> findItemInventorySyncCandidates(Integer externalServiceId, boolean includeFailed, int limit) {
        int effectiveLimit = Math.max(1, limit);
        LinkedHashMap<Integer, EnumSet<ImageHostingSyncCandidateReason>> candidates = new LinkedHashMap<>();
        addCandidates(
                candidates,
                effectiveLimit,
                ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK,
                externalImageMapper::findItemInventoryIdsMissingAlbumLinks,
                externalServiceId
        );
        addCandidates(
                candidates,
                effectiveLimit,
                ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK,
                externalImageMapper::findItemInventoryIdsMissingExternalImageLinks,
                externalServiceId
        );
        if (includeFailed) {
            addCandidates(
                    candidates,
                    effectiveLimit,
                    ImageHostingSyncCandidateReason.FAILED_SYNC,
                    externalImageMapper::findItemInventoryIdsWithFailedSyncRows,
                    externalServiceId
            );
        }
        addCandidates(
                candidates,
                effectiveLimit,
                ImageHostingSyncCandidateReason.PENDING_SYNC,
                externalImageMapper::findItemInventoryIdsWithPendingSyncRows,
                externalServiceId
        );
        addCandidates(
                candidates,
                effectiveLimit,
                ImageHostingSyncCandidateReason.METADATA_CHANGED,
                externalImageMapper::findItemInventoryIdsWithMetadataDrift,
                externalServiceId
        );
        return candidates.entrySet().stream()
                .map(entry -> ImageHostingSyncCandidate.of(entry.getKey(), entry.getValue()))
                .toList();
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

    private void addCandidates(
            LinkedHashMap<Integer, EnumSet<ImageHostingSyncCandidateReason>> candidates,
            int limit,
            ImageHostingSyncCandidateReason reason,
            BiFunction<Integer, Integer, List<Integer>> query,
            Integer externalServiceId
    ) {
        List<Integer> itemInventoryIds = query.apply(externalServiceId, limit);
        for (Integer itemInventoryId : itemInventoryIds) {
            if (itemInventoryId == null) {
                continue;
            }
            if (candidates.containsKey(itemInventoryId)) {
                candidates.get(itemInventoryId).add(reason);
                continue;
            }
            if (candidates.size() < limit) {
                candidates.put(itemInventoryId, EnumSet.of(reason));
            }
        }
    }
}
