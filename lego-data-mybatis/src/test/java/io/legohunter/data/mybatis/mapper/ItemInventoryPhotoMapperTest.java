package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.ItemInventoryPhoto;
import io.legohunter.data.enums.PhotoStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventoryPhotoMapperTest extends MapperTestSupport {

    @Test
    void insertFindsStatusPrimaryStorageUpdatesAndDeletes() {
        ItemInventory itemInventory = insertItemInventory("uuid-photo");
        ItemInventoryPhoto photo = ItemInventoryPhoto.builder()
                .itemInventoryId(itemInventory.getItemInventoryId())
                .s3Bucket("bucket")
                .s3Key("old-key")
                .md5("md5-a")
                .metadataHash("metadata-hash-a")
                .fileName("photo.jpg")
                .fileSize(1000L)
                .primary(false)
                .caption("Caption")
                .status(PhotoStatus.UPLOADED)
                .build();

        itemInventoryPhotoMapper.insert(photo);
        assertThat(photo.getItemInventoryPhotoId()).isNotNull();

        assertThat(itemInventoryPhotoMapper.findAll()).hasSize(1);
        assertThat(itemInventoryPhotoMapper.findByItemInventoryPhotoId(photo.getItemInventoryPhotoId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getMd5()).isEqualTo("md5-a");
                    assertThat(found.getMetadataHash()).isEqualTo("metadata-hash-a");
                });
        assertThat(itemInventoryPhotoMapper.findByMD5("md5-a"))
                .hasValueSatisfying(found -> assertThat(found.getFileName()).isEqualTo("photo.jpg"));
        assertThat(itemInventoryPhotoMapper.findByItemInventoryIdAndFileName(itemInventory.getItemInventoryId(), "photo.jpg"))
                .hasValueSatisfying(found -> assertThat(found.getS3Key()).isEqualTo("old-key"));
        assertThat(itemInventoryPhotoMapper.findByItemInventoryId(itemInventory.getItemInventoryId())).hasSize(1);
        assertThat(itemInventoryPhotoMapper.findByStatus(PhotoStatus.UPLOADED)).hasSize(1);
        assertThat(itemInventoryPhotoMapper.findWithoutS3Key()).isEmpty();

        assertThat(itemInventoryPhotoMapper.updateStatus("md5-a", PhotoStatus.UPLOADED, PhotoStatus.PROCESSED)).isOne();
        assertThat(itemInventoryPhotoMapper.setPrimary(itemInventory.getItemInventoryId(), "md5-a")).isOne();
        assertThat(itemInventoryPhotoMapper.findPrimaryByItemInventoryId(itemInventory.getItemInventoryId()))
                .hasValueSatisfying(found -> assertThat(found.getPrimary()).isTrue());
        assertThat(itemInventoryPhotoMapper.clearPrimaryForItem(itemInventory.getItemInventoryId())).isOne();
        assertThat(itemInventoryPhotoMapper.markUploaded("md5-a", "bucket-new", "key-new", 2000L)).isZero();
        assertThat(itemInventoryPhotoMapper.replaceStoredObject(
                photo.getItemInventoryPhotoId(), "renamed.jpg", "md5-b", "metadata-hash-b", "bucket-new", "key-new", 2000L,
                true, "New caption", PhotoStatus.PROCESSED)).isOne();
        assertThat(itemInventoryPhotoMapper.updateMetadata(
                photo.getItemInventoryPhotoId(), "renamed.jpg", "metadata-hash-c", false, "Metadata caption", PhotoStatus.FAILED)).isOne();
        assertThat(itemInventoryPhotoMapper.findByMD5("md5-b"))
                .hasValueSatisfying(found -> assertThat(found.getMetadataHash()).isEqualTo("metadata-hash-c"));
        assertThat(itemInventoryPhotoMapper.deleteByMd5AndStorage("md5-b", "bucket-new", "wrong-key")).isZero();
        assertThat(itemInventoryPhotoMapper.deleteByMd5AndStorage("md5-b", "bucket-new", "key-new")).isOne();
    }

    @Test
    void deleteByMd5RemovesMatchingPhoto() {
        ItemInventory itemInventory = insertItemInventory("uuid-photo-delete");
        itemInventoryPhotoMapper.insert(ItemInventoryPhoto.builder()
                .itemInventoryId(itemInventory.getItemInventoryId())
                .s3Bucket("bucket")
                .s3Key("key")
                .md5("md5-delete")
                .fileName("photo.jpg")
                .fileSize(1000L)
                .primary(false)
                .status(PhotoStatus.PROCESSED)
                .build());

        assertThat(itemInventoryPhotoMapper.deleteByMd5("md5-delete")).isOne();
        assertThat(itemInventoryPhotoMapper.findByMD5("md5-delete")).isEmpty();
    }
}
