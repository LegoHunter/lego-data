package io.legohunter.data.dao;

import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.ExternalCatalogItem;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.dto.LegoWoodenToyCatalogItem;
import io.legohunter.data.dto.LegoWoodenToyCatalogItemImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** LEGO's wooden toys catalog (HOMELAB-161): detail rows and pictures of entries of the LEGO service, item type W. */
@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class, LegoDataDaoConfiguration.class})
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/current_lookup_data.sql"
})
class LegoWoodenToyCatalogDaoTest {

    private static final int LEGO = ExternalService.Service.LEGO.getExternalServiceId();

    @Autowired ExternalCatalogItemDao externalCatalogItemDao;
    @Autowired ExternalServiceCapabilityDao externalServiceCapabilityDao;
    @Autowired LegoWoodenToyCatalogDao legoWoodenToyCatalogDao;

    @Test
    void theLegoServiceIsACatalog() {
        assertThat(LEGO).isEqualTo(1);
        assertThat(ExternalService.Service.LEGO.getServiceCode()).isEqualTo("LEGO");
        assertThat(externalServiceCapabilityDao.findByExternalServiceId(LEGO))
                .extracting(ExternalServiceCapability::getCapabilityCode).containsExactly("CATALOG");
    }

    @Test
    void anEntryKeepsEveryFieldAsLegoWroteIt() {
        ExternalCatalogItem entry = entry("330-ferry-1957", "bltferry", "Ferry", 1957);

        LegoWoodenToyCatalogItem stored = legoWoodenToyCatalogDao.upsertItem(LegoWoodenToyCatalogItem.builder()
                .externalCatalogItemId(entry.getExternalCatalogItemId())
                .entrySource(LegoWoodenToyCatalogItem.SOURCE_LEGO_COM)
                .productNumber("330")
                .productNumberSortable("0330")
                .alternateNumberText("Pr. August 1958: 1330. Pr. August 1959: BILOfix (samme nr,)")
                .originalTitle("Færge")
                .englishTitle("Ferry")
                .launchYearText("1957")
                .exitYearText("1958/1959")
                .averageSizeText("35 cm")
                .notesOriginal("Løse junger")
                .notesEnglish("Loose Milk Cans")
                .sourceVersion(4)
                .build());

        assertThat(stored.getProductNumber()).isEqualTo("330");
        assertThat(stored.getOriginalTitle()).isEqualTo("Færge");
        assertThat(stored.getExitYearText()).isEqualTo("1958/1959");
        assertThat(stored.getAlternateNumberText()).startsWith("Pr. August 1958: 1330");
        assertThat(stored.getNotesOriginal()).isEqualTo("Løse junger");
        assertThat(stored.getSeriesText()).isNull();
        assertThat(stored.getCreatedAt()).isNotNull();
        assertThat(externalCatalogItemDao.findByExternalServiceIdAndExternalItemKey(LEGO, "330-ferry-1957"))
                .get().extracting(ExternalCatalogItem::getItemTypeCode).isEqualTo(LegoWoodenToyCatalogItem.ITEM_TYPE_CODE);
    }

    @Test
    void aSecondCaptureReplacesTheFieldsOfTheSameEntry() {
        ExternalCatalogItem entry = entry("410-train-1943", "blt410", "Train", 1943);
        legoWoodenToyCatalogDao.upsertItem(item(entry, "410", "Tog", "Train", "1943").sourceVersion(3).build());

        LegoWoodenToyCatalogItem second = legoWoodenToyCatalogDao.upsertItem(
                item(entry, "410", "Tog", "Train (small)", "1943").sourceVersion(4).build());

        assertThat(second.getEnglishTitle()).isEqualTo("Train (small)");
        assertThat(second.getSourceVersion()).isEqualTo(4);
        assertThat(legoWoodenToyCatalogDao.findItemsByProductNumber("410")).hasSize(1);
    }

    @Test
    void oneProductNumberCanBeSeveralEntries() {
        // LEGO reused numbers: 330 is the 1957 ferry and also a 1943 train.
        ExternalCatalogItem ferry = entry("330-ferry-1957-b", "bltferryb", "Ferry", 1957);
        ExternalCatalogItem train = entry("330-train-1943", "blttrain", "Train", 1943);
        legoWoodenToyCatalogDao.upsertItem(item(ferry, "330x", "Færge", "Ferry", "1957").build());
        legoWoodenToyCatalogDao.upsertItem(item(train, "330x", "Tog", "Train", "1943").build());

        List<LegoWoodenToyCatalogItem> found = legoWoodenToyCatalogDao.findItemsByProductNumber("330x");

        assertThat(found).extracting(LegoWoodenToyCatalogItem::getEnglishTitle).containsExactly("Train", "Ferry");
        assertThat(legoWoodenToyCatalogDao.findItemsByProductNumber("no-such-number")).isEmpty();
    }

