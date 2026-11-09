package io.legohunter.data.dao;

import io.legohunter.data.dto.WantListItem;
import io.legohunter.data.mybatis.mapper.WantListItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

/** Shared marketplace pricing schema (HOMELAB-31): {@code marketplace_pricing.want_list_item}. */
@Component
@RequiredArgsConstructor
public class WantListItemDao {
    private final WantListItemMapper mapper;

    public List<WantListItem> findByEnvironmentCode(String environmentCode) {
        return mapper.findByEnvironmentCode(environmentCode);
    }

    public List<WantListItem> findByCanonicalItemId(Integer canonicalItemId) {
        return mapper.findByCanonicalItemId(canonicalItemId);
    }

    /** Adds the row, or refreshes its last confirmation time. */
    public void upsert(WantListItem wantListItem) {
        mapper.upsert(wantListItem);
    }

    public int delete(WantListItem wantListItem) {
        return mapper.delete(wantListItem);
    }

    /** Removes one environment's rows it has not confirmed since {@code cutoff}. */
    public int deleteByEnvironmentCodeAndLastConfirmedAtBefore(String environmentCode, ZonedDateTime cutoff) {
        return mapper.deleteByEnvironmentCodeAndLastConfirmedAtBefore(environmentCode, cutoff);
    }
}
