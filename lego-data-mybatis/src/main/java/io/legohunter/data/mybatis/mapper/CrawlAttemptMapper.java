package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CrawlAttempt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/** Append-only log of crawl attempts (HOMELAB-31). Rows are finished in place, never deleted. */
public interface CrawlAttemptMapper {
    String ALL_COLUMNS = """
            crawl_attempt_id,
            crawl_target_id,
            started_at,
            finished_at,
            outcome_code,
            error_message,
            market_snapshot_id
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.crawl_attempt WHERE crawl_attempt_id = #{crawlAttemptId}")
    @ResultMap("crawlAttemptResultMap")
    Optional<CrawlAttempt> findByCrawlAttemptId(Long crawlAttemptId);

    /** The latest attempts for one target, newest first. */
    @Select("""
            SELECT ${columns}
            FROM ${marketplacePricingSchema}.crawl_attempt
            WHERE crawl_target_id = #{crawlTargetId}
            ORDER BY started_at DESC, crawl_attempt_id DESC
            LIMIT #{limit}
            """)
    @ResultMap("crawlAttemptResultMap")
    List<CrawlAttempt> findLatestByCrawlTargetId(
            @Param("crawlTargetId") Long crawlTargetId,
            @Param("limit") int limit,
            @Param("columns") String columns
    );

    default List<CrawlAttempt> findLatestByCrawlTargetId(Long crawlTargetId, int limit) {
        return findLatestByCrawlTargetId(crawlTargetId, limit, ALL_COLUMNS);
    }

    @Insert("""
            INSERT INTO ${marketplacePricingSchema}.crawl_attempt (
                crawl_target_id,
                started_at,
                finished_at,
                outcome_code,
                error_message,
                market_snapshot_id
            )
            VALUES (
                #{crawlTargetId},
                COALESCE(#{startedAt}, CURRENT_TIMESTAMP),
                #{finishedAt},
                #{outcomeCode},
                #{errorMessage},
                #{marketSnapshotId}
            )
            """)
    @Options(useGeneratedKeys = true, keyColumn = "crawl_attempt_id", keyProperty = "crawlAttemptId")
    void insert(CrawlAttempt crawlAttempt);

    /** Records how an attempt ended. */
    @Update("""
            UPDATE ${marketplacePricingSchema}.crawl_attempt
            SET finished_at = #{finishedAt},
                outcome_code = #{outcomeCode},
                error_message = #{errorMessage},
                market_snapshot_id = #{marketSnapshotId}
            WHERE crawl_attempt_id = #{crawlAttemptId}
            """)
    int finish(CrawlAttempt crawlAttempt);
}
