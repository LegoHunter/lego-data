package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Where a lower-environment seeded item came from (HOMELAB-40).
 *
 * <p>A row exists only for items created by the seeding tool, never for real acquisitions, so it
 * scopes seeding teardown to seeded items. The unique key on {@code (sourceSystem, sourceKey)}
 * stops a re-run from seeding the same source item twice. PROD never holds rows.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInventorySeedSource {
    /** Item data copied from the legacy PROD database; the key is {@code bricklink_inventory.bl_inventory_id}. */
    public static final String SOURCE_SYSTEM_LEGO_PROD_LEGACY = "LEGO_PROD_LEGACY";

    private Integer itemInventoryId;
    private String sourceSystem;
    private String sourceKey;
    private String seedRunId;
    private ZonedDateTime seededAt;
}
