package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.ExternalServiceItem;
import io.legohunter.data.mybatis.mapper.ExternalServiceItemMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalServiceItemDao {
    private final ExternalServiceItemMapper externalServiceItemMapper;

    public Optional<ExternalServiceItem> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId) {
        return externalServiceItemMapper.findByExternalItemIdAndItemInventoryId(externalItemId, itemInventoryId);
    }

    public Optional<ExternalServiceItem> findByExternalItemId(Integer externalItemId) {
        return externalServiceItemMapper.findByExternalItemId(externalItemId);
    }

    public Optional<ExternalServiceItem> findByItemId(Integer itemId) {
        return externalServiceItemMapper.findByItemInventoryId(itemId);
    }

    public void insert(ExternalServiceItem externalServiceItem) {
        externalServiceItemMapper.insert(externalServiceItem);
    }

    public void delete(ExternalServiceItem externalServiceItem) {
        externalServiceItemMapper.delete(externalServiceItem);
    }
}