package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.mybatis.mapper.ExternalServiceCapabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalServiceCapabilityDao {
    private final ExternalServiceCapabilityMapper externalServiceCapabilityMapper;

    public Set<ExternalServiceCapability> findAll() {
        return externalServiceCapabilityMapper.findAll();
    }

    public Optional<ExternalServiceCapability> findByExternalServiceIdAndCapabilityCode(Integer externalServiceId, String capabilityCode) {
        return externalServiceCapabilityMapper.findByExternalServiceIdAndCapabilityCode(externalServiceId, capabilityCode);
    }

    public Set<ExternalServiceCapability> findByExternalServiceId(Integer externalServiceId) {
        return externalServiceCapabilityMapper.findByExternalServiceId(externalServiceId);
    }

    public Set<ExternalServiceCapability> findByCapabilityCode(String capabilityCode) {
        return externalServiceCapabilityMapper.findByCapabilityCode(capabilityCode);
    }

    public ExternalServiceCapability insert(ExternalServiceCapability externalServiceCapability) {
        externalServiceCapabilityMapper.insert(externalServiceCapability);
        return externalServiceCapability;
    }

    public ExternalServiceCapability update(ExternalServiceCapability externalServiceCapability) {
        externalServiceCapabilityMapper.update(externalServiceCapability);
        return findByExternalServiceIdAndCapabilityCode(externalServiceCapability.getExternalServiceId(), externalServiceCapability.getCapabilityCode()).orElseThrow();
    }

    public void delete(Integer externalServiceId, String capabilityCode) {
        externalServiceCapabilityMapper.delete(externalServiceId, capabilityCode);
    }

    public ExternalServiceCapability upsert(ExternalServiceCapability externalServiceCapability) {
        externalServiceCapabilityMapper.upsert(externalServiceCapability);
        return findByExternalServiceIdAndCapabilityCode(externalServiceCapability.getExternalServiceId(), externalServiceCapability.getCapabilityCode()).orElseThrow();
    }
}