    @Test
    void anEntryAddedByHandIsMarkedCustom() {
        ExternalCatalogItem entry = entry("custom-wooden-crane", null, "Crane", null);
        legoWoodenToyCatalogDao.upsertItem(item(entry, "C-1", "Kran", "Crane", "1950")
                .entrySource(LegoWoodenToyCatalogItem.SOURCE_CUSTOM).build());

        assertThat(legoWoodenToyCatalogDao.findItem(entry.getExternalCatalogItemId()))
                .get().extracting(LegoWoodenToyCatalogItem::getEntrySource).isEqualTo(LegoWoodenToyCatalogItem.SOURCE_CUSTOM);
        assertThat(legoWoodenToyCatalogDao.countItemsBySource(LegoWoodenToyCatalogItem.SOURCE_CUSTOM)).isGreaterThanOrEqualTo(1);
    }

    @Test
    void aDetailRowNeedsItsCatalogEntry() {
        assertThatThrownBy(() -> legoWoodenToyCatalogDao.upsertItem(LegoWoodenToyCatalogItem.builder()
                .externalCatalogItemId(987_654_321)
                .entrySource(LegoWoodenToyCatalogItem.SOURCE_LEGO_COM)
                .productNumber("1")
                .build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void listingPicturesNeverLoadsTheirBytes() {
        ExternalCatalogItem entry = entry("310-animals-1945", "blt310", "Giraffe, Horse, Cow", 1945);
        legoWoodenToyCatalogDao.upsertItem(item(entry, "310", "Giraf, Hest, Ko", "Giraffe, Horse, Cow", "1945").build());
        byte[] horse = "horse picture bytes".getBytes(StandardCharsets.UTF_8);
        byte[] giraffe = "giraffe picture bytes".getBytes(StandardCharsets.UTF_8);
        legoWoodenToyCatalogDao.insertImage(image(entry, 1, "310_1_giraf.png", giraffe, sha('b')).build());
        LegoWoodenToyCatalogItemImage first = legoWoodenToyCatalogDao.insertImage(
                image(entry, 0, "310_1_hest.png", horse, sha('a')).sourceAssetId("blthorse").width(589).height(849).build());

        List<LegoWoodenToyCatalogItemImage> listed = legoWoodenToyCatalogDao.findImages(entry.getExternalCatalogItemId());

        assertThat(first.getLegoWoodenToyCatalogItemImageId()).isNotNull();
        assertThat(listed).extracting(LegoWoodenToyCatalogItemImage::getFileName)
                .containsExactly("310_1_hest.png", "310_1_giraf.png");
        assertThat(listed).allSatisfy(picture -> assertThat(picture.getImage()).isNull());
        assertThat(listed.get(0).getByteSize()).isEqualTo(horse.length);
        assertThat(listed.get(0).getWidth()).isEqualTo(589);
        assertThat(listed.get(0).getSourceAssetId()).isEqualTo("blthorse");

        LegoWoodenToyCatalogItemImage withBytes =
                legoWoodenToyCatalogDao.findImageWithBytes(first.getLegoWoodenToyCatalogItemImageId()).orElseThrow();
        assertThat(withBytes.getImage()).isEqualTo(horse);
        assertThat(withBytes.getContentType()).isEqualTo("image/png");
        assertThat(legoWoodenToyCatalogDao.findImageWithBytes(-1L)).isEmpty();
    }

    @Test
    void theSamePictureIsNotStoredTwiceForOneEntryButCanBelongToTwoEntries() {
        // LEGO attaches one group picture to several entries; each entry holds its own copy.
        ExternalCatalogItem cans2 = entry("2-milk-cans-medium-1949", "blt2", "Milk Cans (Medium)", 1949);
        ExternalCatalogItem cans3 = entry("3-milk-cans-small-1949", "blt3", "Milk Cans (Small)", 1949);
        legoWoodenToyCatalogDao.upsertItem(item(cans2, "2", "Junger", "Milk Cans (Medium)", "1949").build());
        legoWoodenToyCatalogDao.upsertItem(item(cans3, "3", "Junger", "Milk Cans (Small)", "1949").build());
        byte[] group = "group picture".getBytes(StandardCharsets.UTF_8);
        String checksum = sha('c');
        legoWoodenToyCatalogDao.insertImage(image(cans2, 1, "1_2_3_4.png", group, checksum).build());
        legoWoodenToyCatalogDao.insertImage(image(cans3, 1, "1_2_3_4.png", group, checksum).build());

        assertThat(legoWoodenToyCatalogDao.findImage(cans2.getExternalCatalogItemId(), checksum)).isPresent();
        assertThat(legoWoodenToyCatalogDao.findImage(cans2.getExternalCatalogItemId(), sha('d'))).isEmpty();
        assertThatThrownBy(() -> legoWoodenToyCatalogDao.insertImage(image(cans2, 2, "again.png", group, checksum).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void aCaptureCannotRemoveACustomPicture() {
        ExternalCatalogItem entry = entry("a-76-goods-train-1941", "blta76", "Goods Train", 1941);
        legoWoodenToyCatalogDao.upsertItem(item(entry, "A/76", "Gods-Tog", "Goods Train", "1941").build());
        LegoWoodenToyCatalogItemImage fromLego = legoWoodenToyCatalogDao.insertImage(
                image(entry, 0, "a76.png", "lego".getBytes(StandardCharsets.UTF_8), sha('e')).build());
        LegoWoodenToyCatalogItemImage custom = legoWoodenToyCatalogDao.insertImage(
                image(entry, 1, "community.jpg", "owner".getBytes(StandardCharsets.UTF_8), sha('f'))
                        .imageSource(LegoWoodenToyCatalogItemImage.SOURCE_CUSTOM)
                        .contentType("image/jpeg")
                        .sourceUrl("https://example.org/post/1")
                        .sourceNote("Posted in a collectors' group").build());

        assertThat(legoWoodenToyCatalogDao.deleteImage(custom.getLegoWoodenToyCatalogItemImageId(),
                LegoWoodenToyCatalogItemImage.SOURCE_LEGO_COM)).isFalse();
        assertThat(legoWoodenToyCatalogDao.deleteImage(fromLego.getLegoWoodenToyCatalogItemImageId(),
                LegoWoodenToyCatalogItemImage.SOURCE_LEGO_COM)).isTrue();

        List<LegoWoodenToyCatalogItemImage> left = legoWoodenToyCatalogDao.findImages(entry.getExternalCatalogItemId());
        assertThat(left).extracting(LegoWoodenToyCatalogItemImage::getFileName).containsExactly("community.jpg");
        assertThat(left.get(0).getImageSource()).isEqualTo(LegoWoodenToyCatalogItemImage.SOURCE_CUSTOM);
        assertThat(left.get(0).getSourceNote()).isEqualTo("Posted in a collectors' group");
    }

    private ExternalCatalogItem entry(String slug, String uid, String englishTitle, Integer year) {
        return externalCatalogItemDao.insert(ExternalCatalogItem.builder()
                .externalServiceId(LEGO)
                .externalItemKey(slug)
                .externalUniqueKey(uid)
                .itemName(englishTitle)
                .itemTypeCode(LegoWoodenToyCatalogItem.ITEM_TYPE_CODE)
                .itemUrl("https://www.lego.com/en-us/history/wooden-toys/" + slug)
                .yearReleased(year)
                .build());
    }

    private static LegoWoodenToyCatalogItem.LegoWoodenToyCatalogItemBuilder item(
            ExternalCatalogItem entry, String productNumber, String originalTitle, String englishTitle, String launchYear) {
        return LegoWoodenToyCatalogItem.builder()
                .externalCatalogItemId(entry.getExternalCatalogItemId())
                .entrySource(LegoWoodenToyCatalogItem.SOURCE_LEGO_COM)
                .productNumber(productNumber)
                .originalTitle(originalTitle)
                .englishTitle(englishTitle)
                .launchYearText(launchYear);
    }

    private static LegoWoodenToyCatalogItemImage.LegoWoodenToyCatalogItemImageBuilder image(
            ExternalCatalogItem entry, int sortOrder, String fileName, byte[] bytes, String sha256) {
        return LegoWoodenToyCatalogItemImage.builder()
                .externalCatalogItemId(entry.getExternalCatalogItemId())
                .sortOrder(sortOrder)
                .imageSource(LegoWoodenToyCatalogItemImage.SOURCE_LEGO_COM)
                .image(bytes)
                .contentType("image/png")
                .fileName(fileName)
                .byteSize((long) bytes.length)
                .sha256(sha256);
    }

    private static String sha(char digit) {
        return String.valueOf(digit).repeat(64);
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}
