package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ExternalImage;
import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.ExternalImageAlbumImage;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ExternalImageAlbumImageMapperTest extends MapperTestSupport {

    @Test
    void insertFindsUpdatesUpsertsPrimaryAndDeletesAlbumImageMembership() {
        insertImageHostingService();
        ItemInventory itemInventory = insertItemInventory("uuid-external-membership");
        ItemInventoryPhoto firstPhoto = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "cccccccccccccccccccccccccccccccc");
        ItemInventoryPhoto secondPhoto = insertItemInventoryPhoto(itemInventory.getItemInventoryId(), "dddddddddddddddddddddddddddddddd");
        ExternalImageAlbum album = externalImageAlbum(10, itemInventory.getItemInventoryId(), "album-membership");
        externalImageAlbumMapper.insert(album);
        ExternalImage firstImage = externalImage(10, firstPhoto.getItemInventoryPhotoId(), "image-first", "cccccccccccccccccccccccccccccccc");
        ExternalImage secondImage = externalImage(10, secondPhoto.getItemInventoryPhotoId(), "image-second", "dddddddddddddddddddddddddddddddd");
        externalImageMapper.insert(firstImage);
        externalImageMapper.insert(secondImage);

        ExternalImageAlbumImage firstMembership = externalImageAlbumImage(
                album.getExternalImageAlbumId(),
                firstImage.getExternalImageId(),
                1,
                true);
        externalImageAlbumImageMapper.insert(firstMembership);

        assertThat(externalImageAlbumImageMapper.findAll()).hasSize(1);
        assertThat(externalImageAlbumImageMapper.findByExternalImageAlbumIdAndExternalImageId(
                album.getExternalImageAlbumId(), firstImage.getExternalImageId()))
                .hasValueSatisfying(found -> assertThat(found.getPrimary()).isTrue());
        assertThat(externalImageAlbumImageMapper.findByExternalImageAlbumId(album.getExternalImageAlbumId())).hasSize(1);
        assertThat(externalImageAlbumImageMapper.findByExternalImageId(firstImage.getExternalImageId())).hasSize(1);
        assertThat(externalImageAlbumImageMapper.findPrimaryByExternalImageAlbumId(album.getExternalImageAlbumId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalImageId()).isEqualTo(firstImage.getExternalImageId()));

        firstMembership.setSortOrder(2);
        firstMembership.setPrimary(false);
        assertThat(externalImageAlbumImageMapper.update(firstMembership)).isOne();
        assertThat(externalImageAlbumImageMapper.findPrimaryByExternalImageAlbumId(album.getExternalImageAlbumId())).isEmpty();

        ExternalImageAlbumImage secondMembership = externalImageAlbumImage(
                album.getExternalImageAlbumId(),
                secondImage.getExternalImageId(),
                1,
                true);
        externalImageAlbumImageMapper.upsert(secondMembership);
        assertThat(externalImageAlbumImageMapper.findByExternalImageAlbumId(album.getExternalImageAlbumId())).hasSize(2);
        assertThat(externalImageAlbumImageMapper.clearPrimaryForExternalImageAlbum(album.getExternalImageAlbumId())).isEqualTo(2);
        assertThat(externalImageAlbumImageMapper.findPrimaryByExternalImageAlbumId(album.getExternalImageAlbumId())).isEmpty();

        secondMembership.setPrimary(true);
        secondMembership.setSortOrder(3);
        externalImageAlbumImageMapper.upsert(secondMembership);
        assertThat(externalImageAlbumImageMapper.findByExternalImageAlbumIdAndExternalImageId(
                album.getExternalImageAlbumId(), secondImage.getExternalImageId()))
                .hasValueSatisfying(found -> assertThat(found.getSortOrder()).isEqualTo(3));

        assertThat(externalImageAlbumImageMapper.deleteByExternalImageAlbumIdAndExternalImageId(
                album.getExternalImageAlbumId(), firstImage.getExternalImageId())).isOne();
        assertThat(externalImageAlbumImageMapper.deleteByExternalImageId(secondImage.getExternalImageId())).isOne();
        assertThat(externalImageAlbumImageMapper.findAll()).isEmpty();

        externalImageAlbumImageMapper.insert(secondMembership);
        assertThat(externalImageAlbumImageMapper.deleteByExternalImageAlbumId(album.getExternalImageAlbumId())).isOne();
    }
}
