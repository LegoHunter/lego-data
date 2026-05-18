package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.InventoryIndex;
import net.lego.data.v1.mybatis.mapper.InventoryIndexMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("inventoryIndexDaoV1")
@RequiredArgsConstructor
public class InventoryIndexDao {
    private final InventoryIndexMapperV1 inventoryIndexMapper;

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

    public void udpate(InventoryIndex inventoryIndex) {
        inventoryIndexMapper.update(inventoryIndex);
    }
}
