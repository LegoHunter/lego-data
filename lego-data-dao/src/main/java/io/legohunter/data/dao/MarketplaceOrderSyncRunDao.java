package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrderSyncRun;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderSyncRunMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceOrderSyncRunDao {
    private final MarketplaceOrderSyncRunMapper marketplaceOrderSyncRunMapper;

    public Set<MarketplaceOrderSyncRun> findAll() {
        return marketplaceOrderSyncRunMapper.findAll();
    }

    public Optional<MarketplaceOrderSyncRun> findByMarketplaceOrderSyncRunId(Integer marketplaceOrderSyncRunId) {
        return marketplaceOrderSyncRunMapper.findByMarketplaceOrderSyncRunId(marketplaceOrderSyncRunId);
    }

    public MarketplaceOrderSyncRun insert(MarketplaceOrderSyncRun marketplaceOrderSyncRun) {
        marketplaceOrderSyncRunMapper.insert(marketplaceOrderSyncRun);
        return marketplaceOrderSyncRun;
    }

    public int update(MarketplaceOrderSyncRun marketplaceOrderSyncRun) {
        return marketplaceOrderSyncRunMapper.update(marketplaceOrderSyncRun);
    }

    public int delete(Integer marketplaceOrderSyncRunId) {
        return marketplaceOrderSyncRunMapper.delete(marketplaceOrderSyncRunId);
    }

    public MarketplaceOrderSyncRun upsert(MarketplaceOrderSyncRun marketplaceOrderSyncRun) {
        marketplaceOrderSyncRunMapper.upsert(marketplaceOrderSyncRun);
        return marketplaceOrderSyncRun;
    }
}
