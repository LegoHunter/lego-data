package io.legohunter.data.dao;

import io.legohunter.data.dto.PartyExternalIdentity;
import io.legohunter.data.dto.Shipment;
import io.legohunter.data.dto.TransactionItemRevenue;
import io.legohunter.data.dto.TransactionPartySnapshot;
import io.legohunter.data.mybatis.mapper.PartyExternalIdentityMapper;
import io.legohunter.data.mybatis.mapper.ShipmentMapper;
import io.legohunter.data.mybatis.mapper.TransactionItemRevenueMapper;
import io.legohunter.data.mybatis.mapper.TransactionPartySnapshotMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Phase1AccountingDaoUnitTest {
    @Test
    void partyExternalIdentityDaoDelegatesCrud() {
        PartyExternalIdentityMapper mapper = mock(PartyExternalIdentityMapper.class);
        PartyExternalIdentityDao dao = new PartyExternalIdentityDao(mapper);
        PartyExternalIdentity value = PartyExternalIdentity.builder().partyExternalIdentityId(1L).build();
        when(mapper.findAll()).thenReturn(List.of(value));
        when(mapper.findByPartyExternalIdentityId(1L)).thenReturn(Optional.of(value));

        assertThat(dao.insert(value)).isSameAs(value);
        assertThat(dao.upsert(value)).isSameAs(value);
        assertThat(dao.findAll()).containsExactly(value);
        assertThat(dao.findByPartyExternalIdentityId(1L)).contains(value);
        dao.delete(1L);
        verify(mapper).insert(value); verify(mapper).upsert(value); verify(mapper).delete(1L);
    }

    @Test
    void transactionPartySnapshotDaoDelegatesCrud() {
        TransactionPartySnapshotMapper mapper = mock(TransactionPartySnapshotMapper.class);
        TransactionPartySnapshotDao dao = new TransactionPartySnapshotDao(mapper);
        TransactionPartySnapshot value = TransactionPartySnapshot.builder().transactionPartySnapshotId(1L).build();
        when(mapper.findAll()).thenReturn(List.of(value));
        when(mapper.findByTransactionPartySnapshotId(1L)).thenReturn(Optional.of(value));

        assertThat(dao.insert(value)).isSameAs(value);
        assertThat(dao.upsert(value)).isSameAs(value);
        assertThat(dao.findAll()).containsExactly(value);
        assertThat(dao.findByTransactionPartySnapshotId(1L)).contains(value);
        dao.delete(1L);
        verify(mapper).insert(value); verify(mapper).upsert(value); verify(mapper).delete(1L);
    }

    @Test
    void transactionItemRevenueDaoDelegatesCrud() {
        TransactionItemRevenueMapper mapper = mock(TransactionItemRevenueMapper.class);
        TransactionItemRevenueDao dao = new TransactionItemRevenueDao(mapper);
        TransactionItemRevenue value = TransactionItemRevenue.builder().transactionItemRevenueId(1L).build();
        when(mapper.findAll()).thenReturn(List.of(value));
        when(mapper.findByTransactionItemRevenueId(1L)).thenReturn(Optional.of(value));

        assertThat(dao.insert(value)).isSameAs(value);
        assertThat(dao.upsert(value)).isSameAs(value);
        assertThat(dao.findAll()).containsExactly(value);
        assertThat(dao.findByTransactionItemRevenueId(1L)).contains(value);
        dao.delete(1L);
        verify(mapper).insert(value); verify(mapper).upsert(value); verify(mapper).delete(1L);
    }

    @Test
    void shipmentDaoDelegatesCrud() {
        ShipmentMapper mapper = mock(ShipmentMapper.class);
        ShipmentDao dao = new ShipmentDao(mapper);
        Shipment value = Shipment.builder().shipmentId(1L).build();
        when(mapper.findAll()).thenReturn(List.of(value));
        when(mapper.findByShipmentId(1L)).thenReturn(Optional.of(value));

        assertThat(dao.insert(value)).isSameAs(value);
        assertThat(dao.upsert(value)).isSameAs(value);
        assertThat(dao.findAll()).containsExactly(value);
        assertThat(dao.findByShipmentId(1L)).contains(value);
        when(mapper.findTransactionItemIdsByShipmentId(1L)).thenReturn(List.of(10L));
        assertThat(dao.findTransactionItemIdsByShipmentId(1L)).containsExactly(10L);
        dao.linkTransactionItem(10L, 1L);
        dao.delete(1L);
        verify(mapper).insert(value); verify(mapper).upsert(value); verify(mapper).linkTransactionItem(10L, 1L); verify(mapper).delete(1L);
    }
}
