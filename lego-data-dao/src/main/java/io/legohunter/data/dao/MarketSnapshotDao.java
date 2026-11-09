package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketSnapshot;
import io.legohunter.data.mybatis.mapper.MarketSnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.market_snapshot}. */
@Component
@RequiredArgsConstructor
public class MarketSnapshotDao {
    private final MarketSnapshotMapper mapper;

    public Optional<MarketSnapshot> findByMarketSnapshotId(Long marketSnapshotId) {
        return mapper.findByMarketSnapshotId(marketSnapshotId);
    }

    public Optional<MarketSnapshot> findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(
            String marketplaceCode,
            Integer canonicalItemId,
            String queryConditionCode
    ) {
        return mapper.findLatestByMarketplaceCodeAndCanonicalItemIdAndQueryConditionCode(marketplaceCode, canonicalItemId, queryConditionCode);
    }

    /** Price history: the snapshots of one item and condition captured in [from, to), oldest first. */
    public List<MarketSnapshot> findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(
            Integer canonicalItemId,
            String queryConditionCode,
            ZonedDateTime from,
            ZonedDateTime to
    ) {
        return mapper.findByCanonicalItemIdAndQueryConditionCodeCapturedBetween(canonicalItemId, queryConditionCode, from, to);
    }

    public MarketSnapshot insert(MarketSnapshot marketSnapshot) {
        mapper.insert(marketSnapshot);
        return findByMarketSnapshotId(marketSnapshot.getMarketSnapshotId()).orElseThrow();
    }
}
