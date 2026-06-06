package io.legohunter.data.dao;

import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public Optional<MarketplaceListing> findByListingExternalServiceIdAndExternalListingId(Integer listingExternalServiceId, String externalListingId) {
        return marketplaceListingMapper.findByListingExternalServiceIdAndExternalListingId(listingExternalServiceId, externalListingId);
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
