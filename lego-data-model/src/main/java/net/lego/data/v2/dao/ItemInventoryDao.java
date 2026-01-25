package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.ItemInventory;
import net.lego.data.v2.mybatis.mapper.ItemInventoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
}
