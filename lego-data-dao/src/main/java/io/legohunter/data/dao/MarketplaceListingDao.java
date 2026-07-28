package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PricingHydrationGap;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
                Math.max(1, limit)
        );
    }

    public Set<MarketplaceListing> findByListingExternalServiceIdAndListingStatusCodes(
            Integer listingExternalServiceId,
            Set<String> listingStatusCodes,
            int limit
    ) {
        if (listingStatusCodes == null || listingStatusCodes.isEmpty()) {
            return Set.of();
        }
        return marketplaceListingMapper.findByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                listingStatusCodes,
                Math.max(1, limit)
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                Set.of(listingStatusCode),
                limit
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
            Integer listingExternalServiceId,
            Set<String> listingStatusCodes,
            int limit
    ) {
        if (listingStatusCodes == null || listingStatusCodes.isEmpty()) {
            return Set.of();
        }
        return marketplaceListingMapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                listingStatusCodes,
                Math.max(1, limit)
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit,
            boolean requireCurrentSnapshot
    ) {
        return findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                Set.of(listingStatusCode),
                limit,
                requireCurrentSnapshot
        );
    }

    public Set<MarketplaceListing> findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
            Integer listingExternalServiceId,
            Set<String> listingStatusCodes,
            int limit,
            boolean requireCurrentSnapshot
    ) {
        if (listingStatusCodes == null || listingStatusCodes.isEmpty()) {
            return Set.of();
        }
        if (requireCurrentSnapshot) {
            return marketplaceListingMapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCodes(
                    listingExternalServiceId,
                    listingStatusCodes,
                    Math.max(1, limit)
            );
        }
        return findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                listingStatusCodes,
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
        return findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                Set.of(listingStatusCode),
                pendingStatusCode,
                claimedStatusCode,
                asOf,
                limit
        );
    }

    public Set<MarketplaceListing> findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCodes(
            Integer listingExternalServiceId,
            Set<String> listingStatusCodes,
            String pendingStatusCode,
            String claimedStatusCode,
            ZonedDateTime asOf,
            int limit
    ) {
        if (listingStatusCodes == null || listingStatusCodes.isEmpty()) {
            return Set.of();
        }
        return marketplaceListingMapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCodes(
                listingExternalServiceId,
                listingStatusCodes,
                pendingStatusCode,
                claimedStatusCode,
                asOf,
                Math.max(1, limit)
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

    public Optional<MarketplaceListing> updateUnitPrice(Integer marketplaceListingId, BigDecimal unitPrice, ZonedDateTime updatedAt) {
        int updated = marketplaceListingMapper.updateUnitPrice(marketplaceListingId, unitPrice, updatedAt);
        if (updated == 0) {
            return Optional.empty();
        }
        return findByMarketplaceListingId(marketplaceListingId);
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
