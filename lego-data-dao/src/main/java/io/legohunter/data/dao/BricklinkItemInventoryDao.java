package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.BricklinkItemInventory;
import io.legohunter.data.mybatis.mapper.BricklinkItemInventoryMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BricklinkItemInventoryDao {
    private final BricklinkItemInventoryMapper bricklinkItemInventoryMapper;

    public Optional<BricklinkItemInventory> findByBricklinkItemInventoryId(Integer bricklinkItemInventoryId) {
        return bricklinkItemInventoryMapper.findByBricklinkItemInventoryId(bricklinkItemInventoryId);
    }

    public Optional<BricklinkItemInventory> findByExternalItemIdAndItemInventoryId(Integer externalItemId, Integer itemInventoryId) {
        return bricklinkItemInventoryMapper.findByExternalItemIdAndItemInventoryId(externalItemId, itemInventoryId);
    }

    public Optional<BricklinkItemInventory> findByUuid(String uuid) {
        return bricklinkItemInventoryMapper.findByUuid(uuid);
    }

    public void insert(BricklinkItemInventory bricklinkItemInventory) {
        bricklinkItemInventoryMapper.insert(bricklinkItemInventory);
    }

    public void update(BricklinkItemInventory bricklinkItemInventory) {
        bricklinkItemInventoryMapper.update(bricklinkItemInventory);
    }
}