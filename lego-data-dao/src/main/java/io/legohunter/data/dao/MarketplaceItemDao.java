package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceItem;
import io.legohunter.data.mybatis.mapper.MarketplaceItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.marketplace_item}. */
@Component
@RequiredArgsConstructor
public class MarketplaceItemDao {
    private final MarketplaceItemMapper mapper;

    public Optional<MarketplaceItem> findByMarketplaceItemId(Integer marketplaceItemId) {
        return mapper.findByMarketplaceItemId(marketplaceItemId);
    }

    public Optional<MarketplaceItem> findByMarketplaceCodeAndCanonicalItemId(String marketplaceCode, Integer canonicalItemId) {
        return mapper.findByMarketplaceCodeAndCanonicalItemId(marketplaceCode, canonicalItemId);
    }

    public MarketplaceItem insert(MarketplaceItem marketplaceItem) {
        mapper.insert(marketplaceItem);
        return findByMarketplaceItemId(marketplaceItem.getMarketplaceItemId()).orElseThrow();
    }

    public MarketplaceItem update(MarketplaceItem marketplaceItem) {
        mapper.update(marketplaceItem);
        return findByMarketplaceItemId(marketplaceItem.getMarketplaceItemId()).orElseThrow();
    }
}
