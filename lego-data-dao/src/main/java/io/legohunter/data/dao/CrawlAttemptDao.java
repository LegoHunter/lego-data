package io.legohunter.data.dao;

import io.legohunter.data.dto.CrawlAttempt;
import io.legohunter.data.mybatis.mapper.CrawlAttemptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.crawl_attempt}. */
@Component
@RequiredArgsConstructor
public class CrawlAttemptDao {
    private final CrawlAttemptMapper mapper;

    public Optional<CrawlAttempt> findByCrawlAttemptId(Long crawlAttemptId) {
        return mapper.findByCrawlAttemptId(crawlAttemptId);
    }

    public List<CrawlAttempt> findLatestByCrawlTargetId(Long crawlTargetId, int limit) {
        return mapper.findLatestByCrawlTargetId(crawlTargetId, limit);
    }

    /** The latest failed attempts on one marketplace, newest first. */
    public List<CrawlAttempt> findRecentFailures(String marketplaceCode, int limit) {
        return mapper.findRecentFailures(marketplaceCode, Math.max(1, limit));
    }

    public CrawlAttempt insert(CrawlAttempt crawlAttempt) {
        mapper.insert(crawlAttempt);
        return findByCrawlAttemptId(crawlAttempt.getCrawlAttemptId()).orElseThrow();
    }

    public CrawlAttempt finish(CrawlAttempt crawlAttempt) {
        mapper.finish(crawlAttempt);
        return findByCrawlAttemptId(crawlAttempt.getCrawlAttemptId()).orElseThrow();
    }
}
