package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrder;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceOrderDao {
    private final MarketplaceOrderMapper marketplaceOrderMapper;

    public Set<MarketplaceOrder> findAll() {
        return marketplaceOrderMapper.findAll();
    }

    public Optional<MarketplaceOrder> findByMarketplaceOrderId(Integer marketplaceOrderId) {
        return marketplaceOrderMapper.findByMarketplaceOrderId(marketplaceOrderId);
    }

    public Optional<MarketplaceOrder> findByMarketplaceCodeAndExternalOrderId(String marketplaceCode, String externalOrderId) {
        return marketplaceOrderMapper.findByMarketplaceCodeAndExternalOrderId(marketplaceCode, externalOrderId);
    }

    public MarketplaceOrder insert(MarketplaceOrder marketplaceOrder) {
        marketplaceOrderMapper.insert(marketplaceOrder);
        return marketplaceOrder;
    }

    public int update(MarketplaceOrder marketplaceOrder) {
        return marketplaceOrderMapper.update(marketplaceOrder);
    }

    public int delete(Integer marketplaceOrderId) {
        return marketplaceOrderMapper.delete(marketplaceOrderId);
    }

    public MarketplaceOrder upsert(MarketplaceOrder marketplaceOrder) {
        marketplaceOrderMapper.upsert(marketplaceOrder);
        return marketplaceOrder;
    }
}
