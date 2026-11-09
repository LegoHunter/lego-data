package io.legohunter.data.dao;

import io.legohunter.data.dto.Marketplace;
import io.legohunter.data.mybatis.mapper.MarketplaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.marketplace}. */
@Component
@RequiredArgsConstructor
public class MarketplaceDao {
    private final MarketplaceMapper mapper;

    public List<Marketplace> findAll() {
        return mapper.findAll();
    }

    public Optional<Marketplace> findByMarketplaceCode(String marketplaceCode) {
        return mapper.findByMarketplaceCode(marketplaceCode);
    }
}
