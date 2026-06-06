package io.legohunter.data.dao;

import io.legohunter.data.dto.EbayMarketplaceListing;
import io.legohunter.data.mybatis.mapper.EbayMarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EbayMarketplaceListingDao {
    private final EbayMarketplaceListingMapper ebayMarketplaceListingMapper;

    public Set<EbayMarketplaceListing> findAll() {
        return ebayMarketplaceListingMapper.findAll();
    }

    public Optional<EbayMarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId) {
        return ebayMarketplaceListingMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Optional<EbayMarketplaceListing> findByEbayItemId(String ebayItemId) {
        return ebayMarketplaceListingMapper.findByEbayItemId(ebayItemId);
    }

    public EbayMarketplaceListing insert(EbayMarketplaceListing ebayMarketplaceListing) {
        ebayMarketplaceListingMapper.insert(ebayMarketplaceListing);
        return findByMarketplaceListingId(ebayMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public EbayMarketplaceListing update(EbayMarketplaceListing ebayMarketplaceListing) {
        ebayMarketplaceListingMapper.update(ebayMarketplaceListing);
        return findByMarketplaceListingId(ebayMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public void delete(Integer marketplaceListingId) {
        ebayMarketplaceListingMapper.delete(marketplaceListingId);
    }

    public EbayMarketplaceListing upsert(EbayMarketplaceListing ebayMarketplaceListing) {
        ebayMarketplaceListingMapper.upsert(ebayMarketplaceListing);
        return findByMarketplaceListingId(ebayMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }
}
