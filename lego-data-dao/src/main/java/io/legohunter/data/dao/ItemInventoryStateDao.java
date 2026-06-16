package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventoryState;
import io.legohunter.data.mybatis.mapper.ItemInventoryStateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventoryStateDao {
    private final ItemInventoryStateMapper itemInventoryStateMapper;

    public Set<ItemInventoryState> findAll() {
        return itemInventoryStateMapper.findAll();
    }

    public Optional<ItemInventoryState> findByInventoryStateCode(final String inventoryStateCode) {
        return itemInventoryStateMapper.findByInventoryStateCode(inventoryStateCode);
    }

    public ItemInventoryState insert(ItemInventoryState itemInventoryState) {
        itemInventoryStateMapper.insert(itemInventoryState);
        return itemInventoryState;
    }

    public ItemInventoryState update(ItemInventoryState itemInventoryState) {
        itemInventoryStateMapper.update(itemInventoryState);
        return findByInventoryStateCode(itemInventoryState.getInventoryStateCode()).orElseThrow();
    }

    public void delete(String inventoryStateCode) {
        itemInventoryStateMapper.delete(inventoryStateCode);
    }

    public ItemInventoryState upsert(ItemInventoryState itemInventoryState) {
        itemInventoryStateMapper.upsert(itemInventoryState);
        return findByInventoryStateCode(itemInventoryState.getInventoryStateCode()).orElseThrow();
    }
}
