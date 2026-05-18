package io.legohunter.data.dao;

import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.ItemInventoryPhoto;
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

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Slf4j
class ItemInventoryPhotoDaoTest {

    @Autowired
    private ItemInventoryPhotoDao dao;

    // =========================================================
    // INSERT
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void insertPhoto() {

        dao.insertPhoto(
                1,
                "md5-1",
                "photo.jpg",
                "lego-photos-sandbox",
                "3001/uuid/md5-1.jpg",
                2048L,
                false,
                PhotoStatus.PROCESSED
        );

        Optional<ItemInventoryPhoto> result =
                dao.findByMd5("md5-1");

        assertThat(result)
                .isPresent();

        assertThat(result.get())
                .extracting(
                        ItemInventoryPhoto::getItemInventoryId,
                        ItemInventoryPhoto::getMd5,
                        ItemInventoryPhoto::getFileName,
                        ItemInventoryPhoto::getS3Bucket,
                        ItemInventoryPhoto::getS3Key,
                        ItemInventoryPhoto::getFileSize,
                        ItemInventoryPhoto::getPrimary,
                        ItemInventoryPhoto::getStatus
                )
                .containsExactly(
                        1,
                        "md5-1",
                        "photo.jpg",
                        "lego-photos-sandbox",
                        "3001/uuid/md5-1.jpg",
                        2048L,
                        false,
                        PhotoStatus.PROCESSED
                );
    }

