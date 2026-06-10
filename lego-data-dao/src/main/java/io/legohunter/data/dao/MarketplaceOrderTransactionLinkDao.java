package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceOrderTransactionLink;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderTransactionLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceOrderTransactionLinkDao {
    private final MarketplaceOrderTransactionLinkMapper marketplaceOrderTransactionLinkMapper;

    public Set<MarketplaceOrderTransactionLink> findAll() {
        return marketplaceOrderTransactionLinkMapper.findAll();
    }

    public Optional<MarketplaceOrderTransactionLink> findByMarketplaceOrderTransactionLinkId(Integer marketplaceOrderTransactionLinkId) {
        return marketplaceOrderTransactionLinkMapper.findByMarketplaceOrderTransactionLinkId(marketplaceOrderTransactionLinkId);
    }

    public MarketplaceOrderTransactionLink insert(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink) {
        marketplaceOrderTransactionLinkMapper.insert(marketplaceOrderTransactionLink);
        return marketplaceOrderTransactionLink;
    }

    public int update(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink) {
        return marketplaceOrderTransactionLinkMapper.update(marketplaceOrderTransactionLink);
    }

    public int delete(Integer marketplaceOrderTransactionLinkId) {
        return marketplaceOrderTransactionLinkMapper.delete(marketplaceOrderTransactionLinkId);
    }

    public MarketplaceOrderTransactionLink upsert(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink) {
        marketplaceOrderTransactionLinkMapper.upsert(marketplaceOrderTransactionLink);
        return marketplaceOrderTransactionLink;
    }
}
