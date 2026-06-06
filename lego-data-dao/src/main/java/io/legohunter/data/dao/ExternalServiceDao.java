package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.mybatis.mapper.ExternalServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExternalServiceDao {
    private final ExternalServiceMapper externalServiceMapper;

    public Set<ExternalService> findAll() {
        return externalServiceMapper.findAll();
    }

    public Optional<ExternalService> findByExternalServiceId(final Integer externalServiceId) {
        return externalServiceMapper.findByExternalServiceId(externalServiceId);
    }

    public Optional<ExternalService> findByServiceCode(final String serviceCode) {
        return externalServiceMapper.findByServiceCode(serviceCode);
    }

    public ExternalService insert(ExternalService externalService) {
        externalServiceMapper.insert(externalService);
        return externalService;
    }

    public ExternalService update(ExternalService externalService) {
        externalServiceMapper.update(externalService);
        return findByExternalServiceId(externalService.getExternalServiceId()).orElseThrow();
    }

    public void delete(Integer externalServiceId) {
        externalServiceMapper.delete(externalServiceId);
    }

    public ExternalService upsert(ExternalService externalService) {
        externalServiceMapper.upsert(externalService);
        return findByExternalServiceId(externalService.getExternalServiceId()).orElseThrow();
    }

    public Optional<ExternalService> findExternalServiceById(final Integer externalServiceId) {
        return findByExternalServiceId(externalServiceId);
    }

    public Optional<ExternalService> findExternalServiceByName(final String serviceCode) {
        return findByServiceCode(serviceCode);
    }
}
