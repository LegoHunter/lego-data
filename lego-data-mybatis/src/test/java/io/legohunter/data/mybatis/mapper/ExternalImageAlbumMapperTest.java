package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.enums.ExternalSyncStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalImageAlbumMapperTest extends MapperTestSupport {

    @Test
    void insertFindsUpdatesUpsertsAndDeletesExternalImageAlbum() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-external-album");
        ExternalImageAlbum album = externalImageAlbum(10, itemInventory.getItemInventoryId(), "album-1");

        externalImageAlbumMapper.insert(album);
        assertThat(album.getExternalImageAlbumId()).isNotNull();

        assertThat(externalImageAlbumMapper.findAll()).hasSize(1);
        assertThat(externalImageAlbumMapper.findByExternalImageAlbumId(album.getExternalImageAlbumId()))
                .hasValueSatisfying(found -> assertThat(found.getShortUrl()).isEqualTo("https://short.example/album-1"));
        assertThat(externalImageAlbumMapper.findByExternalServiceIdAndItemInventoryId(10, itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalAlbumId()).isEqualTo("album-1"));
        assertThat(externalImageAlbumMapper.findByExternalServiceIdAndExternalAlbumId(10, "album-1"))
                .hasValueSatisfying(found -> assertThat(found.getTitle()).isEqualTo("3001-1 [uuid]"));
        assertThat(externalImageAlbumMapper.findBySyncStatus(ExternalSyncStatus.PENDING)).hasSize(1);

        album.setTitle("3001-1 [uuid-updated]");
        album.setSyncStatus(ExternalSyncStatus.SYNCED);
        assertThat(externalImageAlbumMapper.update(album)).isOne();
        assertThat(externalImageAlbumMapper.findByExternalImageAlbumId(album.getExternalImageAlbumId()))
                .hasValueSatisfying(found -> assertThat(found.getSyncStatus()).isEqualTo(ExternalSyncStatus.SYNCED));

        externalImageAlbumMapper.upsert(ExternalImageAlbum.builder()
                .externalServiceId(10)
                .itemInventoryId(itemInventory.getItemInventoryId())
                .externalAlbumId("album-1-upserted")
                .title("Upserted")
                .albumUrl("https://photos.example/albums/upserted")
                .shortUrl("https://short.example/upserted")
                .syncStatus(ExternalSyncStatus.FAILED)
                .errorMessage("failed")
                .build());

        assertThat(externalImageAlbumMapper.findByExternalServiceIdAndItemInventoryId(10, itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getExternalAlbumId()).isEqualTo("album-1-upserted");
                    assertThat(found.getErrorMessage()).isEqualTo("failed");
                });

        assertThat(externalImageAlbumMapper.deleteByExternalServiceIdAndItemInventoryId(10, itemInventory.getItemInventoryId())).isOne();
        assertThat(externalImageAlbumMapper.findAll()).isEmpty();

        externalImageAlbumMapper.insert(album);
        assertThat(externalImageAlbumMapper.deleteByExternalImageAlbumId(album.getExternalImageAlbumId())).isOne();
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
