package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.PricingDecisionReview;
import io.legohunter.data.mybatis.mapper.PricingDecisionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingDecisionDao {
    private final PricingDecisionMapper pricingDecisionMapper;

    public Set<PricingDecision> findAll() {
        return pricingDecisionMapper.findAll();
    }

    public Optional<PricingDecision> findByPricingDecisionId(Long pricingDecisionId) {
        return pricingDecisionMapper.findByPricingDecisionId(pricingDecisionId);
    }

    public Set<PricingDecision> findByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingDecisionMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingDecision> findByDecisionStatusCode(String decisionStatusCode) {
        return pricingDecisionMapper.findByDecisionStatusCode(decisionStatusCode);
    }

    public Set<PricingDecision> findByReasonCode(String reasonCode) {
        return pricingDecisionMapper.findByReasonCode(reasonCode);
    }

    public Optional<PricingDecision> findLatestByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingDecisionMapper.findLatestByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingDecisionReview> findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            int limit
    ) {
        return pricingDecisionMapper.findLatestReviewsByListingExternalServiceIdAndListingStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                limit
        );
    }

    public Set<PricingDecisionReview> findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
            Integer listingExternalServiceId,
            String listingStatusCode,
            String decisionStatusCode,
            int limit
    ) {
        return pricingDecisionMapper.findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
                listingExternalServiceId,
                listingStatusCode,
                decisionStatusCode,
                limit
        );
    }

    public PricingDecision insert(PricingDecision pricingDecision) {
        pricingDecisionMapper.insert(pricingDecision);
        return findByPricingDecisionId(pricingDecision.getPricingDecisionId()).orElseThrow();
    }

    public PricingDecision update(PricingDecision pricingDecision) {
        pricingDecisionMapper.update(pricingDecision);
        return findByPricingDecisionId(pricingDecision.getPricingDecisionId()).orElseThrow();
    }

    public void delete(Long pricingDecisionId) {
        pricingDecisionMapper.delete(pricingDecisionId);
    }

    public PricingDecision upsert(PricingDecision pricingDecision) {
        pricingDecisionMapper.upsert(pricingDecision);
        return findByPricingDecisionId(pricingDecision.getPricingDecisionId()).orElseThrow();
    }
}
