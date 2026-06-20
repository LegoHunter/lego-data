package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.enums.ExternalSyncStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalImageMapperTest extends MapperTestSupport {

    @Test
    void insertFindsUpdatesUpsertsAndDeletesExternalImage() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-external-image");
        ItemInventoryPhoto photo = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ExternalImage image = externalImage(10, photo.getItemInventoryPhotoId(), "image-1", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        image.setMetadataHashAtSync("metadata-hash-a");

        externalImageMapper.insert(image);
        assertThat(image.getExternalImageId()).isNotNull();

        assertThat(externalImageMapper.findAll()).hasSize(1);
        assertThat(externalImageMapper.findByExternalImageId(image.getExternalImageId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getMd5AtUpload()).isEqualTo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                    assertThat(found.getMetadataHashAtSync()).isEqualTo("metadata-hash-a");
                });
        assertThat(externalImageMapper.findByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalServiceImageId()).isEqualTo("image-1"));
        assertThat(externalImageMapper.findByExternalServiceIdAndExternalServiceImageId(10, "image-1"))
                .hasValueSatisfying(found -> assertThat(found.getImageUrl()).isEqualTo("https://photos.example/images/image-1"));
        assertThat(externalImageMapper.findByItemInventoryPhotoId(photo.getItemInventoryPhotoId())).hasSize(1);
        assertThat(externalImageMapper.findBySyncStatus(ExternalSyncStatus.PENDING)).hasSize(1);

        image.setTitle("Updated image title");
        image.setMetadataHashAtSync("metadata-hash-updated");
        image.setSyncStatus(ExternalSyncStatus.SYNCED);
        assertThat(externalImageMapper.update(image)).isOne();
        assertThat(externalImageMapper.findByExternalImageId(image.getExternalImageId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getSyncStatus()).isEqualTo(ExternalSyncStatus.SYNCED);
                    assertThat(found.getMetadataHashAtSync()).isEqualTo("metadata-hash-updated");
                });

        externalImageMapper.upsert(ExternalImage.builder()
                .externalServiceId(10)
                .itemInventoryPhotoId(photo.getItemInventoryPhotoId())
                .externalServiceImageId("image-1-upserted")
                .title("Upserted image")
                .imageUrl("https://photos.example/images/upserted")
                .md5AtUpload("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
                .metadataHashAtSync("metadata-hash-upserted")
                .syncStatus(ExternalSyncStatus.FAILED)
                .errorMessage("failed")
                .build());

        assertThat(externalImageMapper.findByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getExternalServiceImageId()).isEqualTo("image-1-upserted");
                    assertThat(found.getMd5AtUpload()).isEqualTo("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                    assertThat(found.getMetadataHashAtSync()).isEqualTo("metadata-hash-upserted");
                });

        assertThat(externalImageMapper.deleteByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId())).isOne();
        assertThat(externalImageMapper.findAll()).isEmpty();

        externalImageMapper.insert(image);
        assertThat(externalImageMapper.deleteByExternalImageId(image.getExternalImageId())).isOne();
    }

    @Test
    void findItemInventoryIdsNeedingSyncWorkFindsEachFocusedWorkType() {
        insertImageHostingService();
        ItemInventory missingPhotoLinkInventory = insertItemInventory("uuid-missing-photo-link");
        insertItemInventoryPhoto(missingPhotoLinkInventory.getItemInventoryId(), "11111111111111111111111111111111");

        ItemInventory metadataMismatchInventory = insertItemInventory("uuid-metadata-mismatch");
        ItemInventoryPhoto metadataMismatchPhoto = insertItemInventoryPhoto(metadataMismatchInventory.getItemInventoryId(), "22222222222222222222222222222222");
        ExternalImage metadataMismatchImage = externalImage(10, metadataMismatchPhoto.getItemInventoryPhotoId(), "image-metadata-mismatch", "22222222222222222222222222222222");
        metadataMismatchImage.setMetadataHashAtSync("old-metadata");
        metadataMismatchImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(metadataMismatchImage);
        ExternalImageAlbum metadataMismatchAlbum = externalImageAlbum(10, metadataMismatchInventory.getItemInventoryId(), "album-metadata-mismatch");
        metadataMismatchAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(metadataMismatchAlbum);
        externalImageAlbumImageMapper.insert(externalImageAlbumImage(
                metadataMismatchAlbum.getExternalImageAlbumId(),
                metadataMismatchImage.getExternalImageId(),
                1,
                true
        ));

        ItemInventory missingAlbumLinkInventory = insertItemInventory("uuid-missing-album-link");
        ItemInventoryPhoto missingAlbumLinkPhoto = insertItemInventoryPhoto(missingAlbumLinkInventory.getItemInventoryId(), "33333333333333333333333333333333");
        ExternalImage missingAlbumLinkImage = externalImage(10, missingAlbumLinkPhoto.getItemInventoryPhotoId(), "image-missing-album", "33333333333333333333333333333333");
        missingAlbumLinkImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(missingAlbumLinkImage);
        ExternalImageAlbum missingAlbumLinkAlbum = externalImageAlbum(10, missingAlbumLinkInventory.getItemInventoryId(), null);
        missingAlbumLinkAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(missingAlbumLinkAlbum);

        ItemInventory stalePhotoLinkInventory = insertItemInventory("uuid-stale-photo-link");
        ItemInventoryPhoto stalePhotoLinkPhoto = insertItemInventoryPhoto(stalePhotoLinkInventory.getItemInventoryId(), "77777777777777777777777777777777");
        ExternalImage stalePhotoLinkImage = externalImage(10, stalePhotoLinkPhoto.getItemInventoryPhotoId(), null, "77777777777777777777777777777777");
        stalePhotoLinkImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(stalePhotoLinkImage);
        ExternalImageAlbum stalePhotoLinkAlbum = externalImageAlbum(10, stalePhotoLinkInventory.getItemInventoryId(), "album-stale-photo-link");
        stalePhotoLinkAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(stalePhotoLinkAlbum);

        ItemInventory failedImageInventory = insertItemInventory("uuid-failed-image");
        ItemInventoryPhoto failedImagePhoto = insertItemInventoryPhoto(failedImageInventory.getItemInventoryId(), "44444444444444444444444444444444");
        ExternalImage failedImage = externalImage(10, failedImagePhoto.getItemInventoryPhotoId(), "image-failed", "44444444444444444444444444444444");
        failedImage.setSyncStatus(ExternalSyncStatus.FAILED);
        externalImageMapper.insert(failedImage);
        ExternalImageAlbum failedImageAlbum = externalImageAlbum(10, failedImageInventory.getItemInventoryId(), "album-failed");
        failedImageAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(failedImageAlbum);

        ItemInventory failedAlbumInventory = insertItemInventory("uuid-failed-album");
        ItemInventoryPhoto failedAlbumPhoto = insertItemInventoryPhoto(failedAlbumInventory.getItemInventoryId(), "88888888888888888888888888888888");
        ExternalImage failedAlbumImage = externalImage(10, failedAlbumPhoto.getItemInventoryPhotoId(), "image-failed-album", "88888888888888888888888888888888");
        failedAlbumImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(failedAlbumImage);
        ExternalImageAlbum failedAlbum = externalImageAlbum(10, failedAlbumInventory.getItemInventoryId(), "album-failed-status");
        failedAlbum.setSyncStatus(ExternalSyncStatus.FAILED);
        externalImageAlbumMapper.insert(failedAlbum);

        ItemInventory pendingImageInventory = insertItemInventory("uuid-pending-image");
        ItemInventoryPhoto pendingImagePhoto = insertItemInventoryPhoto(pendingImageInventory.getItemInventoryId(), "66666666666666666666666666666666");
        ExternalImage pendingImage = externalImage(10, pendingImagePhoto.getItemInventoryPhotoId(), "image-pending", "66666666666666666666666666666666");
        externalImageMapper.insert(pendingImage);
        ExternalImageAlbum pendingImageAlbum = externalImageAlbum(10, pendingImageInventory.getItemInventoryId(), "album-pending");
        pendingImageAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(pendingImageAlbum);

        ItemInventory pendingAlbumInventory = insertItemInventory("uuid-pending-album");
        ItemInventoryPhoto pendingAlbumPhoto = insertItemInventoryPhoto(pendingAlbumInventory.getItemInventoryId(), "99999999999999999999999999999999");
        ExternalImage pendingAlbumImage = externalImage(10, pendingAlbumPhoto.getItemInventoryPhotoId(), "image-pending-album", "99999999999999999999999999999999");
        pendingAlbumImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(pendingAlbumImage);
        ExternalImageAlbum pendingAlbum = externalImageAlbum(10, pendingAlbumInventory.getItemInventoryId(), "album-pending-status");
        pendingAlbum.setSyncStatus(ExternalSyncStatus.PENDING);
        externalImageAlbumMapper.insert(pendingAlbum);

        ItemInventory missingAlbumMembershipInventory = insertItemInventory("uuid-missing-album-membership");
        ItemInventoryPhoto missingAlbumMembershipPhoto = insertItemInventoryPhoto(missingAlbumMembershipInventory.getItemInventoryId(), "12121212121212121212121212121212");
        ExternalImage missingAlbumMembershipImage = externalImage(10, missingAlbumMembershipPhoto.getItemInventoryPhotoId(), "image-missing-album-membership", "12121212121212121212121212121212");
        missingAlbumMembershipImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(missingAlbumMembershipImage);
        ExternalImageAlbum missingAlbumMembershipAlbum = externalImageAlbum(10, missingAlbumMembershipInventory.getItemInventoryId(), "album-missing-album-membership");
        missingAlbumMembershipAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(missingAlbumMembershipAlbum);

        ItemInventory syncedInventory = insertItemInventory("uuid-synced");
        ItemInventoryPhoto syncedPhoto = insertItemInventoryPhoto(syncedInventory.getItemInventoryId(), "55555555555555555555555555555555");
        ExternalImage syncedImage = externalImage(10, syncedPhoto.getItemInventoryPhotoId(), "image-synced", "55555555555555555555555555555555");
        syncedImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(syncedImage);
        ExternalImageAlbum syncedAlbum = externalImageAlbum(10, syncedInventory.getItemInventoryId(), "album-synced");
        syncedAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(syncedAlbum);
        externalImageAlbumImageMapper.insert(externalImageAlbumImage(
                syncedAlbum.getExternalImageAlbumId(),
                syncedImage.getExternalImageId(),
                1,
                true
        ));

        assertThat(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 10))
                .containsExactly(
                        missingPhotoLinkInventory.getItemInventoryId(),
                        stalePhotoLinkInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsMissingAlbumLinks(10, 10))
                .containsExactly(
                        missingPhotoLinkInventory.getItemInventoryId(),
                        missingAlbumLinkInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsWithPendingSyncRows(10, 10))
                .containsExactly(
                        pendingImageInventory.getItemInventoryId(),
                        pendingAlbumInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsWithFailedSyncRows(10, 10))
                .containsExactly(
                        failedImageInventory.getItemInventoryId(),
                        failedAlbumInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsMissingAlbumMemberships(10, 10))
                .containsExactly(missingAlbumMembershipInventory.getItemInventoryId());
        assertThat(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 10))
                .containsExactly(metadataMismatchInventory.getItemInventoryId());
        assertThat(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 1))
                .containsExactly(missingPhotoLinkInventory.getItemInventoryId());
    }
}
