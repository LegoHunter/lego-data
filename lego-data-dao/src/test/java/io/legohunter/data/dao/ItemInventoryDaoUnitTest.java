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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
}
