package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrder;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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

    public Set<MarketplaceOrder> findFulfillmentCandidates(String marketplaceCode, Collection<String> externalStatusCodes, int limit) {
        int effectiveLimit = Math.max(limit, 1);
        Map<Integer, MarketplaceOrder> ordersById = new LinkedHashMap<>();
        for (String externalStatusCode : externalStatusCodes) {
            if (externalStatusCode == null || externalStatusCode.isBlank() || ordersById.size() >= effectiveLimit) {
                continue;
            }
            marketplaceOrderMapper.findFulfillmentCandidatesByStatus(
                    marketplaceCode,
                    externalStatusCode.trim(),
                    effectiveLimit - ordersById.size()
            ).forEach(order -> ordersById.putIfAbsent(order.getMarketplaceOrderId(), order));
        }
        return new LinkedHashSet<>(ordersById.values());
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
