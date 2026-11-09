package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * An item, condition and sub-condition one environment needs priced (HOMELAB-31).
 * Table {@code marketplace_pricing.want_list_item}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WantListItem {
    private String environmentCode;
    private Integer canonicalItemId;
    private String conditionCode;
    private String completenessCode;
    private ZonedDateTime firstWantedAt;
    private ZonedDateTime lastConfirmedAt;
}
