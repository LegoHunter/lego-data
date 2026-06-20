package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingSnapshot;
import io.legohunter.data.mybatis.mapper.PricingSnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingSnapshotDao {
    private final PricingSnapshotMapper pricingSnapshotMapper;

    public Set<PricingSnapshot> findAll() {
        return pricingSnapshotMapper.findAll();
    }

    public Optional<PricingSnapshot> findByPricingSnapshotId(Long pricingSnapshotId) {
        return pricingSnapshotMapper.findByPricingSnapshotId(pricingSnapshotId);
    }

    public Set<PricingSnapshot> findByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingSnapshotMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingSnapshot> findByExternalCatalogItemId(Integer externalCatalogItemId) {
        return pricingSnapshotMapper.findByExternalCatalogItemId(externalCatalogItemId);
    }

    public Optional<PricingSnapshot> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingSnapshotMapper.findLatestByMarketplaceListingId(marketplaceListingId);
    }

    public Optional<PricingSnapshot> findLatestByMarketplaceListingIdAndConditionAndCompleteness(
            Integer marketplaceListingId,
            String itemConditionCode,
            String completenessCode
    ) {
        return pricingSnapshotMapper.findLatestByMarketplaceListingIdAndConditionAndCompleteness(
                marketplaceListingId,
                itemConditionCode,
                completenessCode
        );
    }

    public Optional<PricingSnapshot> findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
            Integer externalCatalogItemId,
            String itemConditionCode,
            String completenessCode
    ) {
        return pricingSnapshotMapper.findLatestByExternalCatalogItemIdAndConditionAndCompleteness(
                externalCatalogItemId,
                itemConditionCode,
                completenessCode
        );
    }

    public PricingSnapshot insert(PricingSnapshot pricingSnapshot) {
        pricingSnapshotMapper.insert(pricingSnapshot);
        return findByPricingSnapshotId(pricingSnapshot.getPricingSnapshotId()).orElseThrow();
    }

    public PricingSnapshot update(PricingSnapshot pricingSnapshot) {
        pricingSnapshotMapper.update(pricingSnapshot);
        return findByPricingSnapshotId(pricingSnapshot.getPricingSnapshotId()).orElseThrow();
    }

    public void delete(Long pricingSnapshotId) {
        pricingSnapshotMapper.delete(pricingSnapshotId);
    }

    public PricingSnapshot upsert(PricingSnapshot pricingSnapshot) {
        pricingSnapshotMapper.upsert(pricingSnapshot);
        return findByPricingSnapshotId(pricingSnapshot.getPricingSnapshotId()).orElseThrow();
    }
}
