package net.lego.data.v2.dao;

import lombok.extern.slf4j.Slf4j;
import net.lego.data.config.MyBatisV2Configuration;
import net.lego.data.v2.dto.ItemInventoryPhoto;
import net.lego.data.v2.enums.PhotoStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static net.lego.data.v2.enums.PhotoStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class})
@Slf4j
class ItemInventoryPhotoDaoTest {

    @Autowired
    ItemInventoryPhotoDao dao;
    @Autowired
    private ItemInventoryPhotoDao itemInventoryPhotoDao;

    // =========================
    // INSERT + EXISTS
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void insert_and_exists() {

        dao.insertPhoto(1, "md5-1", "file.jpg", false, UPLOADED);

        assertThat(dao.existsByMd5("md5-1"))
                .isTrue();
    }

    // =========================
    // FIND BY MD5
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByMd5() {

        dao.insertPhoto(1, "md5-2", "file.jpg", false, UPLOADED);

        Optional<ItemInventoryPhoto> result = dao.findByMd5("md5-2");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(ItemInventoryPhoto::getMd5)
                .isEqualTo("md5-2");
    }

    // =========================
    // STATE TRANSITIONS
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void transitionStatus_success() {

        dao.insertPhoto(1, "md5-3", "file.jpg", false, UPLOADED);

        int updated = dao.transitionStatus(
                "md5-3",
                UPLOADED,
                PhotoStatus.PROCESSED
        );

        assertThat(updated).isEqualTo(1);

        Optional<ItemInventoryPhoto> photo = dao.findByMd5("md5-3");

        assertThat(photo)
                .isPresent()
                .get()
                .extracting(ItemInventoryPhoto::getStatus)
                .isEqualTo(PhotoStatus.PROCESSED);
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findAll() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(3, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(4, "md5-4", "file4.jpg", false, UPLOADED);
        dao.insertPhoto(5, "md5-5", "file5.jpg", false, UPLOADED);

        Set<ItemInventoryPhoto> itemInventoryPhotos = dao.findAll();
        assertThat(itemInventoryPhotos).hasSize(5);

    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByItemInventoryPhotoId_whenFound() {
        ItemInventoryPhoto itemInventoryPhoto = dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        Integer itemInventoryPhotoId = itemInventoryPhoto.getItemInventoryPhotoId();
        assertThat(dao.findByItemInventoryPhotoId(itemInventoryPhotoId)).isPresent();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByItemInventoryPhotoId_whenNotFound() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        assertThat(dao.findByItemInventoryPhotoId(2)).isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByUuid_whenFound() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        assertThat(dao.findByUuid("md5-1")).isPresent();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByUuid_whenNotFound() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        assertThat(dao.findByUuid("md5-2")).isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByItemInventoryId() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-4", "file4.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-5", "file5.jpg", false, UPLOADED);

        assertThat(dao.findByItemInventoryId(1)).hasSize(3);
        assertThat(dao.findByItemInventoryId(2)).hasSize(2);
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findPrimaryByItemInventoryId_noPrimaryExists() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-4", "file4.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-5", "file5.jpg", false, UPLOADED);

        assertThat(dao.findPrimaryByItemInventoryId(1)).isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findPrimaryByItemInventoryId_primaryExists() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-4", "file4.jpg", true, UPLOADED);
        dao.insertPhoto(1, "md5-5", "file5.jpg", false, UPLOADED);

        assertThat(dao.findPrimaryByItemInventoryId(1)).isPresent().hasValueSatisfying(itemInventoryPhoto -> {
            assertThat(itemInventoryPhoto.getMd5()).isEqualTo("md5-4");
            assertThat(itemInventoryPhoto.getFileName()).isEqualTo("file4.jpg");
        });
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void countByStatus() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, PROCESSED);
        dao.insertPhoto(1, "md5-3", "file3.jpg", false, PROCESSED);
        dao.insertPhoto(2, "md5-4", "file4.jpg", true, UPLOADED);
        dao.insertPhoto(2, "md5-5", "file5.jpg", false, FAILED);
        assertThat(dao.countByStatus(UPLOADED)).isEqualTo(2);
        assertThat(dao.countByStatus(PROCESSED)).isEqualTo(2);
        assertThat(dao.countByStatus(FAILED)).isEqualTo(1);
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void countByStatus_zeroForStatus() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(3, "md5-4", "file4.jpg", true, UPLOADED);
        dao.insertPhoto(3, "md5-5", "file5.jpg", false, UPLOADED);
        assertThat(dao.countByStatus(UPLOADED)).isEqualTo(5);
        assertThat(dao.countByStatus(PROCESSED)).isEqualTo(0);
        assertThat(dao.countByStatus(FAILED)).isEqualTo(0);
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findFailed() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, FAILED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-3", "file3.jpg", false, UPLOADED);
        dao.insertPhoto(3, "md5-4", "file4.jpg", true, FAILED);
        dao.insertPhoto(3, "md5-5", "file5.jpg", false, UPLOADED);
        assertThat(dao.findFailed()).hasSize(2);
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void insert() {
        ItemInventoryPhoto itemInventoryPhoto = ItemInventoryPhoto.builder()
                .itemInventoryId(2)
                .s3Bucket("bucket-one")
                .s3Key("key-123")
                .md5("md5-1")
                .fileName("thefile")
                .fileSize(123847619823L)
                .primary(true)
                .caption("The caption")
                .status(UPLOADED)
                .createdAt(ZonedDateTime.now())
                .uploadedAt(ZonedDateTime.now())
                .build();
        ItemInventoryPhoto newItemInventoryPhoto = dao.insert(itemInventoryPhoto);
        assertThat(newItemInventoryPhoto.getItemInventoryPhotoId()).isNotNull().isGreaterThan(0);
        assertThat(newItemInventoryPhoto.getItemInventoryPhotoId()).isEqualTo(itemInventoryPhoto.getItemInventoryPhotoId());
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void update() {

    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void transitionStatus_fails_when_invalid_from_state() {

        dao.insertPhoto(1, "md5-4", "file.jpg", false, UPLOADED);

        assertThatThrownBy(() ->
                dao.transitionStatus(
                        "md5-4",
                        FAILED,
                        PhotoStatus.PROCESSED
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("md5-4");

        assertThat(dao.findByMd5("md5-4"))
                .isPresent()
                .get()
                .extracting(ItemInventoryPhoto::getStatus)
                .isEqualTo(UPLOADED);
    }

    // =========================
    // MARK UPLOADED (S3 FIELDS)
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void markUploaded_success() {

        dao.insertPhoto(1, "md5-5", "file.jpg", false, UPLOADED);

        int updated = dao.markUploaded(
                "md5-5",
                "bucket",
                "photos/1.jpg",
                2048L
        );

        assertThat(updated).isEqualTo(1);

        assertThat(dao.findByMd5("md5-5"))
                .isPresent()
                .get()
                .satisfies(photo -> {
                    assertThat(photo.getS3Bucket()).isEqualTo("bucket");
                    assertThat(photo.getS3Key()).isEqualTo("photos/1.jpg");
                    assertThat(photo.getFileSize()).isEqualTo(2048L);
                });
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void markUploaded_fails_if_wrong_state() {

        ItemInventoryPhoto itemInventoryPhoto = dao.insertPhoto(1, "md5-6", "file.jpg", false, FAILED);

        int updated = dao.markUploaded(
                "md5-6",
                "bucket",
                "photos/1.jpg",
                2048L
        );

        assertThat(updated).isZero();
    }

    // =========================
    // PRIMARY PHOTO (ATOMIC)
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void setPrimaryForItem_atomic() {

        dao.insertPhoto(1, "md5-a", "a.jpg", false, PhotoStatus.PROCESSED);
        dao.insertPhoto(1, "md5-b", "b.jpg", false, PhotoStatus.PROCESSED);

        dao.setPrimaryForItem(1, "md5-a");

        assertThat(dao.findByMd5("md5-a"))
                .isPresent()
                .get()
                .extracting(ItemInventoryPhoto::getPrimary)
                .isEqualTo(true);

        assertThat(dao.findByMd5("md5-b"))
                .isPresent()
                .get()
                .extracting(ItemInventoryPhoto::getPrimary)
                .isEqualTo(false);
    }

    // =========================
    // FIND BY STATUS
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findByStatus() {

        dao.insertPhoto(1, "md5-x", "x.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-y", "y.jpg", false, FAILED);

        assertThat(dao.findByStatus(UPLOADED))
                .extracting(ItemInventoryPhoto::getMd5)
                .contains("md5-x")
                .doesNotContain("md5-y");
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void findWithoutS3Key() {
        dao.insertPhoto(1, "md5-1", "file1.jpg", false, UPLOADED);
        dao.insertPhoto(1, "md5-2", "file2.jpg", false, UPLOADED);
        dao.insertPhoto(2, "md5-3", "file3.jpg", false, FAILED);
        dao.insertPhoto(3, "md5-4", "file4.jpg", true, PROCESSED);
        dao.insertPhoto(3, "md5-5", "file5.jpg", false, UPLOADED);

        assertThat(dao.findWithoutS3Key()).hasSize(5);

        dao.findByMd5("md5-1").map(iip -> dao.markUploaded("md5-1", "bucket-1", "key-1", 761823L));
        dao.findByMd5("md5-2").map(iip -> dao.markUploaded("md5-2", "bucket-2", "key-2", 761823L));
        assertThat(dao.findWithoutS3Key()).hasSize(3);

        dao.findByMd5("md5-3").map(iip -> dao.markUploaded("md5-3", "bucket-3", "key-3", 761823L));
        assertThat(dao.findWithoutS3Key()).hasSize(3);
    }

    // =========================
    // DELETE
    // =========================

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_photo_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory-photo.sql"
    })
    void deleteByMd5() {

        dao.insertPhoto(1, "md5-del", "file.jpg", false, UPLOADED);

        int deleted = dao.deleteByMd5("md5-del");

        assertThat(deleted).isEqualTo(1);

        assertThat(dao.existsByMd5("md5-del")).isFalse();
    }

    // =========================
    // CONFIG
    // =========================

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}