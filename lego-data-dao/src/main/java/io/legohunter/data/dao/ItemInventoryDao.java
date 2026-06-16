package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ItemInventoryDao {
    private final ItemInventoryMapper itemInventoryMapper;
    private final MarketplaceListingDao marketplaceListingDao;

    public Set<ItemInventory> findAll() {
        return itemInventoryMapper.findAll();
    }

    public Optional<ItemInventory> findByItemInventoryId(Integer itemInventoryId) {
        return itemInventoryMapper.findByItemInventoryId(itemInventoryId);
    }

    public Optional<ItemInventory> findByUuid(String uuid) {
        return itemInventoryMapper.findByUuid(uuid);
    }

    public Optional<ExternalCatalogItem> getPrimaryExternalCatalogItem(ItemInventory itemInventory) {
        if (itemInventory == null || itemInventory.getExternalCatalogItems() == null) {
            return Optional.empty();
        }

        return itemInventory.getExternalCatalogItems().stream()
                .filter(externalCatalogItem -> Boolean.TRUE.equals(externalCatalogItem.getPrimary()))
                .map(ItemInventoryExternalCatalogItem::getExternalCatalogItem)
                .filter(Objects::nonNull)
                .findFirst();
    }

    public Set<MarketplaceListing> findMarketplaceListings(ItemInventory itemInventory) {
        if (itemInventory == null || itemInventory.getItemInventoryId() == null) {
            return Set.of();
        }

        return marketplaceListingDao.findByItemInventoryId(itemInventory.getItemInventoryId());
    }

    public ItemInventory insert(ItemInventory itemInventory) {
        itemInventoryMapper.insert(itemInventory);
        return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
    }

    public ItemInventory update(ItemInventory itemInventory) {
        itemInventoryMapper.update(itemInventory);
        return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
    }

    public ItemInventory updateInventoryState(Integer itemInventoryId, String inventoryStateCode, ZonedDateTime inventoryStateChangedAt) {
        ItemInventory existing = findByItemInventoryId(itemInventoryId).orElseThrow();
        if (Objects.equals(existing.getInventoryStateCode(), inventoryStateCode)) {
            return existing;
        }

        itemInventoryMapper.updateInventoryState(
                itemInventoryId,
                inventoryStateCode,
                Objects.requireNonNullElseGet(inventoryStateChangedAt, () -> ZonedDateTime.now(ZoneOffset.UTC))
        );
        return findByItemInventoryId(itemInventoryId).orElseThrow();
    }

    public ItemInventory updateSaleIntent(Integer itemInventoryId, String saleIntentCode, ZonedDateTime saleIntentUpdatedAt, String saleIntentNote) {
        ItemInventory existing = findByItemInventoryId(itemInventoryId).orElseThrow();
        if (Objects.equals(existing.getSaleIntentCode(), saleIntentCode)
                && Objects.equals(existing.getSaleIntentNote(), saleIntentNote)) {
            return existing;
        }

        itemInventoryMapper.updateSaleIntent(
                itemInventoryId,
                saleIntentCode,
                Objects.requireNonNullElseGet(saleIntentUpdatedAt, () -> ZonedDateTime.now(ZoneOffset.UTC)),
                saleIntentNote
        );
        return findByItemInventoryId(itemInventoryId).orElseThrow();
    }

    public void delete(Integer itemInventoryId) {
        itemInventoryMapper.delete(itemInventoryId);
    }

    public ItemInventory upsert(ItemInventory itemInventory) {
        itemInventoryMapper.upsert(itemInventory);
        if (itemInventory.getItemInventoryId() != null) {
            return findByItemInventoryId(itemInventory.getItemInventoryId()).orElseThrow();
        }
        return findByUuid(itemInventory.getUuid()).orElseThrow();
    }
}
