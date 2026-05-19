package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.mybatis.mapper.ExternalImageAlbumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalImageAlbumDao {
    private final ExternalImageAlbumMapper externalImageAlbumMapper;

    public Set<ExternalImageAlbum> findAll() {
        return externalImageAlbumMapper.findAll();
    }

    public Optional<ExternalImageAlbum> findByExternalImageAlbumId(Long externalImageAlbumId) {
        return externalImageAlbumMapper.findByExternalImageAlbumId(externalImageAlbumId);
    }

    public Optional<ExternalImageAlbum> findByExternalServiceIdAndItemInventoryId(Integer externalServiceId, Integer itemInventoryId) {
        return externalImageAlbumMapper.findByExternalServiceIdAndItemInventoryId(externalServiceId, itemInventoryId);
    }

    public Optional<ExternalImageAlbum> findByExternalServiceIdAndExternalAlbumId(Integer externalServiceId, String externalAlbumId) {
        return externalImageAlbumMapper.findByExternalServiceIdAndExternalAlbumId(externalServiceId, externalAlbumId);
    }

    public Set<ExternalImageAlbum> findBySyncStatus(ExternalSyncStatus syncStatus) {
        return externalImageAlbumMapper.findBySyncStatus(syncStatus);
    }

    public ExternalImageAlbum insert(ExternalImageAlbum externalImageAlbum) {
        externalImageAlbumMapper.insert(externalImageAlbum);
        return externalImageAlbum;
    }

    public int update(ExternalImageAlbum externalImageAlbum) {
        return externalImageAlbumMapper.update(externalImageAlbum);
    }

    public ExternalImageAlbum upsert(ExternalImageAlbum externalImageAlbum) {
        externalImageAlbumMapper.upsert(externalImageAlbum);
        return externalImageAlbum;
    }

    public int deleteByExternalImageAlbumId(Long externalImageAlbumId) {
        return externalImageAlbumMapper.deleteByExternalImageAlbumId(externalImageAlbumId);
    }

    public int deleteByExternalServiceIdAndItemInventoryId(Integer externalServiceId, Integer itemInventoryId) {
        return externalImageAlbumMapper.deleteByExternalServiceIdAndItemInventoryId(externalServiceId, itemInventoryId);
    }

    public ExternalImageAlbum findOrCreateForItem(ExternalImageAlbum externalImageAlbum) {
        return findByExternalServiceIdAndItemInventoryId(
                externalImageAlbum.getExternalServiceId(),
                externalImageAlbum.getItemInventoryId()
        ).orElseGet(() -> insert(externalImageAlbum));
    }
}
