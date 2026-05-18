package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.ExternalItemInventory;
import net.lego.data.v2.mybatis.mapper.ExternalItemInventoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalItemInventoryDao {
    private final ExternalItemInventoryMapper externalItemInventoryMapper;

    public Optional<ExternalItemInventory> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId) {
        return externalItemInventoryMapper.findByExternalItemIdAndItemInventoryId(externalItemId, itemInventoryId);
    }

    public List<ExternalItemInventory> findByExternalItemId(Integer externalItemId) {
        return externalItemInventoryMapper.findByExternalItemId(externalItemId);
    }

    public List<ExternalItemInventory> findByItemInventoryId(Integer itemInventoryId) {
        return externalItemInventoryMapper.findByItemInventoryId(itemInventoryId);
    }

    public void insert(ExternalItemInventory externalItemInventory) {
        externalItemInventoryMapper.insert(externalItemInventory);
    }

    public void update(ExternalItemInventory externalItemInventory) {
        externalItemInventoryMapper.update(externalItemInventory);
    }
}