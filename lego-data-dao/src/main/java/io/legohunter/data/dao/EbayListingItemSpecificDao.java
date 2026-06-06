package io.legohunter.data.dao;

import io.legohunter.data.dto.EbayListingItemSpecific;
import io.legohunter.data.mybatis.mapper.EbayListingItemSpecificMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EbayListingItemSpecificDao {
    private final EbayListingItemSpecificMapper ebayListingItemSpecificMapper;

    public Set<EbayListingItemSpecific> findAll() {
        return ebayListingItemSpecificMapper.findAll();
    }

    public Optional<EbayListingItemSpecific> findByMarketplaceListingIdAndNameAndValue(Integer marketplaceListingId, String name, String value) {
        return ebayListingItemSpecificMapper.findByMarketplaceListingIdAndNameAndValue(marketplaceListingId, name, value);
    }

    public Set<EbayListingItemSpecific> findByMarketplaceListingId(Integer marketplaceListingId) {
        return ebayListingItemSpecificMapper.findByMarketplaceListingId(marketplaceListingId);
    }

    public Set<EbayListingItemSpecific> findByName(String name) {
        return ebayListingItemSpecificMapper.findByName(name);
    }

    public EbayListingItemSpecific insert(EbayListingItemSpecific ebayListingItemSpecific) {
        ebayListingItemSpecificMapper.insert(ebayListingItemSpecific);
        return findByMarketplaceListingIdAndNameAndValue(ebayListingItemSpecific.getMarketplaceListingId(), ebayListingItemSpecific.getName(), ebayListingItemSpecific.getValue()).orElseThrow();
    }

    public EbayListingItemSpecific update(EbayListingItemSpecific ebayListingItemSpecific) {
        ebayListingItemSpecificMapper.update(ebayListingItemSpecific);
        return findByMarketplaceListingIdAndNameAndValue(ebayListingItemSpecific.getMarketplaceListingId(), ebayListingItemSpecific.getName(), ebayListingItemSpecific.getValue()).orElseThrow();
    }

    public void delete(Integer marketplaceListingId, String name, String value) {
        ebayListingItemSpecificMapper.delete(marketplaceListingId, name, value);
    }

    public EbayListingItemSpecific upsert(EbayListingItemSpecific ebayListingItemSpecific) {
        ebayListingItemSpecificMapper.upsert(ebayListingItemSpecific);
        return findByMarketplaceListingIdAndNameAndValue(ebayListingItemSpecific.getMarketplaceListingId(), ebayListingItemSpecific.getName(), ebayListingItemSpecific.getValue()).orElseThrow();
    }
}
