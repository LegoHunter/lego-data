package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceListingSyncRequest;
import io.legohunter.data.mybatis.mapper.MarketplaceListingSyncRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceListingSyncRequestDao {
    private final MarketplaceListingSyncRequestMapper marketplaceListingSyncRequestMapper;

    public Set<MarketplaceListingSyncRequest> findAll() {
        return marketplaceListingSyncRequestMapper.findAll();
    }

    public Optional<MarketplaceListingSyncRequest> findByMarketplaceListingSyncRequestId(Long marketplaceListingSyncRequestId) {
        return marketplaceListingSyncRequestMapper.findByMarketplaceListingSyncRequestId(marketplaceListingSyncRequestId);
    }

    public Set<MarketplaceListingSyncRequest> findByMarketplaceListingId(Integer marketplaceListingId) {
        return marketplaceListingSyncRequestMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<MarketplaceListingSyncRequest> findBySyncRequestStatusCode(String syncRequestStatusCode) {
        return marketplaceListingSyncRequestMapper.findBySyncRequestStatusCode(syncRequestStatusCode);
    }

    public Set<MarketplaceListingSyncRequest> findClaimableByStatusCode(String syncRequestStatusCode, ZonedDateTime asOf, int limit) {
        return marketplaceListingSyncRequestMapper.findClaimableByStatusCode(syncRequestStatusCode, asOf, Math.max(1, limit));
    }

    public long countBySyncRequestStatusCode(String syncRequestStatusCode) {
        return marketplaceListingSyncRequestMapper.countBySyncRequestStatusCode(syncRequestStatusCode);
    }

    public long countDueBySyncRequestStatusCode(String syncRequestStatusCode, ZonedDateTime asOf) {
        return marketplaceListingSyncRequestMapper.countDueBySyncRequestStatusCode(syncRequestStatusCode, asOf);
    }

    public MarketplaceListingSyncRequest insert(MarketplaceListingSyncRequest syncRequest) {
        marketplaceListingSyncRequestMapper.insert(syncRequest);
        return findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId()).orElseThrow();
    }

    public MarketplaceListingSyncRequest update(MarketplaceListingSyncRequest syncRequest) {
        marketplaceListingSyncRequestMapper.update(syncRequest);
        return findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId()).orElseThrow();
    }

    public MarketplaceListingSyncRequest upsert(MarketplaceListingSyncRequest syncRequest) {
        marketplaceListingSyncRequestMapper.upsert(syncRequest);
        if (syncRequest.getMarketplaceListingSyncRequestId() != null) {
            return findByMarketplaceListingSyncRequestId(syncRequest.getMarketplaceListingSyncRequestId()).orElseThrow();
        }
        return marketplaceListingSyncRequestMapper.findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(
                syncRequest.getMarketplaceListingId(),
                syncRequest.getPricingDecisionId(),
                syncRequest.getSyncRequestTypeCode()
        ).orElseThrow();
    }

    public Optional<MarketplaceListingSyncRequest> claim(Long marketplaceListingSyncRequestId, String fromStatusCode, String claimedStatusCode, ZonedDateTime claimedAt) {
        int updated = marketplaceListingSyncRequestMapper.claim(marketplaceListingSyncRequestId, fromStatusCode, claimedStatusCode, claimedAt);
        if (updated == 0) {
            return Optional.empty();
        }
        return findByMarketplaceListingSyncRequestId(marketplaceListingSyncRequestId);
    }

    public void delete(Long marketplaceListingSyncRequestId) {
        marketplaceListingSyncRequestMapper.delete(marketplaceListingSyncRequestId);
    }
}
