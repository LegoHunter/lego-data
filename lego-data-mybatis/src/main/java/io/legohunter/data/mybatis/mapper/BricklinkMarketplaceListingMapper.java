package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.BricklinkMarketplaceListing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BricklinkMarketplaceListingMapper {
    String ALL_COLUMNS = """
            marketplace_listing_id,
            bricklink_inventory_id,
            bricklink_inventory_status_code,
            bricklink_date_created,
            color_id,
            color_name,
            bind_id,
            bulk,
            is_retain,
            is_stock_room,
            stock_room_id,
            sale_rate,
            tier_quantity1,
            tier_price1,
            tier_quantity2,
            tier_price2,
            tier_quantity3,
            tier_price3,
            my_weight,
            remarks,
            last_remote_quantity,
            environment_code,
            system_remarks_hash,
            last_remote_verified_at,
            last_remote_safety_status_code,
            last_remote_safety_message,
            lot_image_md5,
            lot_image_synced_at
            """;

    /**
     * Records the photo now stored as the lot's custom image (HOMELAB-92), or clears it with nulls.
     * The only statement that writes these two columns: insert, update and upsert leave them alone.
     */
    @Update("""
            UPDATE bricklink_marketplace_listing
            SET lot_image_md5 = #{lotImageMd5},
                lot_image_synced_at = #{lotImageSyncedAt}
            WHERE marketplace_listing_id = #{marketplaceListingId}
            """)
    int updateLotImage(@Param("marketplaceListingId") Integer marketplaceListingId,
                       @Param("lotImageMd5") String lotImageMd5,
                       @Param("lotImageSyncedAt") ZonedDateTime lotImageSyncedAt);

    /**
     * The listings whose lot should get a new custom image (HOMELAB-92): ACTIVE, of this marketplace
     * and environment, with a lot, whose item has a processed primary photo, and whose recorded lot
     * image is not one of the item's processed primary photos.
     *
     * <p>Left out: a listing that already has a request of the given type waiting or in progress,
     * and one whose request of that type ended without success after {@code retryNotBefore}, so a
     * lot that cannot take its image is not queued again on every run.
     */
    @Select("""
            SELECT ml.marketplace_listing_id
            FROM marketplace_listing ml
            JOIN bricklink_marketplace_listing bml ON bml.marketplace_listing_id = ml.marketplace_listing_id
            WHERE ml.listing_status_code = 'ACTIVE'
              AND ml.listing_external_service_id = #{listingExternalServiceId}
              AND bml.bricklink_inventory_id IS NOT NULL
              AND bml.environment_code = #{environmentCode}
              AND EXISTS (
                    SELECT 1 FROM item_inventory_photo p
                    WHERE p.item_inventory_id = ml.item_inventory_id
                      AND p.is_primary = TRUE AND p.status = 'PROCESSED')
              AND NOT EXISTS (
                    SELECT 1 FROM item_inventory_photo p
                    WHERE p.item_inventory_id = ml.item_inventory_id
                      AND p.is_primary = TRUE AND p.status = 'PROCESSED'
                      AND p.md5 = bml.lot_image_md5)
              AND NOT EXISTS (
                    SELECT 1 FROM marketplace_listing_sync_request r
                    WHERE r.marketplace_listing_id = ml.marketplace_listing_id
                      AND r.sync_request_type_code = #{syncRequestTypeCode}
                      AND (r.sync_request_status_code IN ('PENDING', 'CLAIMED')
                           OR (r.sync_request_status_code <> 'SUCCEEDED' AND r.updated_at > #{retryNotBefore})))
            ORDER BY ml.marketplace_listing_id
            LIMIT #{limit}
            """)
    List<Integer> findMarketplaceListingIdsNeedingLotImage(@Param("listingExternalServiceId") Integer listingExternalServiceId,
                                                           @Param("environmentCode") String environmentCode,
                                                           @Param("syncRequestTypeCode") String syncRequestTypeCode,
                                                           @Param("retryNotBefore") ZonedDateTime retryNotBefore,
                                                           @Param("limit") int limit);

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Set<BricklinkMarketplaceListing> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Optional<BricklinkMarketplaceListing> findByMarketplaceListingId(Integer marketplaceListingId);

    @Select("SELECT " + ALL_COLUMNS + " FROM bricklink_marketplace_listing WHERE bricklink_inventory_id = #{bricklinkInventoryId}")
    @ResultMap("bricklinkMarketplaceListingResultMap")
    Optional<BricklinkMarketplaceListing> findByBricklinkInventoryId(Integer bricklinkInventoryId);

    @Insert("""
            INSERT INTO bricklink_marketplace_listing (
                marketplace_listing_id,
                bricklink_inventory_id,
                bricklink_inventory_status_code,
                bricklink_date_created,
                color_id,
                color_name,
                bind_id,
                bulk,
                is_retain,
                is_stock_room,
                stock_room_id,
                sale_rate,
                tier_quantity1,
                tier_price1,
                tier_quantity2,
                tier_price2,
                tier_quantity3,
                tier_price3,
                my_weight,
                remarks,
                last_remote_quantity,
                environment_code,
                system_remarks_hash,
                last_remote_verified_at,
                last_remote_safety_status_code,
                last_remote_safety_message
            )
            VALUES (
                #{marketplaceListingId},
                #{bricklinkInventoryId},
                #{bricklinkInventoryStatusCode},
                #{bricklinkDateCreated},
                #{colorId},
                #{colorName},
                #{bindId},
                #{bulk},
                #{isRetain},
                #{isStockRoom},
                #{stockRoomId},
                #{saleRate},
                #{tierQuantity1},
                #{tierPrice1},
                #{tierQuantity2},
                #{tierPrice2},
                #{tierQuantity3},
                #{tierPrice3},
                #{myWeight},
                #{remarks},
                #{lastRemoteQuantity},
                #{environmentCode},
                #{systemRemarksHash},
                #{lastRemoteVerifiedAt},
                #{lastRemoteSafetyStatusCode},
                #{lastRemoteSafetyMessage}
            )
            """)
    int insert(BricklinkMarketplaceListing bricklinkMarketplaceListing);

    @Update("""
            UPDATE bricklink_marketplace_listing
            SET bricklink_inventory_id = #{bricklinkInventoryId},
                bricklink_inventory_status_code = #{bricklinkInventoryStatusCode},
                bricklink_date_created = #{bricklinkDateCreated},
                color_id = #{colorId},
                color_name = #{colorName},
                bind_id = #{bindId},
                bulk = #{bulk},
                is_retain = #{isRetain},
                is_stock_room = #{isStockRoom},
                stock_room_id = #{stockRoomId},
                sale_rate = #{saleRate},
                tier_quantity1 = #{tierQuantity1},
                tier_price1 = #{tierPrice1},
                tier_quantity2 = #{tierQuantity2},
                tier_price2 = #{tierPrice2},
                tier_quantity3 = #{tierQuantity3},
                tier_price3 = #{tierPrice3},
                my_weight = #{myWeight},
                remarks = #{remarks},
                last_remote_quantity = #{lastRemoteQuantity},
                environment_code = #{environmentCode},
                system_remarks_hash = #{systemRemarksHash},
                last_remote_verified_at = #{lastRemoteVerifiedAt},
                last_remote_safety_status_code = #{lastRemoteSafetyStatusCode},
                last_remote_safety_message = #{lastRemoteSafetyMessage}
            WHERE marketplace_listing_id = #{marketplaceListingId}
            """)
    int update(BricklinkMarketplaceListing bricklinkMarketplaceListing);

    @Delete("DELETE FROM bricklink_marketplace_listing WHERE marketplace_listing_id = #{marketplaceListingId}")
    int delete(Integer marketplaceListingId);

    @Insert("""
            INSERT INTO bricklink_marketplace_listing (
                marketplace_listing_id,
                bricklink_inventory_id,
                bricklink_inventory_status_code,
                bricklink_date_created,
                color_id,
                color_name,
                bind_id,
                bulk,
                is_retain,
                is_stock_room,
                stock_room_id,
                sale_rate,
                tier_quantity1,
                tier_price1,
                tier_quantity2,
                tier_price2,
                tier_quantity3,
                tier_price3,
                my_weight,
                remarks,
                last_remote_quantity,
                environment_code,
                system_remarks_hash,
                last_remote_verified_at,
                last_remote_safety_status_code,
                last_remote_safety_message
            )
            VALUES (
                #{marketplaceListingId},
                #{bricklinkInventoryId},
                #{bricklinkInventoryStatusCode},
                #{bricklinkDateCreated},
                #{colorId},
                #{colorName},
                #{bindId},
                #{bulk},
                #{isRetain},
                #{isStockRoom},
                #{stockRoomId},
                #{saleRate},
                #{tierQuantity1},
                #{tierPrice1},
                #{tierQuantity2},
                #{tierPrice2},
                #{tierQuantity3},
                #{tierPrice3},
                #{myWeight},
                #{remarks},
                #{lastRemoteQuantity},
                #{environmentCode},
                #{systemRemarksHash},
                #{lastRemoteVerifiedAt},
                #{lastRemoteSafetyStatusCode},
                #{lastRemoteSafetyMessage}
            )
            ON DUPLICATE KEY UPDATE
                bricklink_inventory_id = VALUES(bricklink_inventory_id),
                bricklink_inventory_status_code = VALUES(bricklink_inventory_status_code),
                bricklink_date_created = VALUES(bricklink_date_created),
                color_id = VALUES(color_id),
                color_name = VALUES(color_name),
                bind_id = VALUES(bind_id),
                bulk = VALUES(bulk),
                is_retain = VALUES(is_retain),
                is_stock_room = VALUES(is_stock_room),
                stock_room_id = VALUES(stock_room_id),
                sale_rate = VALUES(sale_rate),
                tier_quantity1 = VALUES(tier_quantity1),
                tier_price1 = VALUES(tier_price1),
                tier_quantity2 = VALUES(tier_quantity2),
                tier_price2 = VALUES(tier_price2),
                tier_quantity3 = VALUES(tier_quantity3),
                tier_price3 = VALUES(tier_price3),
                my_weight = VALUES(my_weight),
                remarks = VALUES(remarks),
                last_remote_quantity = VALUES(last_remote_quantity),
                environment_code = VALUES(environment_code),
                system_remarks_hash = VALUES(system_remarks_hash),
                last_remote_verified_at = VALUES(last_remote_verified_at),
                last_remote_safety_status_code = VALUES(last_remote_safety_status_code),
                last_remote_safety_message = VALUES(last_remote_safety_message)
            """)
    int upsert(BricklinkMarketplaceListing bricklinkMarketplaceListing);
}
