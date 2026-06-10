package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrderPayload;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderPayloadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceOrderPayloadDao {
    private final MarketplaceOrderPayloadMapper marketplaceOrderPayloadMapper;

    public Set<MarketplaceOrderPayload> findAll() {
        return marketplaceOrderPayloadMapper.findAll();
    }

    public Optional<MarketplaceOrderPayload> findByMarketplaceOrderPayloadId(Integer marketplaceOrderPayloadId) {
        return marketplaceOrderPayloadMapper.findByMarketplaceOrderPayloadId(marketplaceOrderPayloadId);
    }

    public MarketplaceOrderPayload insert(MarketplaceOrderPayload marketplaceOrderPayload) {
        marketplaceOrderPayloadMapper.insert(marketplaceOrderPayload);
        return marketplaceOrderPayload;
    }

    public int update(MarketplaceOrderPayload marketplaceOrderPayload) {
        return marketplaceOrderPayloadMapper.update(marketplaceOrderPayload);
    }

    public int delete(Integer marketplaceOrderPayloadId) {
        return marketplaceOrderPayloadMapper.delete(marketplaceOrderPayloadId);
    }

    public MarketplaceOrderPayload upsert(MarketplaceOrderPayload marketplaceOrderPayload) {
        marketplaceOrderPayloadMapper.upsert(marketplaceOrderPayload);
        return marketplaceOrderPayload;
    }
}
