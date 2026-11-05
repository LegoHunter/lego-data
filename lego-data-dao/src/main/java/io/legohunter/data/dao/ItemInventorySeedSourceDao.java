package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventorySeedSource;
import io.legohunter.data.mybatis.mapper.ItemInventorySeedSourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Provenance of lower-environment seeded items (HOMELAB-40). The database enforces one row per
 * item and one item per source key, so a duplicate insert fails rather than seeding twice.
 */
@Component
@RequiredArgsConstructor
public class ItemInventorySeedSourceDao {
    private final ItemInventorySeedSourceMapper itemInventorySeedSourceMapper;

    public List<ItemInventorySeedSource> findAll() {
        return itemInventorySeedSourceMapper.findAll();
    }

    public Optional<ItemInventorySeedSource> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventorySeedSourceMapper.findByItemInventoryId(itemInventoryId);
    }

    public Optional<ItemInventorySeedSource> findBySourceSystemAndSourceKey(String sourceSystem, String sourceKey) {
        return itemInventorySeedSourceMapper.findBySourceSystemAndSourceKey(sourceSystem, sourceKey);
    }

    public List<ItemInventorySeedSource> findBySeedRunId(String seedRunId) {
        return itemInventorySeedSourceMapper.findBySeedRunId(seedRunId);
    }

    public ItemInventorySeedSource insert(ItemInventorySeedSource itemInventorySeedSource) {
        itemInventorySeedSourceMapper.insert(itemInventorySeedSource);
        return findByItemInventoryId(itemInventorySeedSource.getItemInventoryId()).orElseThrow();
    }

    public int deleteByItemInventoryId(Integer itemInventoryId) {
        return itemInventorySeedSourceMapper.deleteByItemInventoryId(itemInventoryId);
    }
}
