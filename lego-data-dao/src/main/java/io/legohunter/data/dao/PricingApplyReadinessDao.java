package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingApplyReadiness;
import io.legohunter.data.dto.PricingApplyReadinessReview;
import io.legohunter.data.mybatis.mapper.PricingApplyReadinessMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingApplyReadinessDao {
    private final PricingApplyReadinessMapper pricingApplyReadinessMapper;

    public Set<PricingApplyReadiness> findAll() {
        return pricingApplyReadinessMapper.findAll();
    }

    public Optional<PricingApplyReadiness> findByPricingApplyReadinessId(Long pricingApplyReadinessId) {
        return pricingApplyReadinessMapper.findByPricingApplyReadinessId(pricingApplyReadinessId);
    }

    public Optional<PricingApplyReadiness> findByPricingDecisionId(Long pricingDecisionId) {
        return pricingApplyReadinessMapper.findByPricingDecisionId(pricingDecisionId);
    }

    public Set<PricingApplyReadiness> findByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingApplyReadinessMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingApplyReadiness> findByReadinessStatusCode(String readinessStatusCode) {
        return pricingApplyReadinessMapper.findByReadinessStatusCode(readinessStatusCode);
    }

    public Set<PricingApplyReadiness> findByBlockReasonCode(String blockReasonCode) {
        return pricingApplyReadinessMapper.findByBlockReasonCode(blockReasonCode);
    }

    public Optional<PricingApplyReadiness> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingApplyReadinessMapper.findLatestByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingApplyReadinessReview> findLatestReviews(String readinessStatusCode, String blockReasonCode, int limit) {
        return pricingApplyReadinessMapper.findLatestReviews(readinessStatusCode, blockReasonCode, Math.max(1, limit));
    }

    public Set<PricingApplyReadinessReview> findLatestReadyToApplyReviews(int limit) {
        return pricingApplyReadinessMapper.findLatestReadyToApplyReviews(Math.max(1, limit));
    }

    public long countByReadinessStatusCode(String readinessStatusCode) {
        return pricingApplyReadinessMapper.countByReadinessStatusCode(readinessStatusCode);
    }

    public long countByBlockReasonCode(String blockReasonCode) {
        return pricingApplyReadinessMapper.countByBlockReasonCode(blockReasonCode);
    }

    public long countLatestByReadinessStatusCode(String readinessStatusCode) {
        return pricingApplyReadinessMapper.countLatestByReadinessStatusCode(readinessStatusCode);
    }

    public long countLatestByBlockReasonCode(String blockReasonCode) {
        return pricingApplyReadinessMapper.countLatestByBlockReasonCode(blockReasonCode);
    }

    public PricingApplyReadiness insert(PricingApplyReadiness pricingApplyReadiness) {
        pricingApplyReadinessMapper.insert(pricingApplyReadiness);
        return findByPricingApplyReadinessId(pricingApplyReadiness.getPricingApplyReadinessId()).orElseThrow();
    }

    public PricingApplyReadiness update(PricingApplyReadiness pricingApplyReadiness) {
        pricingApplyReadinessMapper.update(pricingApplyReadiness);
        return findByPricingApplyReadinessId(pricingApplyReadiness.getPricingApplyReadinessId()).orElseThrow();
    }

    public PricingApplyReadiness upsert(PricingApplyReadiness pricingApplyReadiness) {
        pricingApplyReadinessMapper.upsert(pricingApplyReadiness);
        if (pricingApplyReadiness.getPricingApplyReadinessId() != null) {
            return findByPricingApplyReadinessId(pricingApplyReadiness.getPricingApplyReadinessId()).orElseThrow();
        }
        return findByPricingDecisionId(pricingApplyReadiness.getPricingDecisionId()).orElseThrow();
    }

    public void delete(Long pricingApplyReadinessId) {
        pricingApplyReadinessMapper.delete(pricingApplyReadinessId);
    }
}
