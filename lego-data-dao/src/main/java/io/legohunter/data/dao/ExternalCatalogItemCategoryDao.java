package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCatalogItemCategory;
import io.legohunter.data.mybatis.mapper.ExternalCatalogItemCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalCatalogItemCategoryDao {
    private final ExternalCatalogItemCategoryMapper externalCatalogItemCategoryMapper;

    public Set<ExternalCatalogItemCategory> findAll() {
        return externalCatalogItemCategoryMapper.findAll();
    }

    public Optional<ExternalCatalogItemCategory> findByExternalCatalogItemIdAndExternalCategoryId(Integer externalCatalogItemId, Integer externalCategoryId) {
        return externalCatalogItemCategoryMapper.findByExternalCatalogItemIdAndExternalCategoryId(externalCatalogItemId, externalCategoryId);
    }

    public Set<ExternalCatalogItemCategory> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return externalCatalogItemCategoryMapper.findByExternalCatalogItemId(externalCatalogItemId);
    }

    public Set<ExternalCatalogItemCategory> findByExternalCategoryId(Integer externalCategoryId) {
        return externalCatalogItemCategoryMapper.findByExternalCategoryId(externalCategoryId);
    }

    public ExternalCatalogItemCategory insert(ExternalCatalogItemCategory externalCatalogItemCategory) {
        externalCatalogItemCategoryMapper.insert(externalCatalogItemCategory);
        return findByExternalCatalogItemIdAndExternalCategoryId(externalCatalogItemCategory.getExternalCatalogItemId(), externalCatalogItemCategory.getExternalCategoryId()).orElseThrow();
    }

    public ExternalCatalogItemCategory update(ExternalCatalogItemCategory externalCatalogItemCategory) {
        externalCatalogItemCategoryMapper.update(externalCatalogItemCategory);
        return findByExternalCatalogItemIdAndExternalCategoryId(externalCatalogItemCategory.getExternalCatalogItemId(), externalCatalogItemCategory.getExternalCategoryId()).orElseThrow();
    }

    public void delete(Integer externalCatalogItemId, Integer externalCategoryId) {
        externalCatalogItemCategoryMapper.delete(externalCatalogItemId, externalCategoryId);
    }

    public ExternalCatalogItemCategory upsert(ExternalCatalogItemCategory externalCatalogItemCategory) {
        externalCatalogItemCategoryMapper.upsert(externalCatalogItemCategory);
        return findByExternalCatalogItemIdAndExternalCategoryId(externalCatalogItemCategory.getExternalCatalogItemId(), externalCatalogItemCategory.getExternalCategoryId()).orElseThrow();
    }
}
