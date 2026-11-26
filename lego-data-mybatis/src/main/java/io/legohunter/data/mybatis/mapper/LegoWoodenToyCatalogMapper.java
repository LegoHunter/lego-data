package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.LegoWoodenToyCatalogItem;
import io.legohunter.data.dto.LegoWoodenToyCatalogItemImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * LEGO's wooden toys catalog: the detail row of a catalog entry and its pictures (HOMELAB-157).
 *
 * <p>Picture bytes are selected by {@link #findImageWithBytes} alone. Every other picture query uses
 * {@link #IMAGE_COLUMNS_WITHOUT_BYTES}.
 */
public interface LegoWoodenToyCatalogMapper {
    String ITEM_COLUMNS = """
            external_catalog_item_id,
            entry_source,
            product_number,
            product_number_sortable,
            alternate_number_text,
            original_title,
            english_title,
            launch_year_text,
            exit_year_text,
            average_size_text,
            series_text,
            notes_original,
            notes_english,
            source_version,
            source_updated_at,
            captured_at,
            created_at,
            updated_at
            """;

    String IMAGE_COLUMNS_WITHOUT_BYTES = """
            lego_wooden_toy_catalog_item_image_id,
            external_catalog_item_id,
            sort_order,
            image_source,
            content_type,
            file_name,
            width,
            height,
            byte_size,
            sha256,
            source_asset_id,
            source_url,
            source_note,
            created_at
            """;

    @Insert("""
            INSERT INTO lego_wooden_toy_catalog_item (external_catalog_item_id,
                                                      entry_source,
                                                      product_number,
                                                      product_number_sortable,
                                                      alternate_number_text,
                                                      original_title,
                                                      english_title,
                                                      launch_year_text,
                                                      exit_year_text,
                                                      average_size_text,
                                                      series_text,
                                                      notes_original,
                                                      notes_english,
                                                      source_version,
                                                      source_updated_at,
                                                      captured_at)
            VALUES (#{externalCatalogItemId},
                    #{entrySource},
                    #{productNumber},
                    #{productNumberSortable},
                    #{alternateNumberText},
                    #{originalTitle},
                    #{englishTitle},
                    #{launchYearText},
                    #{exitYearText},
                    #{averageSizeText},
                    #{seriesText},
                    #{notesOriginal},
                    #{notesEnglish},
                    #{sourceVersion},
                    #{sourceUpdatedAt},
                    #{capturedAt})
            ON DUPLICATE KEY UPDATE
                entry_source = VALUES(entry_source),
                product_number = VALUES(product_number),
                product_number_sortable = VALUES(product_number_sortable),
                alternate_number_text = VALUES(alternate_number_text),
                original_title = VALUES(original_title),
                english_title = VALUES(english_title),
                launch_year_text = VALUES(launch_year_text),
                exit_year_text = VALUES(exit_year_text),
                average_size_text = VALUES(average_size_text),
                series_text = VALUES(series_text),
                notes_original = VALUES(notes_original),
                notes_english = VALUES(notes_english),
                source_version = VALUES(source_version),
                source_updated_at = VALUES(source_updated_at),
                captured_at = VALUES(captured_at)
            """)
    int upsertItem(LegoWoodenToyCatalogItem item);

    @Select("SELECT " + ITEM_COLUMNS + " FROM lego_wooden_toy_catalog_item WHERE external_catalog_item_id = #{externalCatalogItemId}")
    @Results(id = "legoWoodenToyCatalogItemResultMap", value = {
            @Result(property = "externalCatalogItemId", column = "external_catalog_item_id", id = true),
            @Result(property = "entrySource", column = "entry_source"),
            @Result(property = "productNumber", column = "product_number"),
            @Result(property = "productNumberSortable", column = "product_number_sortable"),
            @Result(property = "alternateNumberText", column = "alternate_number_text"),
            @Result(property = "originalTitle", column = "original_title"),
            @Result(property = "englishTitle", column = "english_title"),
            @Result(property = "launchYearText", column = "launch_year_text"),
            @Result(property = "exitYearText", column = "exit_year_text"),
            @Result(property = "averageSizeText", column = "average_size_text"),
            @Result(property = "seriesText", column = "series_text"),
            @Result(property = "notesOriginal", column = "notes_original"),
            @Result(property = "notesEnglish", column = "notes_english"),
            @Result(property = "sourceVersion", column = "source_version"),
            @Result(property = "sourceUpdatedAt", column = "source_updated_at"),
            @Result(property = "capturedAt", column = "captured_at"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Optional<LegoWoodenToyCatalogItem> findItem(@Param("externalCatalogItemId") Integer externalCatalogItemId);

    /** Every entry that carries the Product Number. More than one is normal: LEGO reused numbers. */
    @Select("SELECT " + ITEM_COLUMNS + " FROM lego_wooden_toy_catalog_item WHERE product_number = #{productNumber} "
            + "ORDER BY launch_year_text, external_catalog_item_id")
    @ResultMap("legoWoodenToyCatalogItemResultMap")
    List<LegoWoodenToyCatalogItem> findItemsByProductNumber(@Param("productNumber") String productNumber);

    @Select("SELECT COUNT(*) FROM lego_wooden_toy_catalog_item WHERE entry_source = #{entrySource}")
    int countItemsBySource(@Param("entrySource") String entrySource);

    @Insert("""
            INSERT INTO lego_wooden_toy_catalog_item_image (external_catalog_item_id,
                                                            sort_order,
                                                            image_source,
                                                            image,
                                                            content_type,
                                                            file_name,
                                                            width,
                                                            height,
                                                            byte_size,
                                                            sha256,
                                                            source_asset_id,
                                                            source_url,
                                                            source_note)
            VALUES (#{externalCatalogItemId},
                    #{sortOrder},
                    #{imageSource},
                    #{image},
                    #{contentType},
                    #{fileName},
                    #{width},
                    #{height},
                    #{byteSize},
                    #{sha256},
                    #{sourceAssetId},
                    #{sourceUrl},
                    #{sourceNote})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "legoWoodenToyCatalogItemImageId",
            keyColumn = "lego_wooden_toy_catalog_item_image_id")
    int insertImage(LegoWoodenToyCatalogItemImage image);

    /** An entry's pictures in order, without their bytes. */
    @Select("SELECT " + IMAGE_COLUMNS_WITHOUT_BYTES + " FROM lego_wooden_toy_catalog_item_image "
            + "WHERE external_catalog_item_id = #{externalCatalogItemId} "
            + "ORDER BY sort_order, lego_wooden_toy_catalog_item_image_id")
    @Results(id = "legoWoodenToyCatalogItemImageResultMap", value = {
            @Result(property = "legoWoodenToyCatalogItemImageId", column = "lego_wooden_toy_catalog_item_image_id", id = true),
            @Result(property = "externalCatalogItemId", column = "external_catalog_item_id"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "imageSource", column = "image_source"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "width", column = "width"),
            @Result(property = "height", column = "height"),
            @Result(property = "byteSize", column = "byte_size"),
            @Result(property = "sha256", column = "sha256"),
            @Result(property = "sourceAssetId", column = "source_asset_id"),
            @Result(property = "sourceUrl", column = "source_url"),
            @Result(property = "sourceNote", column = "source_note"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<LegoWoodenToyCatalogItemImage> findImagesByItem(@Param("externalCatalogItemId") Integer externalCatalogItemId);

    /** The entry's picture with these bytes, without the bytes. Empty when the entry does not hold it yet. */
    @Select("SELECT " + IMAGE_COLUMNS_WITHOUT_BYTES + " FROM lego_wooden_toy_catalog_item_image "
            + "WHERE external_catalog_item_id = #{externalCatalogItemId} AND sha256 = #{sha256}")
    @ResultMap("legoWoodenToyCatalogItemImageResultMap")
    Optional<LegoWoodenToyCatalogItemImage> findImageByItemAndSha256(
            @Param("externalCatalogItemId") Integer externalCatalogItemId, @Param("sha256") String sha256);

    /** One picture with its bytes. The only query that reads the image column. */
    @Select("SELECT image, " + IMAGE_COLUMNS_WITHOUT_BYTES + " FROM lego_wooden_toy_catalog_item_image "
            + "WHERE lego_wooden_toy_catalog_item_image_id = #{imageId}")
    @Results(id = "legoWoodenToyCatalogItemImageWithBytesResultMap", value = {
            @Result(property = "legoWoodenToyCatalogItemImageId", column = "lego_wooden_toy_catalog_item_image_id", id = true),
            @Result(property = "externalCatalogItemId", column = "external_catalog_item_id"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "imageSource", column = "image_source"),
            @Result(property = "image", column = "image"),
            @Result(property = "contentType", column = "content_type"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "width", column = "width"),
            @Result(property = "height", column = "height"),
            @Result(property = "byteSize", column = "byte_size"),
            @Result(property = "sha256", column = "sha256"),
            @Result(property = "sourceAssetId", column = "source_asset_id"),
            @Result(property = "sourceUrl", column = "source_url"),
            @Result(property = "sourceNote", column = "source_note"),
            @Result(property = "createdAt", column = "created_at")
    })
    Optional<LegoWoodenToyCatalogItemImage> findImageWithBytes(@Param("imageId") Long imageId);

    /** Removes one picture, but only if it has the given source. Returns 1 when it was removed. */
    @Delete("DELETE FROM lego_wooden_toy_catalog_item_image "
            + "WHERE lego_wooden_toy_catalog_item_image_id = #{imageId} AND image_source = #{imageSource}")
    int deleteImage(@Param("imageId") Long imageId, @Param("imageSource") String imageSource);
}
