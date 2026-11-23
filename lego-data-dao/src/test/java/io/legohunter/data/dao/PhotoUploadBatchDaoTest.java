package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.PhotoUploadBatch;
import io.legohunter.data.dto.PhotoUploadBatchFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {"/scripts/db/h2/photo_upload_batch_schema.ddl"})
class PhotoUploadBatchDaoTest {

    @Autowired PhotoUploadBatchDao photoUploadBatchDao;

    @Test
    void aPresentedBatchIsStoredWithItsFilesPending() {
        String batchId = UUID.randomUUID().toString();
        photoUploadBatchDao.insertBatch(PhotoUploadBatch.builder().batchId(batchId)
                .environment("sandbox").tokenName("owner-laptop").build(), List.of("a.jpg", "b.jpg"));

        assertThat(photoUploadBatchDao.findBatch(batchId)).isPresent();
        List<PhotoUploadBatchFile> files = photoUploadBatchDao.findFiles(batchId);
        assertThat(files).extracting(PhotoUploadBatchFile::getFileName).containsExactly("a.jpg", "b.jpg");
        assertThat(files).allSatisfy(f -> assertThat(f.getState()).isEqualTo(PhotoUploadBatchFile.PENDING));
    }

    @Test
    void aRejectedFileKeepsTheReasonFromIngest() {
        String batchId = UUID.randomUUID().toString();
        photoUploadBatchDao.insertBatch(PhotoUploadBatch.builder().batchId(batchId)
                .environment("sandbox").tokenName("owner-laptop").build(), List.of("bad.jpg"));

        assertThat(photoUploadBatchDao.recordOutcome(batchId, "bad.jpg", PhotoUploadBatchFile.REJECTED,
                "Photo metadata is missing required uuid", null, null)).isTrue();

        PhotoUploadBatchFile file = photoUploadBatchDao.findFiles(batchId).get(0);
        assertThat(file.getState()).isEqualTo(PhotoUploadBatchFile.REJECTED);
        assertThat(file.getReason()).isEqualTo("Photo metadata is missing required uuid");
    }

    @Test
    void anOutcomeIsRecordedOnlyOnce() {
        String batchId = UUID.randomUUID().toString();
        photoUploadBatchDao.insertBatch(PhotoUploadBatch.builder().batchId(batchId)
                .environment("sandbox").tokenName("owner-laptop").build(), List.of("c.jpg"));

        assertThat(photoUploadBatchDao.recordOutcome(batchId, "c.jpg", PhotoUploadBatchFile.ACCEPTED, null, 980, "abc")).isTrue();
        assertThat(photoUploadBatchDao.recordOutcome(batchId, "c.jpg", PhotoUploadBatchFile.REJECTED, "late", null, null)).isFalse();

        PhotoUploadBatchFile file = photoUploadBatchDao.findFiles(batchId).get(0);
        assertThat(file.getState()).isEqualTo(PhotoUploadBatchFile.ACCEPTED);
        assertThat(file.getItemInventoryPhotoId()).isEqualTo(980);
        assertThat(file.getMd5()).isEqualTo("abc");
    }

    @Test
    void anUnknownBatchFindsNothing() {
        assertThat(photoUploadBatchDao.findBatch(UUID.randomUUID().toString())).isEmpty();
        assertThat(photoUploadBatchDao.findFiles(UUID.randomUUID().toString())).isEmpty();
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}
