package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
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
