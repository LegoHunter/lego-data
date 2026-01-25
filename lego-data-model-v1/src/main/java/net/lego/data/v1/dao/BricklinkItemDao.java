package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.BricklinkItem;
import net.lego.data.v1.mybatis.mapper.BricklinkItemMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("bricklinkItemDaoV1")
@RequiredArgsConstructor
public class BricklinkItemDao {
    private final BricklinkItemMapperV1 bricklinkItemMapperV1;

    public List<BricklinkItem> findAll() {
        return bricklinkItemMapperV1.findAll();
    }

    public Optional<BricklinkItem> findByItemId(Integer itemId) {
        return bricklinkItemMapperV1.findByItemId(itemId);
    }

    public Optional<BricklinkItem> findByBricklinkItemNumber(String blItemNumber) {
        return bricklinkItemMapperV1.findByBricklinkItemNumber(blItemNumber);

    }

    public Optional<BricklinkItem> findByBricklinkItemId(Long blItemId) {
        return bricklinkItemMapperV1.findByBricklinkItemId(blItemId);
    }
}
