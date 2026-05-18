package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemInventoryDao {
    private final ItemInventoryMapper itemInventoryMapper;

    public List<ItemInventory> findAll() {
        return itemInventoryMapper.findAll();
    }

    public Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryMapper.findByItemInventoryId(itemInventoryId);
    }

    public Optional<ItemInventory> findByUuid(String uuid) {
        return itemInventoryMapper.findByUuid(uuid);
    }

    public void insert(ItemInventory itemInventory) {
        itemInventoryMapper.insert(itemInventory);
    }

    public void update(ItemInventory itemInventory) {
        itemInventoryMapper.update(itemInventory);
    }

    public void upsert(ItemInventory itemInventory) {
        Integer itemInventoryId = itemInventory.getItemInventoryId();
        if (Optional.ofNullable(itemInventoryId).isPresent()) {
            update(itemInventory);
            log.info("Updated ItemInventory [{}]", itemInventory);
        } else {
            String uuid = itemInventory.getUuid();
            itemInventoryMapper.findByUuid(uuid).ifPresentOrElse(existingItemInventory -> {
                itemInventory.setItemInventoryId(existingItemInventory.getItemInventoryId());
                update(itemInventory);
                log.info("Updated ItemInventory [{}]", itemInventory);
            }, () -> {
                itemInventoryMapper.insert(itemInventory);
                log.info("Inserted ItemInventory [{}]", itemInventory);
            });
        }
    }
}
