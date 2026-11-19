package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row of the Extended Description comparison (HOMELAB-82): a BrickLink listing with a lot, the
 * hash of the Extended Description last stored on that lot, and one captioned photo of its item.
 * A listing whose item has no captioned photo appears once, with a null photo id and caption.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BricklinkListingCaption {
    private Integer marketplaceListingId;
    private String extendedDescriptionHash;
    private Integer itemInventoryPhotoId;
    private String caption;
}
