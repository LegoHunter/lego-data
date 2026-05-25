package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceType;
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
    void findItemInventoryIdsNeedingSyncFindsDistinctPendingFlickrWork() {
        insertImageHostingService();
        ItemInventory missingImageInventory = insertItemInventory("uuid-missing-image");
        insertItemInventoryPhoto(missingImageInventory.getItemInventoryId(), "11111111111111111111111111111111");

        ItemInventory metadataMismatchInventory = insertItemInventory("uuid-metadata-mismatch");
        ItemInventoryPhoto metadataMismatchPhoto = insertItemInventoryPhoto(metadataMismatchInventory.getItemInventoryId(), "22222222222222222222222222222222");
        ExternalImage metadataMismatchImage = externalImage(10, metadataMismatchPhoto.getItemInventoryPhotoId(), "image-metadata-mismatch", "22222222222222222222222222222222");
        metadataMismatchImage.setMetadataHashAtSync("old-metadata");
        metadataMismatchImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(metadataMismatchImage);
        ExternalImageAlbum metadataMismatchAlbum = externalImageAlbum(10, metadataMismatchInventory.getItemInventoryId(), "album-metadata-mismatch");
        metadataMismatchAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(metadataMismatchAlbum);

        ItemInventory missingAlbumInventory = insertItemInventory("uuid-missing-album");
        ItemInventoryPhoto missingAlbumPhoto = insertItemInventoryPhoto(missingAlbumInventory.getItemInventoryId(), "33333333333333333333333333333333");
        ExternalImage missingAlbumImage = externalImage(10, missingAlbumPhoto.getItemInventoryPhotoId(), "image-missing-album", "33333333333333333333333333333333");
        missingAlbumImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(missingAlbumImage);

        ItemInventory failedImageInventory = insertItemInventory("uuid-failed-image");
        ItemInventoryPhoto failedImagePhoto = insertItemInventoryPhoto(failedImageInventory.getItemInventoryId(), "44444444444444444444444444444444");
        ExternalImage failedImage = externalImage(10, failedImagePhoto.getItemInventoryPhotoId(), "image-failed", "44444444444444444444444444444444");
        failedImage.setSyncStatus(ExternalSyncStatus.FAILED);
        externalImageMapper.insert(failedImage);
        ExternalImageAlbum failedImageAlbum = externalImageAlbum(10, failedImageInventory.getItemInventoryId(), "album-failed");
        failedImageAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(failedImageAlbum);

        ItemInventory syncedInventory = insertItemInventory("uuid-synced");
        ItemInventoryPhoto syncedPhoto = insertItemInventoryPhoto(syncedInventory.getItemInventoryId(), "55555555555555555555555555555555");
        ExternalImage syncedImage = externalImage(10, syncedPhoto.getItemInventoryPhotoId(), "image-synced", "55555555555555555555555555555555");
        syncedImage.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageMapper.insert(syncedImage);
        ExternalImageAlbum syncedAlbum = externalImageAlbum(10, syncedInventory.getItemInventoryId(), "album-synced");
        syncedAlbum.setSyncStatus(ExternalSyncStatus.SYNCED);
        externalImageAlbumMapper.insert(syncedAlbum);

        assertThat(externalImageMapper.findItemInventoryIdsNeedingSync(10, false, 10))
                .containsExactly(
                        missingImageInventory.getItemInventoryId(),
                        metadataMismatchInventory.getItemInventoryId(),
                        missingAlbumInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsNeedingSync(10, true, 10))
                .containsExactly(
                        missingImageInventory.getItemInventoryId(),
                        metadataMismatchInventory.getItemInventoryId(),
                        missingAlbumInventory.getItemInventoryId(),
                        failedImageInventory.getItemInventoryId()
                );
        assertThat(externalImageMapper.findItemInventoryIdsNeedingSync(10, true, 2))
                .containsExactly(
                        missingImageInventory.getItemInventoryId(),
                        metadataMismatchInventory.getItemInventoryId()
                );
    }

    private void insertImageHostingService() {
        externalServiceTypeMapper.insertExternalServiceType(ExternalServiceType.builder()
                .externalServiceTypeId(5)
                .externalServiceTypeName("IMAGE_HOSTING")
                .externalServiceTypeDescription("Image Hosting Service")
                .build());
        ExternalService service = new ExternalService();
        service.setExternalServiceId(10);
        service.setExternalServiceName("Flickr");
        service.setExternalServiceUrl("https://www.flickr.com");
        service.setExternalServiceTypeId(5);
        externalServiceMapper.insertExternalService(service);
    }
}
