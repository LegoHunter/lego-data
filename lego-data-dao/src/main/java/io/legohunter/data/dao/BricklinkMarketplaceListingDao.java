package io.legohunter.data.dao;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.mybatis.mapper.BricklinkMarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BricklinkMarketplaceListingDao {
    private final BricklinkMarketplaceListingMapper bricklinkMarketplaceListingMapper;

    public Set<BricklinkMarketplaceListing> findAll() {
        return bricklinkMarketplaceListingMapper.findAll();
    }

    public Optional<BricklinkMarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId) {
        return bricklinkMarketplaceListingMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Optional<BricklinkMarketplaceListing> findByBricklinkInventoryId(Integer bricklinkInventoryId) {
        return bricklinkMarketplaceListingMapper.findByBricklinkInventoryId(bricklinkInventoryId);
    }

    public BricklinkMarketplaceListing insert(BricklinkMarketplaceListing bricklinkMarketplaceListing) {
        bricklinkMarketplaceListingMapper.insert(bricklinkMarketplaceListing);
        return findByMarketplaceListingId(bricklinkMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public BricklinkMarketplaceListing update(BricklinkMarketplaceListing bricklinkMarketplaceListing) {
        bricklinkMarketplaceListingMapper.update(bricklinkMarketplaceListing);
        return findByMarketplaceListingId(bricklinkMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }

    public void delete(Integer marketplaceListingId) {
        bricklinkMarketplaceListingMapper.delete(marketplaceListingId);
    }

    public BricklinkMarketplaceListing upsert(BricklinkMarketplaceListing bricklinkMarketplaceListing) {
        bricklinkMarketplaceListingMapper.upsert(bricklinkMarketplaceListing);
        return findByMarketplaceListingId(bricklinkMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }
}
