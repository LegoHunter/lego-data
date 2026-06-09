package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import io.legohunter.data.dto.EbayListingItemSpecific;
import io.legohunter.data.dto.EbayMarketplaceListing;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceListing;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class MarketplaceListingMapperTest extends MapperTestSupport {

    @Test
    void marketplaceListingSupportsCrudAndExternalListingLookup() {
        ListingFixture fixture = listingFixture("listing-1");
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "MARKETPLACE_LISTING"));
        externalServiceCapabilityMapper.insert(externalServiceCapability(2, "PRICE_GUIDE"));
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), 2, "BL-1");

        marketplaceListingMapper.insert(listing);
        listing.setTitle("Updated listing");
        listing.setUnitPrice(new BigDecimal("24.99"));
        marketplaceListingMapper.update(listing);

        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getTitle()).isEqualTo("Updated listing");
                    assertThat(found.getUnitPrice()).isEqualByComparingTo("24.99");
                    assertHydratedCatalogItem(found);
                });
        assertThat(marketplaceListingMapper.findByListingExternalServiceIdAndExternalListingId(2, "BL-1")).isPresent();
        assertThat(marketplaceListingMapper.findByItemInventoryId(fixture.inventory().getItemInventoryId()))
                .hasSize(1)
                .first()
                .satisfies(this::assertHydratedCatalogItem);
        assertThat(marketplaceListingMapper.findAll()).hasSize(1);

        listing.setListingStatusCode("ENDED");
        marketplaceListingMapper.upsert(listing);
        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getListingStatusCode()).isEqualTo("ENDED"));

        assertThat(marketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
        assertThat(marketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId())).isEmpty();
    }

    @Test
    void bricklinkMarketplaceListingSupportsCrudAndInventoryLookup() {
        MarketplaceListing listing = insertedMarketplaceListing("bricklink-listing", "BL-2", 2);
        BricklinkMarketplaceListing bricklink = bricklinkMarketplaceListing(listing.getMarketplaceListingId(), 2001);

        bricklinkMarketplaceListingMapper.insert(bricklink);
        bricklink.setColorName("Red");
        bricklinkMarketplaceListingMapper.update(bricklink);

        assertThat(bricklinkMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getColorName()).isEqualTo("Red"));
        assertThat(bricklinkMarketplaceListingMapper.findByBricklinkInventoryId(2001)).isPresent();
        assertThat(bricklinkMarketplaceListingMapper.findAll()).hasSize(1);

        bricklink.setLastRemoteQuantity(7);
        bricklinkMarketplaceListingMapper.upsert(bricklink);
        assertThat(bricklinkMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getLastRemoteQuantity()).isEqualTo(7));

        assertThat(bricklinkMarketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
    }

    @Test
    void ebayMarketplaceListingAndItemSpecificsSupportCrud() {
        MarketplaceListing listing = insertedMarketplaceListing("ebay-listing", "EBAY-1", 3);
        EbayMarketplaceListing ebay = ebayMarketplaceListing(listing.getMarketplaceListingId(), "ebay-item-1");

        ebayMarketplaceListingMapper.insert(ebay);
        ebay.setListingFormat("AUCTION");
        ebayMarketplaceListingMapper.update(ebay);

        assertThat(ebayMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getListingFormat()).isEqualTo("AUCTION"));
        assertThat(ebayMarketplaceListingMapper.findByEbayItemId("ebay-item-1")).isPresent();
        assertThat(ebayMarketplaceListingMapper.findAll()).hasSize(1);

        ebay.setDuration("DAYS_7");
        ebayMarketplaceListingMapper.upsert(ebay);
        assertThat(ebayMarketplaceListingMapper.findByMarketplaceListingId(listing.getMarketplaceListingId()))
                .hasValueSatisfying(found -> assertThat(found.getDuration()).isEqualTo("DAYS_7"));

        EbayListingItemSpecific specific = ebayListingItemSpecific(listing.getMarketplaceListingId(), "Brand", "LEGO");
        ebayListingItemSpecificMapper.insert(specific);
        assertThat(ebayListingItemSpecificMapper.findByMarketplaceListingIdAndNameAndValue(listing.getMarketplaceListingId(), "Brand", "LEGO")).isPresent();
        assertThat(ebayListingItemSpecificMapper.findByMarketplaceListingId(listing.getMarketplaceListingId())).hasSize(1);
        assertThat(ebayListingItemSpecificMapper.findByName("Brand")).hasSize(1);
        assertThat(ebayListingItemSpecificMapper.findAll()).hasSize(1);
        ebayListingItemSpecificMapper.upsert(specific);
        assertThat(ebayListingItemSpecificMapper.delete(listing.getMarketplaceListingId(), "Brand", "LEGO")).isOne();

        assertThat(ebayMarketplaceListingMapper.delete(listing.getMarketplaceListingId())).isOne();
    }

    private MarketplaceListing insertedMarketplaceListing(String itemKey, String externalListingId, Integer serviceId) {
        ListingFixture fixture = listingFixture(itemKey);
        MarketplaceListing listing = marketplaceListing(fixture.inventory().getItemInventoryId(), fixture.item().getExternalCatalogItemId(), serviceId, externalListingId);
        marketplaceListingMapper.insert(listing);
        return listing;
    }

    private ListingFixture listingFixture(String itemKey) {
        seedExternalCatalog();
        ExternalCatalogItem item = insertExternalCatalogItem(itemKey);
        ExternalCategory category = externalCategoryMapper.findByExternalServiceIdAndExternalCategoryKey(2, "5").orElseThrow();
        externalCatalogItemCategoryMapper.insert(externalCatalogItemCategory(item.getExternalCatalogItemId(), category.getExternalCategoryId()));
        ItemInventory inventory = insertItemInventory("uuid-" + itemKey);
        return new ListingFixture(item, inventory);
    }

    private void assertHydratedCatalogItem(MarketplaceListing listing) {
        assertThat(listing.getExternalCatalogItem()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalItemKey()).isEqualTo("listing-1");
        assertThat(listing.getExternalCatalogItem().getExternalService()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalService().getServiceCode()).isEqualTo("BRICKLINK");
        assertThat(listing.getExternalCatalogItem().getExternalService().getExternalServiceType()).isNotNull();
        assertThat(listing.getExternalCatalogItem().getExternalService().getExternalServiceType().getExternalServiceTypeName()).isEqualTo("MARKETPLACE");
        assertThat(listing.getExternalCatalogItem().getExternalService().getCapabilities())
                .extracting("capabilityCode")
                .containsExactlyInAnyOrder("MARKETPLACE_LISTING", "PRICE_GUIDE");
        assertThat(listing.getExternalCatalogItem().getCategories())
                .extracting(ExternalCategory::getExternalCategoryKey)
                .containsExactly("5");
    }

    private record ListingFixture(ExternalCatalogItem item, ItemInventory inventory) {
    }
}
