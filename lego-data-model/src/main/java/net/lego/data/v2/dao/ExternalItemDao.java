package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.ExternalItem;
import net.lego.data.v2.mybatis.mapper.ExternalItemMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalItemDao {

    private final ExternalItemMapper externalItemMapper;

    public Optional<ExternalItem> findByExternalItemId(Integer externalItemId) {
        return externalItemMapper.findByExternalItemId(externalItemId);
    }

    public Optional<ExternalItem> findByItemId(Integer externalServiceId, Integer itemId) {
        return externalItemMapper.findByItemId(externalServiceId, itemId);
    }

    public Optional<ExternalItem> findByExternalUniqueId(Integer externalServiceId, Integer externalUniqueId) {
        return externalItemMapper.findByExternalUniqueId(externalServiceId, externalUniqueId);
    }

    public Optional<ExternalItem> findByExternalNumber(Integer externalServiceId, String externalNumber) {
        return externalItemMapper.findByExternalNumber(externalServiceId, externalNumber);
    }

    public Optional<ExternalItem> findByExternalServiceNumber(String externalNumber, String externalServiceName) {
        return externalItemMapper.findByExternalServiceNumber(externalNumber, externalServiceName);
    }

    public void insert(ExternalItem externalItem) {
        externalItemMapper.insert(externalItem);
    }

    public void update(ExternalItem externalItem) {
        externalItemMapper.update(externalItem);
    }

    public void upsert(ExternalItem externalItem) {
        externalItemMapper.upsert(externalItem);
    }
}
