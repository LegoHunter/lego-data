package net.bricklink.data.lego.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dto.BricklinkSaleItem;
import net.bricklink.data.lego.ibatis.mapper.BricklinkSaleItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BricklinkSaleItemDao {
    private final BricklinkSaleItemMapper bricklinkSaleItemMapper;

    public Integer upsert(BricklinkSaleItem bricklinkSaleItem) {
        return bricklinkSaleItemMapper.upsert(bricklinkSaleItem);
    }

    public void updateBricklinkSaleItemSold(Long blItemId, String newOrUsed, List<Integer> currentlyForSaleInventoryIds) {
        if (currentlyForSaleInventoryIds.isEmpty()) {
            log.info("No items currently for sale for [{}] New/Used [{}]", blItemId, newOrUsed);
        } else {
            bricklinkSaleItemMapper.updateBricklinkSaleItemSold(blItemId, newOrUsed, currentlyForSaleInventoryIds);
        }
    }

    public List<BricklinkSaleItem> getBrinklinkSaleItems(Long blItemId, String newOrUsed, String completeness) {
        try {
            return bricklinkSaleItemMapper.getBrinklinkSaleItems(blItemId, newOrUsed, completeness);
        } catch (Exception e) {
            log.error("Unable to get Bricklink Sale Items for blItemId [{}], newOrUsed [{}], completeness [{}]", blItemId, newOrUsed, completeness, e);
            throw e;
        }
    }

    public List<BricklinkSaleItem> getAll() {
        return bricklinkSaleItemMapper.getAll();
    }
}
