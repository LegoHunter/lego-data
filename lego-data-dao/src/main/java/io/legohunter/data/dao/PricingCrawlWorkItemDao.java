package io.legohunter.data.dao;

import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.mybatis.mapper.PricingCrawlWorkItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PricingCrawlWorkItemDao {
    private final PricingCrawlWorkItemMapper pricingCrawlWorkItemMapper;

    public Set<PricingCrawlWorkItem> findAll() {
        return pricingCrawlWorkItemMapper.findAll();
    }

    public Optional<PricingCrawlWorkItem> findByPricingCrawlWorkItemId(Long pricingCrawlWorkItemId) {
        return pricingCrawlWorkItemMapper.findByPricingCrawlWorkItemId(pricingCrawlWorkItemId);
    }

    public Set<PricingCrawlWorkItem> findByMarketplaceListingId(Integer marketplaceListingId) {
        return pricingCrawlWorkItemMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<PricingCrawlWorkItem> findByWorkStatusCode(String workStatusCode) {
        return pricingCrawlWorkItemMapper.findByWorkStatusCode(workStatusCode);
    }

    public Set<PricingCrawlWorkItem> findDueByWorkStatusCode(String workStatusCode, ZonedDateTime dueAt, int limit) {
        return pricingCrawlWorkItemMapper.findDueByWorkStatusCode(workStatusCode, dueAt, limit);
    }

    public Set<PricingCrawlWorkItem> findClaimableByWorkStatusCode(String workStatusCode, ZonedDateTime dueAt, int limit) {
        return pricingCrawlWorkItemMapper.findClaimableByWorkStatusCode(workStatusCode, dueAt, limit);
    }

    @Transactional
    public Set<PricingCrawlWorkItem> claimDueWorkItems(
            String pendingStatusCode,
            String claimedStatusCode,
            ZonedDateTime dueAt,
            ZonedDateTime claimedAt,
            int limit
    ) {
        Set<PricingCrawlWorkItem> candidates = findClaimableByWorkStatusCode(pendingStatusCode, dueAt, limit);
        Set<PricingCrawlWorkItem> claimed = new LinkedHashSet<>();
        for (PricingCrawlWorkItem candidate : sortedByDueOrder(candidates)) {
            int updated = pricingCrawlWorkItemMapper.claim(
                    candidate.getPricingCrawlWorkItemId(),
                    pendingStatusCode,
                    claimedStatusCode,
                    dueAt,
                    claimedAt
            );
            if (updated == 1) {
                findByPricingCrawlWorkItemId(candidate.getPricingCrawlWorkItemId()).ifPresent(claimed::add);
            }
        }
        return claimed;
    }

    private Set<PricingCrawlWorkItem> sortedByDueOrder(Set<PricingCrawlWorkItem> candidates) {
        return candidates.stream()
                .sorted(Comparator
                        .comparing(PricingCrawlWorkItem::getNextAttemptAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(PricingCrawlWorkItem::getPricingCrawlWorkItemId, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }

    public int requeueStaleClaimed(
            String claimedStatusCode,
            String pendingStatusCode,
            ZonedDateTime claimedBefore,
            ZonedDateTime nextAttemptAt,
            String lastErrorMessage
    ) {
        return pricingCrawlWorkItemMapper.requeueStaleClaimed(
                claimedStatusCode,
                pendingStatusCode,
                claimedBefore,
                nextAttemptAt,
                lastErrorMessage
        );
    }

    public PricingCrawlWorkItem insert(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.insert(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }

    public PricingCrawlWorkItem update(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.update(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }

    public void delete(Long pricingCrawlWorkItemId) {
        pricingCrawlWorkItemMapper.delete(pricingCrawlWorkItemId);
    }

    public PricingCrawlWorkItem upsert(PricingCrawlWorkItem pricingCrawlWorkItem) {
        pricingCrawlWorkItemMapper.upsert(pricingCrawlWorkItem);
        return findByPricingCrawlWorkItemId(pricingCrawlWorkItem.getPricingCrawlWorkItemId()).orElseThrow();
    }
}
