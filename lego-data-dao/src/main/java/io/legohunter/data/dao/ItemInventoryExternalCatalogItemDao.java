package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.mybatis.mapper.ItemInventoryExternalCatalogItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventoryExternalCatalogItemDao {
    private final ItemInventoryExternalCatalogItemMapper itemInventoryExternalCatalogItemMapper;

    public Set<ItemInventoryExternalCatalogItem> findAll() {
        return itemInventoryExternalCatalogItemMapper.findAll();
    }

    public Optional<ItemInventoryExternalCatalogItem> findByItemInventoryIdAndExternalCatalogItemId(Integer itemInventoryId, Integer externalCatalogItemId) {
        return itemInventoryExternalCatalogItemMapper.findByItemInventoryIdAndExternalCatalogItemId(itemInventoryId, externalCatalogItemId);
    }

    public Set<ItemInventoryExternalCatalogItem> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryExternalCatalogItemMapper.findByItemInventoryId(itemInventoryId);
    }

    public Set<ItemInventoryExternalCatalogItem> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return itemInventoryExternalCatalogItemMapper.findByExternalCatalogItemId(externalCatalogItemId);
    }

    public ItemInventoryExternalCatalogItem insert(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem) {
        itemInventoryExternalCatalogItemMapper.insert(itemInventoryExternalCatalogItem);
        return findByItemInventoryIdAndExternalCatalogItemId(itemInventoryExternalCatalogItem.getItemInventoryId(), itemInventoryExternalCatalogItem.getExternalCatalogItemId()).orElseThrow();
    }

    public ItemInventoryExternalCatalogItem update(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem) {
        itemInventoryExternalCatalogItemMapper.update(itemInventoryExternalCatalogItem);
        return findByItemInventoryIdAndExternalCatalogItemId(itemInventoryExternalCatalogItem.getItemInventoryId(), itemInventoryExternalCatalogItem.getExternalCatalogItemId()).orElseThrow();
    }

    public void delete(Integer itemInventoryId, Integer externalCatalogItemId) {
        itemInventoryExternalCatalogItemMapper.delete(itemInventoryId, externalCatalogItemId);
    }

    public ItemInventoryExternalCatalogItem upsert(ItemInventoryExternalCatalogItem itemInventoryExternalCatalogItem) {
        itemInventoryExternalCatalogItemMapper.upsert(itemInventoryExternalCatalogItem);
        return findByItemInventoryIdAndExternalCatalogItemId(itemInventoryExternalCatalogItem.getItemInventoryId(), itemInventoryExternalCatalogItem.getExternalCatalogItemId()).orElseThrow();
    }
}
