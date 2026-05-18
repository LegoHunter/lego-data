package net.bricklink.data.lego.dao;

import lombok.RequiredArgsConstructor;
import net.bricklink.data.lego.dto.BricklinkInventory;
import net.bricklink.data.lego.ibatis.configuration.LegoDataException;
import net.bricklink.data.lego.ibatis.mapper.BricklinkInventoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BricklinkInventoryDao {
    private final BricklinkInventoryMapper bricklinkInventoryMapper;

    public List<BricklinkInventory> getAll() {
        return bricklinkInventoryMapper.getAll();
    }

    public List<BricklinkInventory> getAllForSale() {
        return bricklinkInventoryMapper.getAllForSale();
    }

    public List<BricklinkInventory> getInventoryWork() {
        return bricklinkInventoryMapper.getInventoryWork();
    }

    public void update(BricklinkInventory bricklinkInventory) {
        bricklinkInventory.setRemarks(String.format("Box[%d,%d] %s", bricklinkInventory.getBoxId(), bricklinkInventory.getBoxIndex(), bricklinkInventory.getUuid()));
        Optional.ofNullable(bricklinkInventory.getInternalComments()).ifPresent(ed -> {
            String remarks = String.format(bricklinkInventory.getRemarks() + "; %s", bricklinkInventory.getInternalComments());
            bricklinkInventory.setRemarks(remarks);
        });
        bricklinkInventoryMapper.update(bricklinkInventory);
    }

    public BricklinkInventory get(Integer blInventoryId) {
        return bricklinkInventoryMapper.get(blInventoryId);
    }
    public BricklinkInventory getByUuid(String uuid) {
        return bricklinkInventoryMapper.getByUuid(uuid);
    }
    public Optional<BricklinkInventory> getByInventoryId(Long inventoryId) {
        return Optional.ofNullable(bricklinkInventoryMapper.getByInventoryId(inventoryId));
    }

    public void setSynchronizedNow(Integer blInventoryId) {
        bricklinkInventoryMapper.setSynchronizedNow(blInventoryId);
    }

    public void setNotForSale(Integer blInventoryId) {
        bricklinkInventoryMapper.setNotForSale(blInventoryId);
    }

    public void setPrice(Integer blInventoryId, double price) {
        bricklinkInventoryMapper.setPrice(blInventoryId, price);
    }

    public void updateFromImageKeywords(BricklinkInventory bricklinkInventory) {
        bricklinkInventoryMapper.updateFromImageKeywords(bricklinkInventory);
    }

    public void updateOrder(Integer blInventoryId, final String orderId) {
        bricklinkInventoryMapper.updateOrder(blInventoryId, orderId);
    }

    public Optional<BricklinkInventory> findByBoxIdAndBoxIndex(Integer boxId, Integer boxIndex) {
        return bricklinkInventoryMapper.findByBoxIdAndBoxIndex(boxId, boxIndex);
    }

    public List<BricklinkInventory> findByBricklinkitemNumber(String blItemNumber) {
        return bricklinkInventoryMapper.findByBricklinkitemNumber(blItemNumber);
    }

    public BricklinkInventory insert(BricklinkInventory bricklinkInventory) {
        bricklinkInventoryMapper.insert(bricklinkInventory);
        Optional<BricklinkInventory> bricklinkInventoryOptional = bricklinkInventoryMapper.findByBoxIdAndBoxIndex(bricklinkInventory.getBoxId(), bricklinkInventory.getBoxIndex());
        return bricklinkInventoryOptional.orElseThrow(() -> new LegoDataException(String.format("Unable to insert BricklinkInventory - boxId/boxIndex not found [%s/%s]", bricklinkInventory.getBoxId(), bricklinkInventory.getBoxIndex())));
    }
}
