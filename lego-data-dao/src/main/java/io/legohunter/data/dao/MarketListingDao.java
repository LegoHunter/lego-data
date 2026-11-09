package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketListing;
import io.legohunter.data.mybatis.mapper.MarketListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.market_listing}. */
@Component
@RequiredArgsConstructor
public class MarketListingDao {
    private final MarketListingMapper mapper;

    public Optional<MarketListing> findByMarketListingId(Long marketListingId) {
        return mapper.findByMarketListingId(marketListingId);
    }

    public List<MarketListing> findByMarketSnapshotId(Long marketSnapshotId) {
        return mapper.findByMarketSnapshotId(marketSnapshotId);
    }

    /** The lots of one sub-condition market in a snapshot, cheapest first. */
    public List<MarketListing> findComparablesByMarketSnapshotId(Long marketSnapshotId, String conditionCode, String completenessCode) {
        return mapper.findComparablesByMarketSnapshotId(marketSnapshotId, conditionCode, completenessCode);
    }

    public MarketListing insert(MarketListing marketListing) {
        mapper.insert(marketListing);
        return findByMarketListingId(marketListing.getMarketListingId()).orElseThrow();
    }
}
