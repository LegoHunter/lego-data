package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingSnapshotListing;
import io.legohunter.data.mybatis.mapper.PricingSnapshotListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingSnapshotListingDao {
    private final PricingSnapshotListingMapper pricingSnapshotListingMapper;

    public Set<PricingSnapshotListing> findAll() {
        return pricingSnapshotListingMapper.findAll();
    }

    public Optional<PricingSnapshotListing> findByPricingSnapshotListingId(Long pricingSnapshotListingId) {
        return pricingSnapshotListingMapper.findByPricingSnapshotListingId(pricingSnapshotListingId);
    }

    public Set<PricingSnapshotListing> findByPricingSnapshotId(Long pricingSnapshotId) {
        return pricingSnapshotListingMapper.findByPricingSnapshotId(pricingSnapshotId);
    }

    public Optional<PricingSnapshotListing> findByPricingSnapshotIdAndExternalListingId(Long pricingSnapshotId, String externalListingId) {
        return pricingSnapshotListingMapper.findByPricingSnapshotIdAndExternalListingId(pricingSnapshotId, externalListingId);
    }

    public List<PricingSnapshotListing> findExactComparablesByPricingSnapshotId(Long pricingSnapshotId) {
        return pricingSnapshotListingMapper.findExactComparablesByPricingSnapshotId(pricingSnapshotId);
    }

    public PricingSnapshotListing insert(PricingSnapshotListing pricingSnapshotListing) {
        pricingSnapshotListingMapper.insert(pricingSnapshotListing);
        return findByPricingSnapshotListingId(pricingSnapshotListing.getPricingSnapshotListingId()).orElseThrow();
    }

    public PricingSnapshotListing update(PricingSnapshotListing pricingSnapshotListing) {
        pricingSnapshotListingMapper.update(pricingSnapshotListing);
        return findByPricingSnapshotListingId(pricingSnapshotListing.getPricingSnapshotListingId()).orElseThrow();
    }

    public void delete(Long pricingSnapshotListingId) {
        pricingSnapshotListingMapper.delete(pricingSnapshotListingId);
    }

    public PricingSnapshotListing upsert(PricingSnapshotListing pricingSnapshotListing) {
        pricingSnapshotListingMapper.upsert(pricingSnapshotListing);
        return findByPricingSnapshotListingId(pricingSnapshotListing.getPricingSnapshotListingId()).orElseThrow();
    }
}
