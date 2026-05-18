package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.ExternalService;
import net.lego.data.v2.mybatis.mapper.ExternalServiceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalServiceDao {
    private final ExternalServiceMapper externalServiceMapper;

    public List<ExternalService> findAll() {
        return externalServiceMapper.findAll();
    }

    public Optional<ExternalService> findExternalServiceById(final Integer externalServiceId) {
        return externalServiceMapper.findExternalServiceById(externalServiceId);
    }

    public Optional<ExternalService> findExternalServiceByName(final String externalServiceName) {
        return externalServiceMapper.findExternalServiceByName(externalServiceName);
    }

    public void insert(ExternalService externalService) {
        externalServiceMapper.insertExternalService(externalService);
    }

    public void update(ExternalService externalService) {
        externalServiceMapper.updateExternalService(externalService);
    }
}
