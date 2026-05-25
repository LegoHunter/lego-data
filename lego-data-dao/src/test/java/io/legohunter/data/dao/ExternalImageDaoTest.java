package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalImageAlbumImage;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.enums.ExternalSyncStatus;
import io.legohunter.data.enums.PhotoStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = "/scripts/db/h2/current_schema.ddl")
class ExternalImageDaoTest {

    @Autowired ExternalImageAlbumDao externalImageAlbumDao;
    @Autowired ExternalImageDao externalImageDao;
    @Autowired ExternalImageAlbumImageDao externalImageAlbumImageDao;
    @Autowired ExternalServiceTypeDao externalServiceTypeDao;
    @Autowired ExternalServiceDao externalServiceDao;
    @Autowired ItemInventoryDao itemInventoryDao;
    @Autowired ItemInventoryPhotoDao itemInventoryPhotoDao;

    @Test
    void externalImageAlbumDaoExposesCrudUpsertAndFindOrCreateOperations() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-dao-album");
        ExternalImageAlbum album = externalImageAlbum(itemInventory.getItemInventoryId(), "album-dao");

        assertThat(externalImageAlbumDao.insert(album).getExternalImageAlbumId()).isNotNull();
        assertThat(externalImageAlbumDao.findAll()).hasSize(1);
        assertThat(externalImageAlbumDao.findByExternalImageAlbumId(album.getExternalImageAlbumId())).isPresent();
        assertThat(externalImageAlbumDao.findByExternalServiceIdAndItemInventoryId(10, itemInventory.getItemInventoryId())).isPresent();
        assertThat(externalImageAlbumDao.findByExternalServiceIdAndExternalAlbumId(10, "album-dao")).isPresent();
        assertThat(externalImageAlbumDao.findBySyncStatus(ExternalSyncStatus.PENDING)).hasSize(1);

        album.setShortUrl("https://short.example/updated");
        album.setSyncStatus(ExternalSyncStatus.SYNCED);
        assertThat(externalImageAlbumDao.update(album)).isOne();
        assertThat(externalImageAlbumDao.findByExternalImageAlbumId(album.getExternalImageAlbumId()))
                .hasValueSatisfying(found -> assertThat(found.getShortUrl()).isEqualTo("https://short.example/updated"));

        externalImageAlbumDao.upsert(ExternalImageAlbum.builder()
                .externalServiceId(10)
                .itemInventoryId(itemInventory.getItemInventoryId())
                .externalAlbumId("album-dao-upsert")
                .title("Upserted")
                .albumUrl("https://photos.example/upsert")
                .shortUrl("https://short.example/upsert")
                .syncStatus(ExternalSyncStatus.FAILED)
                .build());
        assertThat(externalImageAlbumDao.findByExternalServiceIdAndItemInventoryId(10, itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalAlbumId()).isEqualTo("album-dao-upsert"));

        ExternalImageAlbum existing = externalImageAlbumDao.findOrCreateForItem(externalImageAlbum(itemInventory.getItemInventoryId(), "ignored"));
        assertThat(existing.getExternalImageAlbumId()).isEqualTo(album.getExternalImageAlbumId());

        ItemInventory secondItemInventory = insertItemInventory("uuid-dao-album-new");
        ExternalImageAlbum created = externalImageAlbumDao.findOrCreateForItem(externalImageAlbum(secondItemInventory.getItemInventoryId(), "album-created"));
        assertThat(created.getExternalImageAlbumId()).isNotNull();

