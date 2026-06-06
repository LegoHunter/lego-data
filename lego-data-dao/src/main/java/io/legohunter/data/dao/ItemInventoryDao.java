package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventoryDao {
    private final ItemInventoryMapper itemInventoryMapper;

    public Set<ItemInventory> findAll() {
        return itemInventoryMapper.findAll();
    }

    public Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryMapper.findByItemInventoryId(itemInventoryId);
    }

    public Optional<ItemInventory> findByUuid(String uuid) {
        return itemInventoryMapper.findByUuid(uuid);
    }

    public ItemInventory insert(ItemInventory itemInventory) {
        itemInventoryMapper.insert(itemInventory);
        return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
    }

    public ItemInventory update(ItemInventory itemInventory) {
        itemInventoryMapper.update(itemInventory);
        return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
    }

    public void delete(Integer itemInventoryId) {
        itemInventoryMapper.delete(itemInventoryId);
    }

    public ItemInventory upsert(ItemInventory itemInventory) {
        itemInventoryMapper.upsert(itemInventory);
        if (itemInventory.getItemInventoryId() != null) {
            return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
        }
        return findByUuid(itemInventory.getUuid()).orElseThrow();
    }
}
