package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImageAlbumImage;
import io.legohunter.data.mybatis.mapper.ExternalImageAlbumImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalImageAlbumImageDao {
    private final ExternalImageAlbumImageMapper externalImageAlbumImageMapper;

    public Set<ExternalImageAlbumImage> findAll() {
        return externalImageAlbumImageMapper.findAll();
    }

    public Optional<ExternalImageAlbumImage> findByExternalImageAlbumIdAndExternalImageId(Long externalImageAlbumId, Long externalImageId) {
        return externalImageAlbumImageMapper.findByExternalImageAlbumIdAndExternalImageId(externalImageAlbumId, externalImageId);
    }

    public Set<ExternalImageAlbumImage> findByExternalImageAlbumId(Long externalImageAlbumId) {
        return externalImageAlbumImageMapper.findByExternalImageAlbumId(externalImageAlbumId);
    }

    public Set<ExternalImageAlbumImage> findByExternalImageId(Long externalImageId) {
        return externalImageAlbumImageMapper.findByExternalImageId(externalImageId);
    }

    public Optional<ExternalImageAlbumImage> findPrimaryByExternalImageAlbumId(Long externalImageAlbumId) {
        return externalImageAlbumImageMapper.findPrimaryByExternalImageAlbumId(externalImageAlbumId);
    }

    public ExternalImageAlbumImage insert(ExternalImageAlbumImage externalImageAlbumImage) {
        externalImageAlbumImageMapper.insert(externalImageAlbumImage);
        return externalImageAlbumImage;
    }

    public int update(ExternalImageAlbumImage externalImageAlbumImage) {
        return externalImageAlbumImageMapper.update(externalImageAlbumImage);
    }

    public ExternalImageAlbumImage upsert(ExternalImageAlbumImage externalImageAlbumImage) {
        externalImageAlbumImageMapper.upsert(externalImageAlbumImage);
        return externalImageAlbumImage;
    }

    public int clearPrimaryForExternalImageAlbum(Long externalImageAlbumId) {
        return externalImageAlbumImageMapper.clearPrimaryForExternalImageAlbum(externalImageAlbumId);
    }

    public int deleteByExternalImageAlbumIdAndExternalImageId(Long externalImageAlbumId, Long externalImageId) {
        return externalImageAlbumImageMapper.deleteByExternalImageAlbumIdAndExternalImageId(externalImageAlbumId, externalImageId);
    }

    public int deleteByExternalImageAlbumId(Long externalImageAlbumId) {
        return externalImageAlbumImageMapper.deleteByExternalImageAlbumId(externalImageAlbumId);
    }

    public int deleteByExternalImageId(Long externalImageId) {
        return externalImageAlbumImageMapper.deleteByExternalImageId(externalImageId);
    }

    public void setPrimaryImage(Long externalImageAlbumId, Long externalImageId) {
        clearPrimaryForExternalImageAlbum(externalImageAlbumId);
        upsert(ExternalImageAlbumImage.builder()
                .externalImageAlbumId(externalImageAlbumId)
                .externalImageId(externalImageId)
                .primary(true)
                .build());
    }
}
