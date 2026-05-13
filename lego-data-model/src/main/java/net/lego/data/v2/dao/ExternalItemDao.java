package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.ExternalItem;
import net.lego.data.v2.mybatis.mapper.ExternalItemMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static net.lego.data.v2.dto.ExternalService.ExternalServiceType.BRICKLINK;

@Component
@RequiredArgsConstructor
public class ExternalItemDao {

    private final ExternalItemMapper externalItemMapper;

    public Optional<ExternalItem> findByExternalItemId(Integer externalItemId) {
        return externalItemMapper.findByExternalItemId(externalItemId);
    }

    public Optional<ExternalItem> findByExternalServiceAndUniqueId(Integer externalServiceId, Integer externalUniqueId) {
        return externalItemMapper.findByExternalServiceAndUniqueId(externalServiceId, externalUniqueId);
    }

    public Optional<ExternalItem> findByExternalServiceAndNumber(Integer externalServiceId, String externalNumber) {
        return externalItemMapper.findByExternalServiceAndNumber(externalServiceId, externalNumber);
    }

    public Set<ExternalItem> findAllByExternalService(String externalServiceName) {
        return externalItemMapper.findAllByExternalService(externalServiceName);
    }

    public void insert(ExternalItem externalItem) {
        externalItemMapper.insert(externalItem);
    }

    public void update(ExternalItem externalItem) {
        externalItemMapper.update(externalItem);
    }

    public void upsert(ExternalItem externalItem) {
        Optional.ofNullable(externalItem)
                .map(ExternalItem::getExternalItemId)
                .ifPresentOrElse(externalItemId -> {
                    externalItemMapper.update(externalItem);
                }, () -> {
                    if (externalItemMapper
                            .findByExternalServiceAndNumber(BRICKLINK.getExternalServiceId(), externalItem.getExternalNumber())
                            .isPresent()) {
                        externalItemMapper.update(externalItem);
                    } else {
                        externalItemMapper.insert(externalItem);
                    }
                });
    }
}
