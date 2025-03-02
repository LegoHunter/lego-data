package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.Item;
import net.lego.data.v2.mybatis.mapper.ItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemDao {
    private final ItemMapper itemMapper;

    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    public Optional<Item> findByItemId(Integer itemId) {
        return itemMapper.findByItemId(itemId);
    }

    public Optional<Item> findByItemNumber(String itemNumber) {
        return itemMapper.findByItemNumber(itemNumber);
    }

    public void insert(Item item) {
        itemMapper.insert(item);
    }

    public void migrate(Item item) {
        itemMapper.migrate(item);
    }

    public void update(Item item) {
        itemMapper.update(item);
    }
}
