package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.mybatis.mapper.ExternalCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalCategoryDao {
    private final ExternalCategoryMapper externalCategoryMapper;

    public Set<ExternalCategory> findAll() {
        return externalCategoryMapper.findAll();
    }

    public Optional<ExternalCategory> findByExternalCategoryId(Integer externalCategoryId) {
        return externalCategoryMapper.findByExternalCategoryId(externalCategoryId);
    }

    public Optional<ExternalCategory> findByExternalServiceIdAndExternalCategoryKey(Integer externalServiceId, String externalCategoryKey) {
        return externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(externalServiceId, externalCategoryKey);
    }

    public ExternalCategory insert(ExternalCategory externalCategory) {
        externalCategoryMapper.insert(externalCategory);
        return externalCategory;
    }

    public ExternalCategory update(ExternalCategory externalCategory) {
        externalCategoryMapper.update(externalCategory);
        return findByExternalCategoryId(externalCategory.getExternalCategoryId()).orElseThrow();
    }

    public void delete(Integer externalCategoryId) {
        externalCategoryMapper.delete(externalCategoryId);
    }

    public ExternalCategory upsert(ExternalCategory externalCategory) {
        externalCategoryMapper.upsert(externalCategory);
        if (externalCategory.getExternalCategoryId() != null) {
            return findByExternalCategoryId(externalCategory.getExternalCategoryId()).orElseThrow();
        }
        return findByExternalServiceIdAndExternalCategoryKey(
                externalCategory.getExternalServiceId(),
                externalCategory.getExternalCategoryKey()
        ).orElseThrow();
    }
}
