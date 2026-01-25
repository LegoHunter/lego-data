package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.bricklink.data.lego.dto.BricklinkItem;
import net.lego.data.v1.dto.Item;
import net.lego.data.v1.mybatis.mapper.ItemMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("itemDaoV1")
@RequiredArgsConstructor
public class ItemDao {
    private final ItemMapperV1 itemMapper;

    public List<Item> getAll() {
        return itemMapper.getAll();
    }

    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    public List<Item> getAllWithNoBricklinkItem() {
        return itemMapper.getAllWithNoBricklinkItem();
    }

    public void insertItem(Item item) {
        itemMapper.insertItem(item);
    }

    public Item findItemById(int itemId) {
        return itemMapper.findItemById(itemId);
    }

    public List<Item> findItemByNumber(String itemNumber) {
        return itemMapper.findItemByNumber(itemNumber);
    }

    public void insertBricklinkItem(BricklinkItem bricklinkItem) {
        itemMapper.insertBricklinkItem(bricklinkItem);
    }

    public void updateItem(Item item) {
        itemMapper.updateItem(item);
    }
}
