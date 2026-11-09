package io.legohunter.data.dao;

import io.legohunter.data.dto.CrawlTarget;
import io.legohunter.data.mybatis.mapper.CrawlTargetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.crawl_target}. */
@Component
@RequiredArgsConstructor
public class CrawlTargetDao {
    private final CrawlTargetMapper mapper;

    public Optional<CrawlTarget> findByCrawlTargetId(Long crawlTargetId) {
        return mapper.findByCrawlTargetId(crawlTargetId);
    }

    public Optional<CrawlTarget> findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            String marketplaceCode,
            Integer canonicalItemId,
            String queryConditionCode
    ) {
        return mapper.findByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(marketplaceCode, canonicalItemId, queryConditionCode);
    }

    public List<CrawlTarget> findDue(String marketplaceCode, ZonedDateTime now, int limit) {
        return mapper.findDue(marketplaceCode, now, limit);
    }

    /** True when this caller won the claim; false when another crawler claimed the target first. */
    public boolean claim(Long crawlTargetId, String claimedBy, ZonedDateTime claimedAt, ZonedDateTime claimExpiresAt) {
        return mapper.claim(crawlTargetId, claimedBy, claimedAt, claimExpiresAt) == 1;
    }

    public CrawlTarget insert(CrawlTarget crawlTarget) {
        mapper.insert(crawlTarget);
        return findByCrawlTargetId(crawlTarget.getCrawlTargetId()).orElseThrow();
    }

    public CrawlTarget update(CrawlTarget crawlTarget) {
        mapper.update(crawlTarget);
        return findByCrawlTargetId(crawlTarget.getCrawlTargetId()).orElseThrow();
    }
}
