package io.legohunter.data.dao;

import io.legohunter.data.bricklink.BricklinkLotText;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.MarketplaceListing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Reads what {@link BricklinkLotText} needs for one listing (HOMELAB-82): the item's photo album link
 * (Flickr), and the number and the captions of its processed photos. Shared by lego-data-service and lego-data-ingress so
 * both compose the lot text from the same data.
 */
@Component
@RequiredArgsConstructor
public class BricklinkLotTextSource {
    private final ExternalImageAlbumDao externalImageAlbumDao;
    private final ItemInventoryPhotoDao itemInventoryPhotoDao;

    /**
     * The lot Description this listing should show now.
     *
     * @param extendedDescriptionSync whether this environment writes the Extended Description
     */
    public BricklinkLotText.Description description(MarketplaceListing listing, boolean extendedDescriptionSync) {
        if (listing == null) {
            return BricklinkLotText.Description.NONE;
        }
        String shortText = BricklinkLotText.shortText(listing.getDescription(), listing.getTitle());
        if (shortText == null || listing.getItemInventoryId() == null) {
            return BricklinkLotText.description(shortText, null, 0, false, extendedDescriptionSync);
        }
        String albumLink = albumLink(listing.getItemInventoryId());
        return BricklinkLotText.description(
                shortText,
                albumLink,
                albumLink == null ? 0 : photoCount(listing.getItemInventoryId()),
                !captions(listing.getItemInventoryId()).isEmpty(),
                extendedDescriptionSync);
    }

    /** The lot Extended Description this listing's item calls for now; empty when no photo has a caption. */
    public String extendedDescription(Integer itemInventoryId) {
        return BricklinkLotText.extendedDescription(itemInventoryId == null ? List.of() : captions(itemInventoryId));
    }

    /** The item's album address (short link first), or null when it has no album address yet. */
    public String albumLink(Integer itemInventoryId) {
        return externalImageAlbumDao
                .findByExternalServiceIdAndItemInventoryId(ExternalService.Service.FLICKR.getExternalServiceId(), itemInventoryId)
                .map(album -> BricklinkLotText.albumLink(album.getShortUrl(), album.getAlbumUrl()))
                .orElse(null);
    }

    /** How many processed photos the item has: the number the Description names beside the album address. */
    public int photoCount(Integer itemInventoryId) {
        return itemInventoryId == null ? 0 : itemInventoryPhotoDao.countProcessedByItemInventoryId(itemInventoryId);
    }

    private List<String> captions(Integer itemInventoryId) {
        return itemInventoryPhotoDao.findProcessedCaptionsByItemInventoryId(itemInventoryId);
    }
}
