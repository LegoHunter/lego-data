package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.mybatis.mapper.PricingCrawlWorkItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingCrawlWorkItemDao {
    private final PricingCrawlWorkItemMapper pricingCrawlWorkItemMapper;

    public Set<PricingCrawlWorkItem> findAll() {
        return pricingCrawlWorkItemMapper.findAll();
    }

    public Optional<PricingCrawlWorkItem> findByPricingCrawlWorkItemId(Long pricingCrawlWorkItemId) {
        return pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(pricingCrawlWorkItemId);
    }

    public Set<PricingCrawlWorkItem> findByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingCrawlWorkItemMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingCrawlWorkItem> findByWorkStatusCode(String workStatusCode) {
        return pricingCrawlWorkItemMapper.findByWorkStatusCode(workStatusCode);
    }

    public Set<PricingCrawlWorkItem> findDueByWorkStatusCode(String workStatusCode, ZonedDateTime dueAt, int limit) {
        return pricingCrawlWorkItemMapper.findDueByWorkStatusCode(workStatusCode, dueAt, limit);
    }

    public PricingCrawlWorkItem insert(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.insert(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }

    public PricingCrawlWorkItem update(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.update(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }

    public void delete(Long pricingCrawlWorkItemId) {
        pricingCrawlWorkItemMapper.delete(pricingCrawlWorkItemId);
    }

    public PricingCrawlWorkItem upsert(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.upsert(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }
}
