package io.legohunter.data.dao;

import io.legohunter.data.dto.LegoWoodenToyCatalogItem;
import io.legohunter.data.dto.LegoWoodenToyCatalogItemImage;
import io.legohunter.data.mybatis.mapper.LegoWoodenToyCatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * LEGO's wooden toys catalog: the detail row of a catalog entry and its pictures (HOMELAB-157).
 *
 * <p>The entry itself is an {@code external_catalog_item} of the LEGO service with item type
 * {@link LegoWoodenToyCatalogItem#ITEM_TYPE_CODE}; write that row first with {@link ExternalCatalogItemDao}.
 */
@Component
@RequiredArgsConstructor
public class LegoWoodenToyCatalogDao {
    private final LegoWoodenToyCatalogMapper legoWoodenToyCatalogMapper;

    /** Inserts the detail row of a catalog entry, or replaces its fields when the entry already has one. */
    public LegoWoodenToyCatalogItem upsertItem(LegoWoodenToyCatalogItem item) {
        legoWoodenToyCatalogMapper.upsertItem(item);
        return legoWoodenToyCatalogMapper.findItem(item.getExternalCatalogItemId()).orElse(item);
    }

    public Optional<LegoWoodenToyCatalogItem> findItem(Integer externalCatalogItemId) {
        return legoWoodenToyCatalogMapper.findItem(externalCatalogItemId);
    }

    /** Every entry that carries the Product Number. More than one is normal: LEGO reused numbers. */
    public List<LegoWoodenToyCatalogItem> findItemsByProductNumber(String productNumber) {
        return legoWoodenToyCatalogMapper.findItemsByProductNumber(productNumber);
    }

    public int countItemsBySource(String entrySource) {
        return legoWoodenToyCatalogMapper.countItemsBySource(entrySource);
    }

    /** Stores a picture with its bytes and sets its generated id. The same bytes cannot be stored twice for one entry. */
    public LegoWoodenToyCatalogItemImage insertImage(LegoWoodenToyCatalogItemImage image) {
        legoWoodenToyCatalogMapper.insertImage(image);
        return image;
    }

    /** An entry's pictures in order, without their bytes. */
    public List<LegoWoodenToyCatalogItemImage> findImages(Integer externalCatalogItemId) {
        return legoWoodenToyCatalogMapper.findImagesByItem(externalCatalogItemId);
    }

    /** The entry's picture with these bytes, without the bytes. Empty when the entry does not hold it yet. */
    public Optional<LegoWoodenToyCatalogItemImage> findImage(Integer externalCatalogItemId, String sha256) {
        return legoWoodenToyCatalogMapper.findImageByItemAndSha256(externalCatalogItemId, sha256);
    }

    /** One picture with its bytes. */
    public Optional<LegoWoodenToyCatalogItemImage> findImageWithBytes(Long imageId) {
        return legoWoodenToyCatalogMapper.findImageWithBytes(imageId);
    }

    /**
     * Removes one picture, but only if it has the given source: a capture passes
     * {@link LegoWoodenToyCatalogItemImage#SOURCE_LEGO_COM} and so can never remove a picture the owner added.
     * True when the picture was removed.
     */
    public boolean deleteImage(Long imageId, String imageSource) {
        return legoWoodenToyCatalogMapper.deleteImage(imageId, imageSource) == 1;
    }
}
