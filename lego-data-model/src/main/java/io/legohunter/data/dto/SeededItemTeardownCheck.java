package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * What stands in the way of removing a lower-environment seeded item's local rows (HOMELAB-40
 * teardown). Every count must be zero before the rows are deleted: a remote lot or an eBay listing
 * must be deleted on the marketplace first, an active sync request may still be creating one, and
 * photos, orders, sales and shipments are real history that teardown never erases.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeededItemTeardownCheck {
    private Integer itemInventoryId;
    /** 1 when the item has an {@code item_inventory_seed_source} row, else 0. */
    private Integer seeded;
    /** BrickLink listings that still hold a remote lot id. */
    private Integer bricklinkLots;
    /** eBay listings that still hold an eBay item id. */
    private Integer ebayItems;
    /** PENDING or CLAIMED sync requests on the item's listings. */
    private Integer activeSyncRequests;
    private Integer photos;
    private Integer imageAlbums;
    private Integer orderItems;
    private Integer orderTransactionLinks;
    private Integer revenues;
    private Integer shipments;

    public boolean isSeededItem() {
        return seeded != null && seeded > 0;
    }

    /** Human-readable reasons the item cannot be removed yet; empty when it can. */
    public List<String> blockers() {
        List<String> blockers = new ArrayList<>();
        if (!isSeededItem()) {
            blockers.add("not a seeded item");
        }
        add(blockers, bricklinkLots, "BrickLink lot(s) still exist; delete them first (LISTING_DELETE)");
        add(blockers, ebayItems, "eBay listing(s) still exist");
        add(blockers, activeSyncRequests, "active (PENDING or CLAIMED) sync request(s)");
        add(blockers, photos, "photo(s)");
        add(blockers, imageAlbums, "image album(s)");
        add(blockers, orderItems, "marketplace order item(s)");
        add(blockers, orderTransactionLinks, "marketplace order transaction link(s)");
        add(blockers, revenues, "sale revenue row(s)");
        add(blockers, shipments, "shipment row(s)");
        return blockers;
    }

    private static void add(List<String> blockers, Integer count, String what) {
        if (count != null && count > 0) {
            blockers.add(count + " " + what);
        }
    }
}
