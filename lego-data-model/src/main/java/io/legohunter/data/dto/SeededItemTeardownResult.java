package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** What the teardown of one seeded item removed: rows deleted per table, in delete order. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeededItemTeardownResult {
    private Integer itemInventoryId;
    @Builder.Default
    private Map<String, Integer> deletedRows = new LinkedHashMap<>();
    /** Acquisition transactions removed because their last item was this one. */
    private List<Long> deletedTransactionIds;
}
