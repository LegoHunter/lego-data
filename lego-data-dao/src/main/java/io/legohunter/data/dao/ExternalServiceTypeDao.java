package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.mybatis.mapper.ExternalServiceTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalServiceTypeDao {
    private final ExternalServiceTypeMapper externalServiceTypeMapper;

    public List<ExternalServiceType> findAll() {
        return externalServiceTypeMapper.findAll();
    }

    public Optional<ExternalServiceType> findExternalServiceTypeById(final Integer externalServiceTypeId) {
        return externalServiceTypeMapper.findExternalServiceTypeById(externalServiceTypeId);
    }

    public Optional<ExternalServiceType> findExternalServiceTypeByName(final String externalServiceTypeName) {
        return externalServiceTypeMapper.findExternalServiceTypeByName(externalServiceTypeName);
    }

    public void insert(ExternalServiceType externalServiceType) {
        externalServiceTypeMapper.insertExternalServiceType(externalServiceType);
    }

    public void update(ExternalServiceType externalServiceType) {
        externalServiceTypeMapper.updateExternalServiceType(externalServiceType);
    }
}