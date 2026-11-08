package io.legohunter.data.dao;

import io.legohunter.data.dto.SeededItemTeardownCheck;
import io.legohunter.data.dto.SeededItemTeardownResult;
import io.legohunter.data.mybatis.mapper.SeededItemTeardownMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Removes every local row of a lower-environment seeded item (HOMELAB-40 teardown).
 *
 * <p>Only items with an {@code item_inventory_seed_source} row qualify, and only when
 * {@link SeededItemTeardownCheck#blockers()} is empty: no remote BrickLink lot or eBay listing, no
 * active sync request, no photos, orders, sales or shipments. The acquisition transaction is
 * removed with its last item. The caller must run {@link #teardown} in a transaction, and must
 * refuse it in PROD (PROD never holds seed-source rows, so it would refuse anyway).
 */
@Component
@RequiredArgsConstructor
public class SeededItemTeardownDao {
    private final SeededItemTeardownMapper mapper;

    public SeededItemTeardownCheck check(Integer itemInventoryId) {
        return mapper.check(itemInventoryId);
    }

    /**
     * Deletes the item's rows in dependency order. Throws {@link IllegalStateException} and deletes
     * nothing if the item is not seeded or has a blocker.
     */
    public SeededItemTeardownResult teardown(Integer itemInventoryId) {
        SeededItemTeardownCheck check = mapper.check(itemInventoryId);
        List<String> blockers = check.blockers();
        if (!blockers.isEmpty()) {
            throw new IllegalStateException("Item inventory " + itemInventoryId + " cannot be torn down: " + String.join("; ", blockers));
        }

        Map<String, Integer> deleted = new LinkedHashMap<>();
        step(deleted, "marketplace_listing_sync_request", () -> mapper.deleteSyncRequests(itemInventoryId));
        step(deleted, "pricing_apply_readiness", () -> mapper.deletePricingApplyReadiness(itemInventoryId));
        step(deleted, "pricing_decision", () -> mapper.deletePricingDecisions(itemInventoryId));
        step(deleted, "pricing_snapshot_listing", () -> mapper.deletePricingSnapshotListings(itemInventoryId));
        step(deleted, "pricing_snapshot", () -> mapper.deletePricingSnapshots(itemInventoryId));
        step(deleted, "pricing_crawl_work_item", () -> mapper.deletePricingCrawlWorkItems(itemInventoryId));
        step(deleted, "bricklink_marketplace_listing", () -> mapper.deleteBricklinkListings(itemInventoryId));
        step(deleted, "ebay_listing_item_specific", () -> mapper.deleteEbayItemSpecifics(itemInventoryId));
        step(deleted, "ebay_marketplace_listing", () -> mapper.deleteEbayListings(itemInventoryId));
        step(deleted, "marketplace_listing", () -> mapper.deleteMarketplaceListings(itemInventoryId));
        step(deleted, "item_inventory_external_catalog_item", () -> mapper.deleteCatalogLinks(itemInventoryId));
        step(deleted, "transaction_item_cost", () -> mapper.deleteTransactionItemCosts(itemInventoryId));
        List<Long> transactionIds = mapper.findTransactionIds(itemInventoryId);
        step(deleted, "transaction_item", () -> mapper.deleteTransactionItems(itemInventoryId));
        step(deleted, "item_inventory_seed_source", () -> mapper.deleteSeedSource(itemInventoryId));
        step(deleted, "item_inventory", () -> mapper.deleteItemInventory(itemInventoryId));

        List<Long> deletedTransactionIds = new ArrayList<>();
        for (Long transactionId : transactionIds) {
            step(deleted, "payment", () -> mapper.deleteEmptyTransactionPayments(transactionId));
            step(deleted, "transaction_cost", () -> mapper.deleteEmptyTransactionCosts(transactionId));
            step(deleted, "transaction_party_snapshot", () -> mapper.deleteEmptyTransactionPartySnapshots(transactionId));
            if (mapper.deleteEmptyTransaction(transactionId) > 0) {
                deletedTransactionIds.add(transactionId);
                deleted.merge("transactions", 1, Integer::sum);
            }
        }
        if (deleted.getOrDefault("item_inventory", 0) != 1) {
            throw new IllegalStateException("Item inventory " + itemInventoryId + " was not deleted: " + deleted);
        }
        return SeededItemTeardownResult.builder()
                .itemInventoryId(itemInventoryId)
                .deletedRows(deleted)
                .deletedTransactionIds(deletedTransactionIds)
                .build();
    }

    private static void step(Map<String, Integer> deleted, String table, IntSupplier delete) {
        int count = delete.getAsInt();
        if (count > 0) {
            deleted.merge(table, count, Integer::sum);
        }
    }
}
