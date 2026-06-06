package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.mybatis.mapper.ExternalCatalogItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalCatalogItemDao {
    private final ExternalCatalogItemMapper externalCatalogItemMapper;

    public Set<ExternalCatalogItem> findAll() {
        return externalCatalogItemMapper.findAll();
    }

    public Optional<ExternalCatalogItem> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return externalCatalogItemMapper.findByExternalCatalogItemId(externalCatalogItemId);
    }

    public Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalItemKey(Integer externalServiceId, String externalItemKey) {
        return externalCatalogItemMapper.findByExternalServiceIdAndExternalItemKey(externalServiceId, externalItemKey);
    }

    public Optional<ExternalCatalogItem> findByExternalServiceIdAndExternalUniqueKey(Integer externalServiceId, String externalUniqueKey) {
        return externalCatalogItemMapper.findByExternalServiceIdAndExternalUniqueKey(externalServiceId, externalUniqueKey);
    }

    public ExternalCatalogItem insert(ExternalCatalogItem externalCatalogItem) {
        externalCatalogItemMapper.insert(externalCatalogItem);
        return externalCatalogItem;
    }

    public ExternalCatalogItem update(ExternalCatalogItem externalCatalogItem) {
        externalCatalogItemMapper.update(externalCatalogItem);
        return findByExternalCatalogItemId(externalCatalogItem.getExternalCatalogItemId()).orElseThrow();
    }

    public void delete(Integer externalCatalogItemId) {
        externalCatalogItemMapper.delete(externalCatalogItemId);
    }

    public ExternalCatalogItem upsert(ExternalCatalogItem externalCatalogItem) {
        externalCatalogItemMapper.upsert(externalCatalogItem);
        return findByExternalCatalogItemId(externalCatalogItem.getExternalCatalogItemId()).orElseThrow();
    }
}
