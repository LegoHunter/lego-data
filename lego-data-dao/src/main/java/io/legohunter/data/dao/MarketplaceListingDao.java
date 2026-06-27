package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingHydrationGap;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MarketplaceListingDao {
    private final MarketplaceListingMapper marketplaceListingMapper;

    public Set<MarketplaceListing> findAll() {
        return marketplaceListingMapper.findAll();
    }

    public Optional<MarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId) {
        return marketplaceListingMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<MarketplaceListing> findByItemInventoryId(Integer itemInventoryId) {
        return marketplaceListingMapper.findByItemInventoryId(itemInventoryId);
    }

    public Set<MarketplaceListing> findByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return marketplaceListingMapper.findByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit,
            boolean requireCurrentSnapshot
    ) {
        if (requireCurrentSnapshot) {
            return marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(
                    listingExternalServiceId,
                    listingStatusCode,
                    limit
            );
        }
        return findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit
        );
    }

    public Set<MarketplaceListing> findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            String pendingStatusCode,
            String claimedStatusCode,
            ZonedDateTime asOf,
            int limit
    ) {
        return marketplaceListingMapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                pendingStatusCode,
                claimedStatusCode,
                asOf,
                limit
        );
    }

    public Optional<MarketplaceListing> findByListingExternalServiceIdAndExternalListingId(Integer listingExternalServiceId, String externalListingId) {
        return marketplaceListingMapper.findByListingExternalServiceIdAndExternalListingId(listingExternalServiceId, externalListingId);
    }

    public Set<PricingHydrationGap> findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return marketplaceListingMapper.findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                Math.max(1, limit)
        );
    }

    public MarketplaceListing insert(MarketplaceListing marketplaceListing) {
        marketplaceListingMapper.insert(marketplaceListing);
        return findByMarketplaceListingId(marketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public MarketplaceListing update(MarketplaceListing marketplaceListing) {
        marketplaceListingMapper.update(marketplaceListing);
        return findByMarketplaceListingId(marketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public void delete(Integer marketplaceListingId) {
        marketplaceListingMapper.delete(marketplaceListingId);
    }

    public MarketplaceListing upsert(MarketplaceListing marketplaceListing) {
        marketplaceListingMapper.upsert(marketplaceListing);
        if (marketplaceListing.getMarketplaceListingId() != null) {
            return findByMarketplaceListingId(marketplaceListing.getMarketplaceListingId()).orElseThrow();
        }
        return findByListingExternalServiceIdAndExternalListingId(
                marketplaceListing.getListingExternalServiceId(),
                marketplaceListing.getExternalListingId()
        ).orElseThrow();
    }
}
