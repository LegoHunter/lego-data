package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.SeededItemTeardownCheck;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Removes the local rows of a lower-environment seeded item (HOMELAB-40 teardown).
 *
 * <p>Every statement up to the seed-source row is scoped through {@code item_inventory_seed_source},
 * so it can only ever touch an item the seeding tool created: called with any other item id, it
 * deletes nothing. The statements must run in the order {@code SeededItemTeardownDao} uses, inside
 * one transaction, after {@link #check} reported no blockers.
 */
public interface SeededItemTeardownMapper {
    /** The item's listings, only if the item is seeded. */
    String SEEDED_LISTINGS = """
            SELECT ml.marketplace_listing_id
            FROM marketplace_listing ml
            JOIN item_inventory_seed_source iiss ON iiss.item_inventory_id = ml.item_inventory_id
            WHERE ml.item_inventory_id = #{itemInventoryId}
            """;

    /** The item's transaction items, only if the item is seeded. */
    String SEEDED_TRANSACTION_ITEMS = """
            SELECT ti.transaction_item_id
            FROM transaction_item ti
            JOIN item_inventory_seed_source iiss ON iiss.item_inventory_id = ti.item_inventory_id
            WHERE ti.item_inventory_id = #{itemInventoryId}
            """;

    String IS_SEEDED = """
            EXISTS (SELECT 1 FROM item_inventory_seed_source iiss WHERE iiss.item_inventory_id = #{itemInventoryId})
            """;

    @Select("""
            SELECT #{itemInventoryId} AS itemInventoryId,
                   (SELECT COUNT(*) FROM item_inventory_seed_source s WHERE s.item_inventory_id = #{itemInventoryId}) AS seeded,
                   (SELECT COUNT(*) FROM bricklink_marketplace_listing bml
                      JOIN marketplace_listing ml ON ml.marketplace_listing_id = bml.marketplace_listing_id
                     WHERE ml.item_inventory_id = #{itemInventoryId} AND bml.bricklink_inventory_id IS NOT NULL) AS bricklinkLots,
                   (SELECT COUNT(*) FROM ebay_marketplace_listing eml
                      JOIN marketplace_listing ml ON ml.marketplace_listing_id = eml.marketplace_listing_id
                     WHERE ml.item_inventory_id = #{itemInventoryId} AND eml.ebay_item_id IS NOT NULL) AS ebayItems,
                   (SELECT COUNT(*) FROM marketplace_listing_sync_request r
                      JOIN marketplace_listing ml ON ml.marketplace_listing_id = r.marketplace_listing_id
                     WHERE ml.item_inventory_id = #{itemInventoryId} AND r.sync_request_status_code IN ('PENDING', 'CLAIMED')) AS activeSyncRequests,
                   (SELECT COUNT(*) FROM item_inventory_photo p WHERE p.item_inventory_id = #{itemInventoryId}) AS photos,
                   (SELECT COUNT(*) FROM external_image_album a WHERE a.item_inventory_id = #{itemInventoryId}) AS imageAlbums,
                   (SELECT COUNT(*) FROM marketplace_order_item oi
                     WHERE oi.item_inventory_id = #{itemInventoryId}
                        OR oi.marketplace_listing_id IN (SELECT ml.marketplace_listing_id FROM marketplace_listing ml WHERE ml.item_inventory_id = #{itemInventoryId})) AS orderItems,
                   (SELECT COUNT(*) FROM marketplace_order_transaction_link l
                      JOIN transaction_item ti ON ti.transaction_item_id = l.transaction_item_id
                     WHERE ti.item_inventory_id = #{itemInventoryId}) AS orderTransactionLinks,
                   (SELECT COUNT(*) FROM transaction_item_revenue rv
                      JOIN transaction_item ti ON ti.transaction_item_id = rv.transaction_item_id
                     WHERE ti.item_inventory_id = #{itemInventoryId}) AS revenues,
                   (SELECT COUNT(*) FROM transaction_item_shipment sh
                      JOIN transaction_item ti ON ti.transaction_item_id = sh.item_transaction_id
                     WHERE ti.item_inventory_id = #{itemInventoryId}) AS shipments
            """)
    SeededItemTeardownCheck check(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM marketplace_listing_sync_request WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deleteSyncRequests(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM pricing_apply_readiness WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deletePricingApplyReadiness(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM pricing_decision WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deletePricingDecisions(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("""
            DELETE FROM pricing_snapshot_listing
            WHERE pricing_snapshot_id IN (SELECT ps.pricing_snapshot_id
                                          FROM pricing_snapshot ps
                                          WHERE ps.marketplace_listing_id IN (""" + SEEDED_LISTINGS + "))")
    int deletePricingSnapshotListings(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM pricing_snapshot WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deletePricingSnapshots(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM pricing_crawl_work_item WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deletePricingCrawlWorkItems(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM bricklink_marketplace_listing WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deleteBricklinkListings(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM ebay_listing_item_specific WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deleteEbayItemSpecifics(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM ebay_marketplace_listing WHERE marketplace_listing_id IN (" + SEEDED_LISTINGS + ")")
    int deleteEbayListings(@Param("itemInventoryId") Integer itemInventoryId);

    // MySQL cannot delete from a table it also reads in a subquery, so this one checks the
    // seed-source row directly instead of going through SEEDED_LISTINGS.
    @Delete("DELETE FROM marketplace_listing WHERE item_inventory_id = #{itemInventoryId} AND " + IS_SEEDED)
    int deleteMarketplaceListings(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM item_inventory_external_catalog_item WHERE item_inventory_id = #{itemInventoryId} AND " + IS_SEEDED)
    int deleteCatalogLinks(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM transaction_item_cost WHERE transaction_item_id IN (" + SEEDED_TRANSACTION_ITEMS + ")")
    int deleteTransactionItemCosts(@Param("itemInventoryId") Integer itemInventoryId);

    @Select("SELECT DISTINCT ti.transaction_id FROM transaction_item ti WHERE ti.transaction_item_id IN (" + SEEDED_TRANSACTION_ITEMS + ") ORDER BY ti.transaction_id")
    List<Long> findTransactionIds(@Param("itemInventoryId") Integer itemInventoryId);

    @Delete("DELETE FROM transaction_item WHERE item_inventory_id = #{itemInventoryId} AND " + IS_SEEDED)
    int deleteTransactionItems(@Param("itemInventoryId") Integer itemInventoryId);

    /** Last scoped statement: after it, the item is no longer recognisable as seeded. */
    @Delete("DELETE FROM item_inventory_seed_source WHERE item_inventory_id = #{itemInventoryId}")
    int deleteSeedSource(@Param("itemInventoryId") Integer itemInventoryId);

    /** Only after deleteSeedSource, in the same transaction that checked the item was seeded. */
    @Delete("DELETE FROM item_inventory WHERE item_inventory_id = #{itemInventoryId}")
    int deleteItemInventory(@Param("itemInventoryId") Integer itemInventoryId);

    // An acquisition transaction goes only once it has no items left, so a transaction that also
    // holds a real (unseeded) item is never touched.
    String TRANSACTION_IS_EMPTY = """
            NOT EXISTS (SELECT 1 FROM transaction_item ti WHERE ti.transaction_id = #{transactionId})
            """;

    @Delete("DELETE FROM payment WHERE transaction_id = #{transactionId} AND " + TRANSACTION_IS_EMPTY)
    int deleteEmptyTransactionPayments(@Param("transactionId") Long transactionId);

    @Delete("DELETE FROM transaction_cost WHERE transaction_id = #{transactionId} AND " + TRANSACTION_IS_EMPTY)
    int deleteEmptyTransactionCosts(@Param("transactionId") Long transactionId);

    @Delete("DELETE FROM transaction_party_snapshot WHERE transaction_id = #{transactionId} AND " + TRANSACTION_IS_EMPTY)
    int deleteEmptyTransactionPartySnapshots(@Param("transactionId") Long transactionId);

    @Delete("DELETE FROM transactions WHERE transaction_id = #{transactionId} AND " + TRANSACTION_IS_EMPTY)
    int deleteEmptyTransaction(@Param("transactionId") Long transactionId);
}
