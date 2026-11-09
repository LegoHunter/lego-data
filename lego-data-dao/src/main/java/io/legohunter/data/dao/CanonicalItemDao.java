package io.legohunter.data.dao;

import io.legohunter.data.dto.CanonicalItem;
import io.legohunter.data.mybatis.mapper.CanonicalItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.canonical_item}. */
@Component
@RequiredArgsConstructor
public class CanonicalItemDao {
    private final CanonicalItemMapper mapper;

    public Optional<CanonicalItem> findByCanonicalItemId(Integer canonicalItemId) {
        return mapper.findByCanonicalItemId(canonicalItemId);
    }

    public Optional<CanonicalItem> findByItemTypeCodeAndItemNumber(String itemTypeCode, String itemNumber) {
        return mapper.findByItemTypeCodeAndItemNumber(itemTypeCode, itemNumber);
    }

    public CanonicalItem insert(CanonicalItem canonicalItem) {
        mapper.insert(canonicalItem);
        return findByCanonicalItemId(canonicalItem.getCanonicalItemId()).orElseThrow();
    }

    public CanonicalItem update(CanonicalItem canonicalItem) {
        mapper.update(canonicalItem);
        return findByCanonicalItemId(canonicalItem.getCanonicalItemId()).orElseThrow();
    }
}
