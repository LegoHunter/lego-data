package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventorySaleIntent;
import io.legohunter.data.mybatis.mapper.ItemInventorySaleIntentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventorySaleIntentDao {
    private final ItemInventorySaleIntentMapper itemInventorySaleIntentMapper;

    public Set<ItemInventorySaleIntent> findAll() {
        return itemInventorySaleIntentMapper.findAll();
    }

    public Optional<ItemInventorySaleIntent> findBySaleIntentCode(final String saleIntentCode) {
        return itemInventorySaleIntentMapper.findBySaleIntentCode(saleIntentCode);
    }

    public ItemInventorySaleIntent insert(ItemInventorySaleIntent itemInventorySaleIntent) {
        itemInventorySaleIntentMapper.insert(itemInventorySaleIntent);
        return itemInventorySaleIntent;
    }

    public ItemInventorySaleIntent update(ItemInventorySaleIntent itemInventorySaleIntent) {
        itemInventorySaleIntentMapper.update(itemInventorySaleIntent);
        return findBySaleIntentCode(itemInventorySaleIntent.getSaleIntentCode()).orElseThrow();
    }

    public void delete(String saleIntentCode) {
        itemInventorySaleIntentMapper.delete(saleIntentCode);
    }

    public ItemInventorySaleIntent upsert(ItemInventorySaleIntent itemInventorySaleIntent) {
        itemInventorySaleIntentMapper.upsert(itemInventorySaleIntent);
        return findBySaleIntentCode(itemInventorySaleIntent.getSaleIntentCode()).orElseThrow();
    }
}
