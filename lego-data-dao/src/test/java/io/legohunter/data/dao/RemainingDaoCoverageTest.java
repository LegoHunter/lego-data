package io.legohunter.data.dao;

import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.MarketplaceOrderItem;
import io.legohunter.data.enums.PhotoStatus;
import io.legohunter.data.mybatis.mapper.ItemInventoryMapper;
import io.legohunter.data.mybatis.mapper.ItemInventoryPhotoMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceOrderItemMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RemainingDaoCoverageTest {

    @Test
    void marketplaceListingDaoCoversNonSnapshotCandidateAndExternalListingLookupBranches() {
        MarketplaceListingMapper mapper = mock(MarketplaceListingMapper.class);
        MarketplaceListingDao dao = new MarketplaceListingDao(mapper);
        MarketplaceListing listing = MarketplaceListing.builder()
                .marketplaceListingId(100)
                .listingExternalServiceId(1)
                .externalListingId("remote-1")
                .build();

        when(mapper.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 5))
                .thenReturn(Set.of(listing));
        when(mapper.findByListingExternalServiceIdAndExternalListingId(1, "remote-1"))
                .thenReturn(Optional.of(listing));

        assertThat(dao.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 5, false))
                .containsExactly(listing);
        assertThat(dao.findByListingExternalServiceIdAndExternalListingId(1, "remote-1")).contains(listing);
    }

    @Test
    void itemInventoryDaoCoversSaleIntentNoteOnlyChangeBranch() {
        ItemInventoryMapper mapper = mock(ItemInventoryMapper.class);
        MarketplaceListingDao marketplaceListingDao = mock(MarketplaceListingDao.class);
        ItemInventoryDao dao = new ItemInventoryDao(mapper, marketplaceListingDao);
        ItemInventory existing = new ItemInventory();
        existing.setItemInventoryId(1);
        existing.setSaleIntentCode("SELL");
        existing.setSaleIntentNote("old");
        ItemInventory updated = new ItemInventory();
        updated.setItemInventoryId(1);
        updated.setSaleIntentCode("SELL");
        updated.setSaleIntentNote("new");

        when(mapper.findByItemInventoryId(1))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(updated));

        assertThat(dao.updateSaleIntent(1, "SELL", null, "new")).isSameAs(updated);
    }

    @Test
    void marketplaceOrderItemDaoCoversUpsertReturnValue() {
        MarketplaceOrderItemMapper mapper = mock(MarketplaceOrderItemMapper.class);
        MarketplaceOrderItemDao dao = new MarketplaceOrderItemDao(mapper);
        MarketplaceOrderItem item = MarketplaceOrderItem.builder()
                .marketplaceOrderItemId(1)
                .build();
        when(mapper.delete(1)).thenReturn(1);

        assertThat(dao.delete(1)).isOne();
        assertThat(dao.upsert(item)).isSameAs(item);
        verify(mapper).upsert(item);
    }

    @Test
    void itemInventoryPhotoDaoCoversWrapperMethodsAndTransitionFailures() {
        ItemInventoryPhotoMapper mapper = mock(ItemInventoryPhotoMapper.class);
        ItemInventoryPhotoDao dao = new ItemInventoryPhotoDao(mapper);
        ItemInventoryPhoto photo = ItemInventoryPhoto.builder()
                .itemInventoryPhotoId(1)
                .itemInventoryId(2)
                .md5("md5")
                .fileName("photo.jpg")
                .status(PhotoStatus.UPLOADED)
                .build();

        when(mapper.findAll()).thenReturn(Set.of(photo));
        when(mapper.findByItemInventoryPhotoId(1)).thenReturn(Optional.of(photo));
        when(mapper.findByMD5("md5")).thenReturn(Optional.of(photo));
        when(mapper.updateStatus("md5", PhotoStatus.UPLOADED, PhotoStatus.PROCESSED))
                .thenReturn(0)
                .thenReturn(1);
        when(mapper.markUploaded("md5", "bucket", "key", 100L)).thenReturn(1);
        when(mapper.replaceStoredObject(1, "photo.jpg", "md5", null, "bucket", "key", 100L, true, "caption", PhotoStatus.PROCESSED))
                .thenReturn(1);
        when(mapper.updateMetadata(1, "photo.jpg", null, true, "caption", PhotoStatus.PROCESSED)).thenReturn(1);
        when(mapper.clearPrimaryForItem(2)).thenReturn(1);
        when(mapper.setPrimary(2, "md5")).thenReturn(1);
        when(mapper.findByItemInventoryId(2)).thenReturn(Set.of(photo));
        when(mapper.findPrimaryByItemInventoryId(2)).thenReturn(Optional.of(photo));
        when(mapper.findByStatus(PhotoStatus.FAILED)).thenReturn(Set.of(photo));
        when(mapper.deleteByMd5("md5")).thenReturn(1);
        when(mapper.deleteByMd5AndStorage("md5", "bucket", "key")).thenReturn(1);
        when(mapper.findWithoutS3Key()).thenReturn(Set.of(photo));

        assertThat(dao.findAll()).containsExactly(photo);
        assertThat(dao.findByItemInventoryPhotoId(1)).contains(photo);
        assertThat(dao.findByUuid("md5")).contains(photo);
        assertThatThrownBy(() -> dao.transitionStatus("md5", PhotoStatus.PROCESSED, PhotoStatus.UPLOADED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid transition: PROCESSED ? UPLOADED");
        assertThatThrownBy(() -> dao.transitionStatus("md5", PhotoStatus.UPLOADED, PhotoStatus.PROCESSED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stale state or race condition for md5=md5");
        assertThat(dao.transitionStatus("md5", PhotoStatus.UPLOADED, PhotoStatus.PROCESSED)).isOne();
        assertThat(dao.markUploaded("md5", "bucket", "key", 100L)).isOne();
        assertThat(dao.replaceStoredObject(1, "photo.jpg", "md5", "bucket", "key", 100L, true, "caption", PhotoStatus.PROCESSED)).isOne();
        assertThat(dao.updateMetadata(1, "photo.jpg", true, "caption", PhotoStatus.PROCESSED)).isOne();
        dao.clearPrimaryForItem(2);
        dao.setPrimary(2, "md5");
        dao.setPrimaryForItem(2, "md5");
        dao.setPrimaryPhoto(2, "md5");
        assertThat(dao.findByItemInventoryId(2)).containsExactly(photo);
        assertThat(dao.findPrimaryByItemInventoryId(2)).contains(photo);
        assertThat(dao.findByStatus(PhotoStatus.FAILED)).containsExactly(photo);
        assertThat(dao.countByStatus(PhotoStatus.FAILED)).isOne();
        assertThat(dao.findFailed()).containsExactly(photo);
        assertThat(dao.resetToUploaded("md5")).isZero();
        assertThat(dao.deleteByMd5("md5")).isOne();
        assertThat(dao.deleteByMd5AndStorage("md5", "bucket", "key")).isOne();
        assertThat(dao.findWithoutS3Key()).containsExactly(photo);
    }
}
