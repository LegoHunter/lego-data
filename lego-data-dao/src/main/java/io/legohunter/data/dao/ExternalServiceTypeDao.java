package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.mybatis.mapper.ExternalServiceTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalServiceTypeDao {
    private final ExternalServiceTypeMapper externalServiceTypeMapper;

    public Set<ExternalServiceType> findAll() {
        return externalServiceTypeMapper.findAll();
    }

    public Optional<ExternalServiceType> findByExternalServiceTypeId(final Integer externalServiceTypeId) {
        return externalServiceTypeMapper.findByExternalServiceTypeId(externalServiceTypeId);
    }

    public Optional<ExternalServiceType> findByExternalServiceTypeName(final String externalServiceTypeName) {
        return externalServiceTypeMapper.findByExternalServiceTypeName(externalServiceTypeName);
    }

    public ExternalServiceType insert(ExternalServiceType externalServiceType) {
        externalServiceTypeMapper.insert(externalServiceType);
        return externalServiceType;
    }

    public ExternalServiceType update(ExternalServiceType externalServiceType) {
        externalServiceTypeMapper.update(externalServiceType);
        return findByExternalServiceTypeId(externalServiceType.getExternalServiceTypeId()).orElseThrow();
    }

    public void delete(Integer externalServiceTypeId) {
        externalServiceTypeMapper.delete(externalServiceTypeId);
    }

    public ExternalServiceType upsert(ExternalServiceType externalServiceType) {
        externalServiceTypeMapper.upsert(externalServiceType);
        return findByExternalServiceTypeId(externalServiceType.getExternalServiceTypeId()).orElseThrow();
    }

    public Optional<ExternalServiceType> findExternalServiceTypeById(final Integer externalServiceTypeId) {
        return findByExternalServiceTypeId(externalServiceTypeId);
    }

    public Optional<ExternalServiceType> findExternalServiceTypeByName(final String externalServiceTypeName) {
        return findByExternalServiceTypeName(externalServiceTypeName);
    }
}
