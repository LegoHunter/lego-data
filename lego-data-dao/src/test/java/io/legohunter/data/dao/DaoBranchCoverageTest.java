package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.MarketplaceOrder;
import io.legohunter.data.dto.MarketplaceOrderItem;
import io.legohunter.data.dto.PricingCrawlWorkItem;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.mybatis.mapper.ExternalCatalogItemMapper;
import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderItemMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderMapper;
import io.legohunter.data.mybatis.mapper.PricingCrawlWorkItemMapper;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DaoBranchCoverageTest {

    @Test
    void imageHostingSyncCandidateNormalizesNullEmptyAndNonEmptyReasons() {
        assertThat(new ImageHostingSyncCandidate(1, null).reasons()).isEmpty();
        assertThat(new ImageHostingSyncCandidate(1, Set.of()).reasons()).isEmpty();

        ImageHostingSyncCandidate candidate = new ImageHostingSyncCandidate(
                1,
                Set.of(ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK)
        );

        assertThat(candidate.reasons()).containsExactly(ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK);
    }

    @Test
    void externalImageDaoCoversNullCandidateAndQueryDelegations() {
        ExternalImageMapper mapper = mock(ExternalImageMapper.class);
        ExternalImageDao dao = new ExternalImageDao(mapper);
        ExternalImage image = ExternalImage.builder()
                .externalImageId(1L)
                .externalServiceId(2)
                .itemInventoryPhotoId(3)
                .externalServiceImageId("remote-image")
                .syncStatus(ExternalSyncStatus.SYNCED)
                .md5AtUpload("md5")
                .metadataHashAtSync("meta")
                .build();

        when(mapper.findAll()).thenReturn(Set.of(image));
        when(mapper.findByExternalImageId(1L)).thenReturn(Optional.of(image));
        when(mapper.findByExternalServiceIdAndItemInventoryPhotoId(2, 3)).thenReturn(Optional.of(image));
        when(mapper.findByExternalServiceIdAndExternalServiceImageId(2, "remote-image")).thenReturn(Optional.of(image));
        when(mapper.findByItemInventoryPhotoId(3)).thenReturn(Set.of(image));
        when(mapper.findBySyncStatus(ExternalSyncStatus.SYNCED)).thenReturn(Set.of(image));
        when(mapper.update(image)).thenReturn(1);
        when(mapper.deleteByExternalImageId(1L)).thenReturn(1);
        when(mapper.deleteByExternalServiceIdAndItemInventoryPhotoId(2, 3)).thenReturn(1);
        when(mapper.findItemInventoryIdsMissingAlbumLinks(2, 1)).thenReturn(Arrays.asList((Integer) null));
        when(mapper.findItemInventoryIdsMissingExternalImageLinks(2, 1)).thenReturn(List.of(10));
        when(mapper.findItemInventoryIdsMissingAlbumMemberships(2, 1)).thenReturn(List.of(10));
        when(mapper.findItemInventoryIdsWithPendingSyncRows(2, 1)).thenReturn(List.of(10));
        when(mapper.findItemInventoryIdsWithMetadataDrift(2, 1)).thenReturn(List.of(11));

        assertThat(dao.findAll()).containsExactly(image);
        assertThat(dao.findByExternalImageId(1L)).contains(image);
        assertThat(dao.findByExternalServiceIdAndItemInventoryPhotoId(2, 3)).contains(image);
        assertThat(dao.findByExternalServiceIdAndExternalServiceImageId(2, "remote-image")).contains(image);
        assertThat(dao.findByItemInventoryPhotoId(3)).containsExactly(image);
        assertThat(dao.findBySyncStatus(ExternalSyncStatus.SYNCED)).containsExactly(image);
        assertThat(dao.findItemInventoryIdsNeedingSync(2, false, 0)).containsExactly(10);
        assertThat(dao.findItemInventorySyncCandidates(2, false, 1))
                .singleElement()
                .satisfies(candidate -> assertThat(candidate.reasons()).contains(
                        ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK,
                        ImageHostingSyncCandidateReason.MISSING_ALBUM_MEMBERSHIP,
                        ImageHostingSyncCandidateReason.PENDING_SYNC
                ));
        assertThat(dao.insert(image)).isSameAs(image);
        assertThat(dao.update(image)).isOne();
        assertThat(dao.upsert(image)).isSameAs(image);
        assertThat(dao.deleteByExternalImageId(1L)).isOne();
        assertThat(dao.deleteByExternalServiceIdAndItemInventoryPhotoId(2, 3)).isOne();
        assertThat(dao.hasUploadedMd5(2, 3, "md5")).isTrue();
        assertThat(dao.hasSyncedMetadataHash(2, 3, "meta")).isTrue();

        verify(mapper, never()).findItemInventoryIdsWithFailedSyncRows(2, 1);
    }

    @Test
    void marketplaceOrderDaoHandlesBlankStatusesLowLimitAndDuplicateCandidates() {
        MarketplaceOrderMapper mapper = mock(MarketplaceOrderMapper.class);
        MarketplaceOrderDao dao = new MarketplaceOrderDao(mapper);
        MarketplaceOrder first = MarketplaceOrder.builder().marketplaceOrderId(1).build();
        MarketplaceOrder duplicate = MarketplaceOrder.builder()
                .marketplaceOrderId(1)
                .externalOrderId("duplicate-row")
                .build();
        Set<MarketplaceOrder> duplicateRows = new LinkedHashSet<>();
        duplicateRows.add(first);
        duplicateRows.add(duplicate);

        when(mapper.findFulfillmentCandidatesByStatus("BRICKLINK", "PAID", 1))
                .thenReturn(duplicateRows);

        assertThat(dao.findFulfillmentCandidates("BRICKLINK", Arrays.asList(null, " ", " PAID ", "PACKED"), 0))
                .containsExactly(first);

        verify(mapper).findFulfillmentCandidatesByStatus("BRICKLINK", "PAID", 1);
        verify(mapper, never()).findFulfillmentCandidatesByStatus("BRICKLINK", "PACKED", 0);
    }

    @Test
    void pricingCrawlWorkItemDaoSkipsCandidatesThatCannotBeClaimed() {
        PricingCrawlWorkItemMapper mapper = mock(PricingCrawlWorkItemMapper.class);
        PricingCrawlWorkItemDao dao = new PricingCrawlWorkItemDao(mapper);
        ZonedDateTime dueAt = ZonedDateTime.parse("2026-06-24T10:00:00Z");
        PricingCrawlWorkItem candidate = PricingCrawlWorkItem.builder()
                .pricingCrawlWorkItemId(1L)
                .nextAttemptAt(dueAt)
                .build();

        when(mapper.findClaimableByWorkStatusCode("PENDING", dueAt, 1)).thenReturn(Set.of(candidate));
        when(mapper.claim(1L, "PENDING", "CLAIMED", dueAt, dueAt)).thenReturn(0);

        assertThat(dao.claimDueWorkItems("PENDING", "CLAIMED", dueAt, dueAt, 1)).isEmpty();
        verify(mapper, never()).findByPricingCrawlWorkItemId(1L);
    }

    @Test
    void itemInventoryDaoCoversUpdateAndUpsertFallbackBranches() {
        ItemInventoryMapper mapper = mock(ItemInventoryMapper.class);
        MarketplaceListingDao marketplaceListingDao = mock(MarketplaceListingDao.class);
        ItemInventoryDao dao = new ItemInventoryDao(mapper, marketplaceListingDao);
        ItemInventory existing = new ItemInventory();
        existing.setItemInventoryId(1);
        existing.setInventoryStateCode("AVAILABLE");
        existing.setSaleIntentCode("SELL");
        existing.setSaleIntentNote("old");
        ItemInventory updated = new ItemInventory();
        updated.setItemInventoryId(1);
        updated.setInventoryStateCode("SOLD");
        updated.setSaleIntentCode("KEEP");
        updated.setSaleIntentNote("new");
        ItemInventory byUuid = new ItemInventory();
        byUuid.setUuid("uuid-1");

        when(mapper.findByItemInventoryId(1))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated));
        when(mapper.findByUuid("uuid-1")).thenReturn(Optional.of(byUuid));

        assertThat(dao.updateInventoryState(1, "SOLD", null)).isSameAs(updated);
        assertThat(dao.updateSaleIntent(1, "KEEP", null, "new")).isSameAs(updated);
        assertThat(dao.upsert(byUuid)).isSameAs(byUuid);

        verify(mapper).upsert(byUuid);
    }

    @Test
    void externalCatalogItemDaoUpsertFallsBackToUniqueKeyWhenItemKeyIsMissing() {
        ExternalCatalogItemMapper mapper = mock(ExternalCatalogItemMapper.class);
        ExternalCatalogItemDao dao = new ExternalCatalogItemDao(mapper);
        ExternalCatalogItem catalogItem = ExternalCatalogItem.builder()
                .externalServiceId(1)
                .externalItemKey("missing")
                .externalUniqueKey("4997")
                .build();
        ExternalCatalogItem hydrated = ExternalCatalogItem.builder()
                .externalCatalogItemId(10)
                .externalServiceId(1)
                .externalUniqueKey("4997")
                .build();

        when(mapper.findByExternalServiceIdAndExternalItemKey(1, "missing")).thenReturn(Optional.empty());
        when(mapper.findByExternalServiceIdAndExternalUniqueKey(1, "4997")).thenReturn(Optional.of(hydrated));

        assertThat(dao.upsert(catalogItem)).isSameAs(hydrated);
        verify(mapper).upsert(catalogItem);
    }

    @Test
    void marketplaceOrderItemDaoDelegatesDeleteByMarketplaceOrderId() {
        MarketplaceOrderItemMapper mapper = mock(MarketplaceOrderItemMapper.class);
        MarketplaceOrderItemDao dao = new MarketplaceOrderItemDao(mapper);
        when(mapper.deleteByMarketplaceOrderId(1)).thenReturn(2);

        assertThat(dao.deleteByMarketplaceOrderId(1)).isEqualTo(2);
    }
}
