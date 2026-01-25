package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.BricklinkInventory;
import net.lego.data.v1.mybatis.mapper.BricklinkInventoryMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("bricklinkInventoryDaoV1")
@RequiredArgsConstructor
public class BricklinkInventoryDao {
    private final BricklinkInventoryMapperV1 bricklinkInventoryMapperV1;

    public List<BricklinkInventory> findAll() {
        return bricklinkInventoryMapperV1.findAll();
    }

    public Optional<BricklinkInventory> findById(Integer blInventoryId) {
        return bricklinkInventoryMapperV1.findById(blInventoryId);
    }

    public Optional<BricklinkInventory> findByUuid(String uuid) {
        return bricklinkInventoryMapperV1.findByUuid(uuid);
    }
}