    // =========================================================
    // FIND BY MD5
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByMd5_returns_photo() {

        dao.insertPhoto(
                1,
                "md5-2",
                "photo.jpg",
                "bucket",
                "key",
                4096L,
                false,
                PhotoStatus.PROCESSED
        );

        Optional<ItemInventoryPhoto> result =
                dao.findByMd5("md5-2");

        assertThat(result)
                .isPresent();

        assertThat(result.get().getMd5())
                .isEqualTo("md5-2");
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByMd5_returns_empty_when_not_found() {

        Optional<ItemInventoryPhoto> result =
                dao.findByMd5("does-not-exist");

        assertThat(result)
                .isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByItemInventoryIdAndFileName_returnsMatchingPhoto() {

        dao.insertPhoto(
                1,
                "md5-filename-1",
                "same-name.jpg",
                "bucket",
                "key-one",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        dao.insertPhoto(
                2,
                "md5-filename-2",
                "same-name.jpg",
                "bucket",
                "key-two",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        Optional<ItemInventoryPhoto> result =
                dao.findByItemInventoryIdAndFileName(1, "same-name.jpg");

        assertThat(result)
                .isPresent();

        assertThat(result.get().getMd5())
                .isEqualTo("md5-filename-1");
    }

    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void existsByMd5_returns_true_when_exists() {

        dao.insertPhoto(
                1,
                "md5-3",
                "photo.jpg",
                "bucket",
                "key",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        assertThat(dao.existsByMd5("md5-3"))
                .isTrue();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void existsByMd5_returns_false_when_missing() {

        assertThat(dao.existsByMd5("missing"))
                .isFalse();
    }

    // =========================================================
    // PRIMARY PHOTO
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void setPrimaryPhoto_sets_only_one_primary_photo() {

        dao.insertPhoto(
                1,
                "md5-a",
                "a.jpg",
                "bucket",
                "key-a",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        dao.insertPhoto(
                1,
                "md5-b",
                "b.jpg",
                "bucket",
                "key-b",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        dao.setPrimaryPhoto(1, "md5-b");

        ItemInventoryPhoto first =
                dao.findByMd5("md5-a").orElseThrow();

        ItemInventoryPhoto second =
                dao.findByMd5("md5-b").orElseThrow();

        assertThat(first.getPrimary())
                .isFalse();

        assertThat(second.getPrimary())
                .isTrue();
    }

    // =========================================================
    // FIND BY ITEM INVENTORY ID
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByItemInventoryId_returns_all_photos() {

        dao.insertPhoto(
                1,
                "md5-10",
                "a.jpg",
                "bucket",
                "key-a",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        dao.insertPhoto(
                1,
                "md5-11",
                "b.jpg",
                "bucket",
                "key-b",
                1000L,
                true,
                PhotoStatus.PROCESSED
        );

        Set<ItemInventoryPhoto> results =
                dao.findByItemInventoryId(1);

        assertThat(results)
                .hasSize(2);

        assertThat(results)
                .extracting(ItemInventoryPhoto::getMd5)
                .containsExactlyInAnyOrder(
                        "md5-10",
                        "md5-11"
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void deleteByMd5_removes_photo() {

        dao.insertPhoto(
                1,
                "md5-delete",
                "delete.jpg",
                "bucket",
                "key",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        assertThat(dao.findByMd5("md5-delete"))
                .isPresent();

        dao.deleteByMd5("md5-delete");

        assertThat(dao.findByMd5("md5-delete"))
                .isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void deleteByMd5AndStorage_removesPhotoOnlyWhenStorageMatches() {

        dao.insertPhoto(
                1,
                "md5-storage-delete",
                "delete.jpg",
                "lego-photos-sandbox",
                "3001/uuid/md5-storage-delete.jpg",
                1000L,
                true,
                PhotoStatus.PROCESSED
        );

        int mismatchDeleted =
                dao.deleteByMd5AndStorage(
                        "md5-storage-delete",
                        "lego-photos-sandbox",
                        "3001/other/md5-storage-delete.jpg"
                );

        assertThat(mismatchDeleted)
                .isZero();

        assertThat(dao.findByMd5("md5-storage-delete"))
                .isPresent();

        int deleted =
                dao.deleteByMd5AndStorage(
                        "md5-storage-delete",
                        "lego-photos-sandbox",
                        "3001/uuid/md5-storage-delete.jpg"
                );

        assertThat(deleted)
                .isOne();

        assertThat(dao.findByMd5("md5-storage-delete"))
                .isEmpty();
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void replaceStoredObject_updatesStorageAndMetadataOnExistingPhoto() {

        ItemInventoryPhoto inserted =
                dao.insertPhoto(
                        1,
                        "md5-old",
                        "replace.jpg",
                        "bucket",
                        "old-key",
                        1000L,
                        false,
                        "Old caption",
                        PhotoStatus.PROCESSED
                );

        int updated =
                dao.replaceStoredObject(
                        inserted.getItemInventoryPhotoId(),
                        "replace.jpg",
                        "md5-new",
                        "lego-photos-sandbox",
                        "3001/uuid/md5-new.jpg",
                        2000L,
                        true,
                        "New caption",
                        PhotoStatus.PROCESSED
                );

        assertThat(updated)
                .isOne();

        ItemInventoryPhoto result =
                dao.findByMd5("md5-new").orElseThrow();

        assertThat(result)
                .extracting(
                        ItemInventoryPhoto::getMd5,
                        ItemInventoryPhoto::getFileName,
                        ItemInventoryPhoto::getS3Bucket,
                        ItemInventoryPhoto::getS3Key,
                        ItemInventoryPhoto::getFileSize,
                        ItemInventoryPhoto::getPrimary,
                        ItemInventoryPhoto::getCaption,
                        ItemInventoryPhoto::getStatus
                )
                .containsExactly(
                        "md5-new",
                        "replace.jpg",
                        "lego-photos-sandbox",
                        "3001/uuid/md5-new.jpg",
                        2000L,
                        true,
                        "New caption",
                        PhotoStatus.PROCESSED
                );
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void updateMetadata_updatesCaptionAndPrimaryWithoutChangingStorage() {

        ItemInventoryPhoto inserted =
                dao.insertPhoto(
                        1,
                        "md5-metadata",
                        "metadata.jpg",
                        "bucket",
                        "metadata-key",
                        1000L,
                        true,
                        "Old caption",
                        PhotoStatus.PROCESSED
                );

        int updated =
                dao.updateMetadata(
                        inserted.getItemInventoryPhotoId(),
                        "metadata.jpg",
                        false,
                        "New caption",
                        PhotoStatus.PROCESSED
                );

        assertThat(updated)
                .isOne();

        ItemInventoryPhoto result =
                dao.findByMd5("md5-metadata").orElseThrow();

        assertThat(result)
                .extracting(
                        ItemInventoryPhoto::getMd5,
                        ItemInventoryPhoto::getFileName,
                        ItemInventoryPhoto::getS3Bucket,
                        ItemInventoryPhoto::getS3Key,
                        ItemInventoryPhoto::getFileSize,
                        ItemInventoryPhoto::getPrimary,
                        ItemInventoryPhoto::getCaption
                )
                .containsExactly(
                        "md5-metadata",
                        "metadata.jpg",
                        "bucket",
                        "metadata-key",
                        1000L,
                        false,
                        "New caption"
                );
    }

    // =========================================================
    // DUPLICATE MD5
    // =========================================================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void insertPhoto_fails_when_md5_duplicate() {

        dao.insertPhoto(
                1,
                "duplicate-md5",
                "photo1.jpg",
                "bucket",
                "key1",
                1000L,
                false,
                PhotoStatus.PROCESSED
        );

        assertThatThrownBy(() ->
                dao.insertPhoto(
                        1,
                        "duplicate-md5",
                        "photo2.jpg",
                        "bucket",
                        "key2",
                        2000L,
                        false,
                        PhotoStatus.PROCESSED
                )
        ).isInstanceOf(Exception.class);
    }

    // =========================================================
    // CONFIG
    // =========================================================

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}