        assertThat(externalImageAlbumDao.deleteByExternalServiceIdAndItemInventoryId(10, secondItemInventory.getItemInventoryId())).isOne();
        assertThat(externalImageAlbumDao.deleteByExternalImageAlbumId(album.getExternalImageAlbumId())).isOne();
    }

    @Test
    void externalImageDaoExposesCrudUpsertAndMd5ComparisonOperations() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-dao-image");
        ItemInventoryPhoto photo = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        ExternalImage image = externalImage(photo.getItemInventoryPhotoId(), "image-dao", "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");

        assertThat(externalImageDao.insert(image).getExternalImageId()).isNotNull();
        assertThat(externalImageDao.findAll()).hasSize(1);
        assertThat(externalImageDao.findByExternalImageId(image.getExternalImageId())).isPresent();
        assertThat(externalImageDao.findByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId())).isPresent();
        assertThat(externalImageDao.findByExternalServiceIdAndExternalServiceImageId(10, "image-dao")).isPresent();
        assertThat(externalImageDao.findByItemInventoryPhotoId(photo.getItemInventoryPhotoId())).hasSize(1);
        assertThat(externalImageDao.findBySyncStatus(ExternalSyncStatus.PENDING)).hasSize(1);
        assertThat(externalImageDao.hasUploadedMd5(10, photo.getItemInventoryPhotoId(), "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")).isTrue();
        assertThat(externalImageDao.hasUploadedMd5(10, photo.getItemInventoryPhotoId(), "ffffffffffffffffffffffffffffffff")).isFalse();
        assertThat(externalImageDao.hasSyncedMetadataHash(10, photo.getItemInventoryPhotoId(), "metadata-eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")).isTrue();
        assertThat(externalImageDao.hasSyncedMetadataHash(10, photo.getItemInventoryPhotoId(), "different-metadata")).isFalse();
        assertThat(externalImageDao.findItemInventoryIdsNeedingSync(10, false, 10))
                .containsExactly(itemInventory.getItemInventoryId());

        image.setMd5AtUpload("ffffffffffffffffffffffffffffffff");
        image.setMetadataHashAtSync("metadata-updated");
        image.setSyncStatus(ExternalSyncStatus.SYNCED);
        assertThat(externalImageDao.update(image)).isOne();
        assertThat(externalImageDao.findByExternalImageId(image.getExternalImageId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getMd5AtUpload()).isEqualTo("ffffffffffffffffffffffffffffffff");
                    assertThat(found.getMetadataHashAtSync()).isEqualTo("metadata-updated");
                });

        externalImageDao.upsert(ExternalImage.builder()
                .externalServiceId(10)
                .itemInventoryPhotoId(photo.getItemInventoryPhotoId())
                .externalServiceImageId("image-dao-upsert")
                .title("Upserted")
                .imageUrl("https://photos.example/images/upsert")
                .md5AtUpload("11111111111111111111111111111111")
                .metadataHashAtSync("metadata-upsert")
                .syncStatus(ExternalSyncStatus.FAILED)
                .build());
        assertThat(externalImageDao.findByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getExternalServiceImageId()).isEqualTo("image-dao-upsert");
                    assertThat(found.getMetadataHashAtSync()).isEqualTo("metadata-upsert");
                });

        assertThat(externalImageDao.deleteByExternalServiceIdAndItemInventoryPhotoId(10, photo.getItemInventoryPhotoId())).isOne();
        externalImageDao.insert(image);
        assertThat(externalImageDao.deleteByExternalImageId(image.getExternalImageId())).isOne();
    }

    @Test
    void externalImageAlbumImageDaoExposesCrudUpsertPrimaryAndDeleteOperations() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-dao-membership");
        ItemInventoryPhoto firstPhoto = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "12121212121212121212121212121212");
        ItemInventoryPhoto secondPhoto = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "34343434343434343434343434343434");
        ExternalImageAlbum album = externalImageAlbumDao.insert(externalImageAlbum(itemInventory.getItemInventoryId(), "album-membership-dao"));
        ExternalImage firstImage = externalImageDao.insert(externalImage(firstPhoto.getItemInventoryPhotoId(), "image-first-dao", "12121212121212121212121212121212"));
        ExternalImage secondImage = externalImageDao.insert(externalImage(secondPhoto.getItemInventoryPhotoId(), "image-second-dao", "34343434343434343434343434343434"));
        ExternalImageAlbumImage firstMembership = externalImageAlbumImage(album.getExternalImageAlbumId(), firstImage.getExternalImageId(), 1, true);

        externalImageAlbumImageDao.insert(firstMembership);
        assertThat(externalImageAlbumImageDao.findAll()).hasSize(1);
        assertThat(externalImageAlbumImageDao.findByExternalImageAlbumIdAndExternalImageId(album.getExternalImageAlbumId(), firstImage.getExternalImageId())).isPresent();
        assertThat(externalImageAlbumImageDao.findByExternalImageAlbumId(album.getExternalImageAlbumId())).hasSize(1);
        assertThat(externalImageAlbumImageDao.findByExternalImageId(firstImage.getExternalImageId())).hasSize(1);
        assertThat(externalImageAlbumImageDao.findPrimaryByExternalImageAlbumId(album.getExternalImageAlbumId())).isPresent();

        firstMembership.setPrimary(false);
        firstMembership.setSortOrder(2);
        assertThat(externalImageAlbumImageDao.update(firstMembership)).isOne();
        assertThat(externalImageAlbumImageDao.clearPrimaryForExternalImageAlbum(album.getExternalImageAlbumId())).isOne();

        ExternalImageAlbumImage secondMembership = externalImageAlbumImage(album.getExternalImageAlbumId(), secondImage.getExternalImageId(), 3, false);
        externalImageAlbumImageDao.upsert(secondMembership);
        externalImageAlbumImageDao.setPrimaryImage(album.getExternalImageAlbumId(), secondImage.getExternalImageId());
        assertThat(externalImageAlbumImageDao.findPrimaryByExternalImageAlbumId(album.getExternalImageAlbumId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalImageId()).isEqualTo(secondImage.getExternalImageId()));

        assertThat(externalImageAlbumImageDao.deleteByExternalImageAlbumIdAndExternalImageId(album.getExternalImageAlbumId(), firstImage.getExternalImageId())).isOne();
        assertThat(externalImageAlbumImageDao.deleteByExternalImageId(secondImage.getExternalImageId())).isOne();
        externalImageAlbumImageDao.insert(secondMembership);
        assertThat(externalImageAlbumImageDao.deleteByExternalImageAlbumId(album.getExternalImageAlbumId())).isOne();
    }

    private void insertImageHostingService() {
        externalServiceTypeDao.insert(ExternalServiceType.builder()
                .externalServiceTypeId(5)
                .externalServiceTypeName("IMAGE_HOSTING")
                .externalServiceTypeDescription("Image Hosting Service")
                .build());
        ExternalService service = new ExternalService();
        service.setExternalServiceId(10);
        service.setExternalServiceName("Flickr");
        service.setExternalServiceUrl("https://www.flickr.com");
        service.setExternalServiceTypeId(5);
        externalServiceDao.insert(service);
    }

    private ItemInventory insertItemInventory(String uuid) {
        ItemInventory itemInventory = new ItemInventory();
        itemInventory.setUuid(uuid);
        itemInventory.setDescription("Inventory " + uuid);
        itemInventory.setActive(true);
        itemInventory.setForSale(false);
        itemInventoryDao.insert(itemInventory);
        return itemInventory;
    }

    private ItemInventoryPhoto insertItemInventoryPhoto(Integer itemInventoryId, String md5) {
        return itemInventoryPhotoDao.insertPhoto(
                itemInventoryId,
                md5,
                md5 + ".jpg",
                "lego-photos-sandbox",
                "3001/uuid/" + md5 + ".jpg",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );
    }

    private ExternalImageAlbum externalImageAlbum(Integer itemInventoryId, String externalAlbumId) {
        return ExternalImageAlbum.builder()
                .externalServiceId(10)
                .itemInventoryId(itemInventoryId)
                .externalAlbumId(externalAlbumId)
                .title("3001-1 [uuid]")
                .albumUrl("https://photos.example/albums/" + externalAlbumId)
                .shortUrl("https://short.example/" + externalAlbumId)
                .syncStatus(ExternalSyncStatus.PENDING)
                .build();
    }

    private ExternalImage externalImage(Integer itemInventoryPhotoId, String externalServiceImageId, String md5) {
        return ExternalImage.builder()
                .externalServiceId(10)
                .itemInventoryPhotoId(itemInventoryPhotoId)
                .externalServiceImageId(externalServiceImageId)
                .title("3001-1 [uuid]")
                .imageUrl("https://photos.example/images/" + externalServiceImageId)
                .md5AtUpload(md5)
                .metadataHashAtSync("metadata-" + md5)
                .syncStatus(ExternalSyncStatus.PENDING)
                .build();
    }

    private ExternalImageAlbumImage externalImageAlbumImage(Long externalImageAlbumId, Long externalImageId, int sortOrder, boolean primary) {
        return ExternalImageAlbumImage.builder()
                .externalImageAlbumId(externalImageAlbumId)
                .externalImageId(externalImageId)
                .sortOrder(sortOrder)
                .primary(primary)
                .build();
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}
