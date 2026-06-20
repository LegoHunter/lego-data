package io.legohunter.data.dao;

import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalImageDaoUnitTest {
    @Mock
    private ExternalImageMapper externalImageMapper;

    private ExternalImageDao externalImageDao;

    @BeforeEach
    void setUp() {
        externalImageDao = new ExternalImageDao(externalImageMapper);
    }

    @Test
    void findItemInventorySyncCandidatesMergesFocusedQueriesInPriorityOrderAndTracksReasons() {
        when(externalImageMapper.findItemInventoryIdsMissingAlbumLinks(10, 10))
                .thenReturn(List.of(100, 101));
        when(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 10))
                .thenReturn(List.of(101, 102));
        when(externalImageMapper.findItemInventoryIdsMissingAlbumMemberships(10, 10))
                .thenReturn(List.of(102, 103));
        when(externalImageMapper.findItemInventoryIdsWithFailedSyncRows(10, 10))
                .thenReturn(List.of(103, 104));
        when(externalImageMapper.findItemInventoryIdsWithPendingSyncRows(10, 10))
                .thenReturn(List.of(104, 105));
        when(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 10))
                .thenReturn(List.of(105, 106));

        List<ImageHostingSyncCandidate> candidates = externalImageDao.findItemInventorySyncCandidates(10, true, 10);

        assertThat(candidates)
                .extracting(ImageHostingSyncCandidate::itemInventoryId)
                .containsExactly(100, 101, 102, 103, 104, 105, 106);
        assertThat(candidates.get(1).reasons()).containsExactlyInAnyOrder(
                ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK,
                ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK
        );
        assertThat(candidates.get(2).reasons()).containsExactlyInAnyOrder(
                ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK,
                ImageHostingSyncCandidateReason.MISSING_ALBUM_MEMBERSHIP
        );
        assertThat(candidates.get(4).reasons()).containsExactlyInAnyOrder(
                ImageHostingSyncCandidateReason.FAILED_SYNC,
                ImageHostingSyncCandidateReason.PENDING_SYNC
        );
        verify(externalImageMapper).findItemInventoryIdsWithFailedSyncRows(10, 10);
    }

    @Test
    void findItemInventorySyncCandidatesAppliesLimitButStillAddsLaterReasonsForSelectedIds() {
        when(externalImageMapper.findItemInventoryIdsMissingAlbumLinks(10, 2))
                .thenReturn(List.of(100, 101));
        when(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 2))
                .thenReturn(List.of(101, 102));
        when(externalImageMapper.findItemInventoryIdsMissingAlbumMemberships(10, 2))
                .thenReturn(List.of(100, 103));
        when(externalImageMapper.findItemInventoryIdsWithPendingSyncRows(10, 2))
                .thenReturn(List.of(100, 104));
        when(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 2))
                .thenReturn(List.of(104));

        List<ImageHostingSyncCandidate> candidates = externalImageDao.findItemInventorySyncCandidates(10, false, 2);

        assertThat(candidates)
                .extracting(ImageHostingSyncCandidate::itemInventoryId)
                .containsExactly(100, 101);
        assertThat(candidates.get(0).reasons()).containsExactlyInAnyOrder(
                ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK,
                ImageHostingSyncCandidateReason.MISSING_ALBUM_MEMBERSHIP,
                ImageHostingSyncCandidateReason.PENDING_SYNC
        );
        assertThat(candidates.get(1).reasons()).containsExactlyInAnyOrder(
                ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK,
                ImageHostingSyncCandidateReason.MISSING_PHOTO_LINK
        );
    }

    @Test
    void findItemInventoryIdsNeedingSyncNormalizesInvalidLimitToOne() {
        when(externalImageMapper.findItemInventoryIdsMissingAlbumLinks(10, 1))
                .thenReturn(List.of(100, 101));
        when(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 1))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsMissingAlbumMemberships(10, 1))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsWithPendingSyncRows(10, 1))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 1))
                .thenReturn(List.of());

        assertThat(externalImageDao.findItemInventoryIdsNeedingSync(10, false, 0))
                .containsExactly(100);
    }

    @Test
    void findItemInventorySyncCandidatesSkipsFailedRowsWhenRetryFailedIsDisabled() {
        when(externalImageMapper.findItemInventoryIdsMissingAlbumLinks(10, 10))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsMissingExternalImageLinks(10, 10))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsMissingAlbumMemberships(10, 10))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsWithPendingSyncRows(10, 10))
                .thenReturn(List.of());
        when(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 10))
                .thenReturn(List.of());

        List<ImageHostingSyncCandidate> candidates = externalImageDao.findItemInventorySyncCandidates(10, false, 10);

        assertThat(candidates).isEmpty();
        verify(externalImageMapper, never()).findItemInventoryIdsWithFailedSyncRows(10, 10);
    }

    @Test
    void imageHostingSyncCandidateDefensivelyCopiesReasons() {
        EnumSet<ImageHostingSyncCandidateReason> reasons =
                EnumSet.of(ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK);

        ImageHostingSyncCandidate candidate = ImageHostingSyncCandidate.of(100, reasons);
        reasons.add(ImageHostingSyncCandidateReason.METADATA_CHANGED);

        assertThat(candidate.reasons()).containsExactly(ImageHostingSyncCandidateReason.MISSING_ALBUM_LINK);
        assertThat(new ImageHostingSyncCandidate(101, null).reasons()).isEqualTo(Set.of());
    }
}
