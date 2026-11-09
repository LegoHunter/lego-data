package io.legohunter.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * One crawl attempt, successful or not, in the append-only attempt log (HOMELAB-31).
 * Table {@code marketplace_pricing.crawl_attempt}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlAttempt {
    private Long crawlAttemptId;
    private Long crawlTargetId;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private String outcomeCode;
    private String errorMessage;
    private Long marketSnapshotId;
}
