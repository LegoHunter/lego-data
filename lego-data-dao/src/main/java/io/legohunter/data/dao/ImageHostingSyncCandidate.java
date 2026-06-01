package io.legohunter.data.dao;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record ImageHostingSyncCandidate(
        Integer itemInventoryId,
        Set<ImageHostingSyncCandidateReason> reasons
) {
    public ImageHostingSyncCandidate {
        reasons = reasons == null || reasons.isEmpty()
                ? Set.of()
                : Collections.unmodifiableSet(EnumSet.copyOf(reasons));
    }

    static ImageHostingSyncCandidate of(Integer itemInventoryId, EnumSet<ImageHostingSyncCandidateReason> reasons) {
        return new ImageHostingSyncCandidate(itemInventoryId, reasons);
    }
}
