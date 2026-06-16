package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryExternalCatalogItem;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemInventoryDaoUnitTest {
    @Mock
    private ItemInventoryMapper itemInventoryMapper;

    @Mock
    private MarketplaceListingDao marketplaceListingDao;

    private ItemInventoryDao itemInventoryDao;

    @BeforeEach
    void setUp() {
        itemInventoryDao = new ItemInventoryDao(itemInventoryMapper, marketplaceListingDao);
    }

    @Test
    void getPrimaryExternalCatalogItemReturnsEmptyForMissingInventoryOrPrimaryLink() {
        assertThat(itemInventoryDao.getPrimaryExternalCatalogItem(null)).isEmpty();
        assertThat(itemInventoryDao.getPrimaryExternalCatalogItem(new ItemInventory())).isEmpty();

        ItemInventory inventory = new ItemInventory();
        inventory.setExternalCatalogItems(Set.of(ItemInventoryExternalCatalogItem.builder()
                .primary(false)
                .externalCatalogItem(new ExternalCatalogItem())
                .build()));

        assertThat(itemInventoryDao.getPrimaryExternalCatalogItem(inventory)).isEmpty();
    }

    @Test
    void getPrimaryExternalCatalogItemReturnsHydratedPrimaryCatalogItem() {
        ExternalCatalogItem catalogItem = ExternalCatalogItem.builder()
                .externalCatalogItemId(101)
                .externalItemKey("3001-1")
                .build();
        ItemInventory inventory = new ItemInventory();
        inventory.setExternalCatalogItems(Set.of(
                ItemInventoryExternalCatalogItem.builder()
                        .primary(false)
                        .externalCatalogItem(new ExternalCatalogItem())
                        .build(),
                ItemInventoryExternalCatalogItem.builder()
                        .primary(true)
                        .externalCatalogItem(catalogItem)
                        .build()
        ));

        assertThat(itemInventoryDao.getPrimaryExternalCatalogItem(inventory)).contains(catalogItem);
    }

    @Test
    void findMarketplaceListingsReturnsEmptyForMissingInventoryId() {
        assertThat(itemInventoryDao.findMarketplaceListings(null)).isEmpty();
        assertThat(itemInventoryDao.findMarketplaceListings(new ItemInventory())).isEmpty();

        verify(marketplaceListingDao, never()).findByItemInventoryId(null);
    }

    @Test
    void findMarketplaceListingsDelegatesToMarketplaceListingDao() {
        ItemInventory inventory = new ItemInventory();
        inventory.setItemInventoryId(200);
        MarketplaceListing marketplaceListing = MarketplaceListing.builder()
                .marketplaceListingId(300)
                .itemInventoryId(200)
                .build();
        when(marketplaceListingDao.findByItemInventoryId(200)).thenReturn(Set.of(marketplaceListing));

        assertThat(itemInventoryDao.findMarketplaceListings(inventory)).containsExactly(marketplaceListing);

        verify(marketplaceListingDao).findByItemInventoryId(200);
    }

    @Test
    void updateInventoryStateSkipsMapperUpdateWhenStateIsUnchanged() {
        ItemInventory inventory = new ItemInventory();
        inventory.setItemInventoryId(200);
        inventory.setInventoryStateCode("AVAILABLE");
        when(itemInventoryMapper.findByItemInventoryId(200)).thenReturn(Optional.of(inventory));

        assertThat(itemInventoryDao.updateInventoryState(200, "AVAILABLE", ZonedDateTime.parse("2026-06-16T10:00:00Z")))
                .isSameAs(inventory);

        verify(itemInventoryMapper, never()).updateInventoryState(any(), any(), any());
    }

    @Test
    void updateInventoryStateUpdatesMapperWhenStateChanges() {
        ItemInventory existing = new ItemInventory();
        existing.setItemInventoryId(200);
        existing.setInventoryStateCode("AVAILABLE");
        ItemInventory updated = new ItemInventory();
        updated.setItemInventoryId(200);
        updated.setInventoryStateCode("RESERVED_FOR_ORDER");
        ZonedDateTime changedAt = ZonedDateTime.parse("2026-06-16T10:00:00Z");
        when(itemInventoryMapper.findByItemInventoryId(200))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated));

        assertThat(itemInventoryDao.updateInventoryState(200, "RESERVED_FOR_ORDER", changedAt))
                .isSameAs(updated);

        verify(itemInventoryMapper).updateInventoryState(200, "RESERVED_FOR_ORDER", changedAt);
    }

    @Test
    void updateSaleIntentSkipsMapperUpdateWhenIntentAndNoteAreUnchanged() {
        ItemInventory inventory = new ItemInventory();
        inventory.setItemInventoryId(200);
        inventory.setSaleIntentCode("KEEP");
        inventory.setSaleIntentNote("Collection hold");
        when(itemInventoryMapper.findByItemInventoryId(200)).thenReturn(Optional.of(inventory));

        assertThat(itemInventoryDao.updateSaleIntent(200, "KEEP", ZonedDateTime.parse("2026-06-16T10:00:00Z"), "Collection hold"))
                .isSameAs(inventory);

        verify(itemInventoryMapper, never()).updateSaleIntent(any(), any(), any(), any());
    }

    @Test
    void updateSaleIntentUpdatesMapperWhenIntentChanges() {
        ItemInventory existing = new ItemInventory();
        existing.setItemInventoryId(200);
        existing.setSaleIntentCode("UNDECIDED");
        ItemInventory updated = new ItemInventory();
        updated.setItemInventoryId(200);
        updated.setSaleIntentCode("KEEP");
        updated.setSaleIntentNote("Collection hold");
        ZonedDateTime updatedAt = ZonedDateTime.parse("2026-06-16T11:00:00Z");
        when(itemInventoryMapper.findByItemInventoryId(200))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated));

        assertThat(itemInventoryDao.updateSaleIntent(200, "KEEP", updatedAt, "Collection hold"))
                .isSameAs(updated);

        verify(itemInventoryMapper).updateSaleIntent(200, "KEEP", updatedAt, "Collection hold");
    }

    @Test
    void updateSaleIntentSuppliesTimestampWhenIntentChangesWithoutOne() {
        ItemInventory existing = new ItemInventory();
        existing.setItemInventoryId(200);
        existing.setSaleIntentCode("UNDECIDED");
        ItemInventory updated = new ItemInventory();
        updated.setItemInventoryId(200);
        updated.setSaleIntentCode("KEEP");
        when(itemInventoryMapper.findByItemInventoryId(200))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated));

        itemInventoryDao.updateSaleIntent(200, "KEEP", null, null);

        verify(itemInventoryMapper).updateSaleIntent(eq(200), eq("KEEP"), any(ZonedDateTime.class), eq(null));
    }
}
