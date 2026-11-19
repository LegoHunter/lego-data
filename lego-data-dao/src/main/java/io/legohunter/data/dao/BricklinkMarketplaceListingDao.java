package io.legohunter.data.dao;

import io.legohunter.data.dto.BricklinkListingCaption;
import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.mybatis.mapper.BricklinkMarketplaceListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
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

    /** Records the photo now stored as the lot's custom image, or clears the record with nulls. */
    public void updateLotImage(Integer marketplaceListingId, String lotImageMd5, ZonedDateTime lotImageSyncedAt) {
        bricklinkMarketplaceListingMapper.updateLotImage(marketplaceListingId, lotImageMd5, lotImageSyncedAt);
    }

    /** See {@link BricklinkMarketplaceListingMapper#findMarketplaceListingIdsNeedingLotImage}. */
    public List<Integer> findMarketplaceListingIdsNeedingLotImage(Integer listingExternalServiceId, String environmentCode,
                                                                  String syncRequestTypeCode, ZonedDateTime retryNotBefore,
                                                                  int limit) {
        return bricklinkMarketplaceListingMapper.findMarketplaceListingIdsNeedingLotImage(
                listingExternalServiceId, environmentCode, syncRequestTypeCode, retryNotBefore, limit);
    }

    /** Records the Extended Description now stored on the lot by its hash, or clears the record with nulls. */
    public void updateExtendedDescription(Integer marketplaceListingId, String extendedDescriptionHash,
                                          ZonedDateTime extendedDescriptionSyncedAt) {
        bricklinkMarketplaceListingMapper.updateExtendedDescription(marketplaceListingId, extendedDescriptionHash, extendedDescriptionSyncedAt);
    }

    /** See {@link BricklinkMarketplaceListingMapper#findListingCaptionsForExtendedDescription}. */
    public List<BricklinkListingCaption> findListingCaptionsForExtendedDescription(Integer listingExternalServiceId,
                                                                                  String environmentCode,
                                                                                  String syncRequestTypeCode,
                                                                                  ZonedDateTime retryNotBefore) {
        return bricklinkMarketplaceListingMapper.findListingCaptionsForExtendedDescription(
                listingExternalServiceId, environmentCode, syncRequestTypeCode, retryNotBefore);
    }

    public void delete(Integer marketplaceListingId) {
        bricklinkMarketplaceListingMapper.delete(marketplaceListingId);
    }

    public BricklinkMarketplaceListing upsert(BricklinkMarketplaceListing bricklinkMarketplaceListing) {
        bricklinkMarketplaceListingMapper.upsert(bricklinkMarketplaceListing);
        return findByMarketplaceListingId(bricklinkMarketplaceListing.getMarketplaceListingId()).orElseThrow();
    }
}
