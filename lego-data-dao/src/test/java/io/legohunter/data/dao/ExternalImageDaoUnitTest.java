package io.legohunter.data.dao;

import io.legohunter.data.mybatis.mapper.ExternalImageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    void findItemInventoryIdsNeedingSyncMergesFocusedQueriesInPriorityOrder() {
        when(externalImageMapper.findItemInventoryIdsMissingExternalImages(10, 10))
                .thenReturn(List.of(100, 101));
        when(externalImageMapper.findItemInventoryIdsMissingOrUnsyncedAlbums(10, 10))
                .thenReturn(List.of(101, 102));
        when(externalImageMapper.findItemInventoryIdsWithUnsyncedImages(10, true, 10))
                .thenReturn(List.of(102, 103));
        when(externalImageMapper.findItemInventoryIdsWithMetadataDrift(10, 10))
                .thenReturn(List.of(103, 104));

        assertThat(externalImageDao.findItemInventoryIdsNeedingSync(10, true, 10))
                .containsExactly(100, 101, 102, 103, 104);

        verify(externalImageMapper).findItemInventoryIdsWithUnsyncedImages(10, true, 10);
    }

    @Test
    void findItemInventoryIdsNeedingSyncAppliesLimitAndStopsWhenFull() {
        when(externalImageMapper.findItemInventoryIdsMissingExternalImages(10, 2))
                .thenReturn(List.of(100, 101));

        assertThat(externalImageDao.findItemInventoryIdsNeedingSync(10, false, 2))
                .containsExactly(100, 101);

        verify(externalImageMapper, never()).findItemInventoryIdsMissingOrUnsyncedAlbums(10, 2);
        verify(externalImageMapper, never()).findItemInventoryIdsWithUnsyncedImages(10, false, 2);
        verify(externalImageMapper, never()).findItemInventoryIdsWithMetadataDrift(10, 2);
    }

    @Test
    void findItemInventoryIdsNeedingSyncNormalizesInvalidLimitToOne() {
        when(externalImageMapper.findItemInventoryIdsMissingExternalImages(10, 1))
                .thenReturn(List.of(100, 101));

        assertThat(externalImageDao.findItemInventoryIdsNeedingSync(10, false, 0))
                .containsExactly(100);
    }
}
