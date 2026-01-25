package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.InventoryIndex;
import net.lego.data.v2.mybatis.mapper.InventoryIndexMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryIndexDao {
    private final InventoryIndexMapper inventoryIndexMapper;

    public List<InventoryIndex> findAll() {
        return inventoryIndexMapper.getAll();
    }

    public List<InventoryIndex> getAllForBox(int boxId) {
        return inventoryIndexMapper.getAllForBox(boxId);
    }

    public List<InventoryIndex> getAllWithNoItem() {
        return inventoryIndexMapper.getAllWithNoItem();
    }

    public void insert(InventoryIndex inventoryIndex) {
        inventoryIndexMapper.insert(inventoryIndex);
    }

    public void update(InventoryIndex inventoryIndex) {
        inventoryIndexMapper.update(inventoryIndex);
    }

    public Optional<InventoryIndex> findByItemNumber(String itemNumber) {
        return inventoryIndexMapper.findByItemNumber(itemNumber);
    }


    public Optional<InventoryIndex> findByBoxIdAndBoxIndex(Integer boxId, Integer boxIndex) {
        return inventoryIndexMapper.findByBoxIdAndBoxIndex(boxId, boxIndex);
    }

    public Optional<InventoryIndex> findByBoxIdAndBoxIndexAndItemNumber(Integer boxId, Integer boxIndex, String itemNumber) {
        try {
            return inventoryIndexMapper.findByBoxIdAndBoxIndexAndItemNumber(itemNumber, boxId, boxIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
