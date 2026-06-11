package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrderItem;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceOrderItemDao {
    private final MarketplaceOrderItemMapper marketplaceOrderItemMapper;

    public Set<MarketplaceOrderItem> findAll() {
        return marketplaceOrderItemMapper.findAll();
    }

    public Optional<MarketplaceOrderItem> findByMarketplaceOrderItemId(Integer marketplaceOrderItemId) {
        return marketplaceOrderItemMapper.findByMarketplaceOrderItemId(marketplaceOrderItemId);
    }

    public Set<MarketplaceOrderItem> findByMarketplaceOrderId(Integer marketplaceOrderId) {
        return marketplaceOrderItemMapper.findByMarketplaceOrderId(marketplaceOrderId);
    }

    public Set<MarketplaceOrderItem> findByMarketplaceOrderIdAndExternalInventoryId(Integer marketplaceOrderId, String externalInventoryId) {
        return marketplaceOrderItemMapper.findByMarketplaceOrderIdAndExternalInventoryId(marketplaceOrderId, externalInventoryId);
    }

    public MarketplaceOrderItem insert(MarketplaceOrderItem marketplaceOrderItem) {
        marketplaceOrderItemMapper.insert(marketplaceOrderItem);
        return marketplaceOrderItem;
    }

    public int update(MarketplaceOrderItem marketplaceOrderItem) {
        return marketplaceOrderItemMapper.update(marketplaceOrderItem);
    }

    public int delete(Integer marketplaceOrderItemId) {
        return marketplaceOrderItemMapper.delete(marketplaceOrderItemId);
    }

    public int deleteByMarketplaceOrderId(Integer marketplaceOrderId) {
        return marketplaceOrderItemMapper.deleteByMarketplaceOrderId(marketplaceOrderId);
    }

    public MarketplaceOrderItem upsert(MarketplaceOrderItem marketplaceOrderItem) {
        marketplaceOrderItemMapper.upsert(marketplaceOrderItem);
        return marketplaceOrderItem;
    }
}
