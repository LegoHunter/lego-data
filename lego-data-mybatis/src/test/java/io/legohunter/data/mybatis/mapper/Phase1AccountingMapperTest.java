package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PartyExternalIdentity;
import io.legohunter.data.dto.Shipment;
import io.legohunter.data.dto.TransactionItemRevenue;
import io.legohunter.data.dto.TransactionPartySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class Phase1AccountingMapperTest {
    @Autowired PartyExternalIdentityMapper partyExternalIdentityMapper;
    @Autowired TransactionPartySnapshotMapper transactionPartySnapshotMapper;
    @Autowired TransactionItemRevenueMapper transactionItemRevenueMapper;
    @Autowired ShipmentMapper shipmentMapper;

    @Test
    void partyExternalIdentitySupportsCrud() {
        PartyExternalIdentity identity = PartyExternalIdentity.builder()
                .partyId(1L).transactionPlatformId(1).externalPartyId("buyer-1").build();
        partyExternalIdentityMapper.insert(identity);

        assertThat(partyExternalIdentityMapper.findAll()).singleElement()
                .satisfies(found -> assertThat(found.getPartyExternalIdentityId()).isEqualTo(identity.getPartyExternalIdentityId()));
        assertThat(partyExternalIdentityMapper.findByPartyExternalIdentityId(identity.getPartyExternalIdentityId()))
                .hasValueSatisfying(found -> assertThat(found.getExternalPartyId()).isEqualTo("buyer-1"));

        identity.setPartyId(2L);
        partyExternalIdentityMapper.upsert(identity);
        assertThat(partyExternalIdentityMapper.findByPlatformAndExternalPartyId(1, "buyer-1"))
                .hasValueSatisfying(found -> assertThat(found.getPartyId()).isEqualTo(2L));

        assertThat(partyExternalIdentityMapper.delete(identity.getPartyExternalIdentityId())).isOne();
        assertThat(partyExternalIdentityMapper.findByPartyExternalIdentityId(identity.getPartyExternalIdentityId())).isEmpty();
    }

    @Test
    void transactionPartySnapshotSupportsCrudWithoutMutatingHistoricalSnapshot() {
        TransactionPartySnapshot snapshot = TransactionPartySnapshot.builder()
                .transactionId(1L).partyId(1L).partyRoleCode("BUYER").displayName("Original buyer")
                .capturedAt(ZonedDateTime.parse("2026-08-28T12:00:00Z")).build();
        transactionPartySnapshotMapper.insert(snapshot);

        assertThat(transactionPartySnapshotMapper.findAll()).singleElement()
                .satisfies(found -> assertThat(found.getTransactionPartySnapshotId()).isEqualTo(snapshot.getTransactionPartySnapshotId()));
        assertThat(transactionPartySnapshotMapper.findByTransactionPartySnapshotId(snapshot.getTransactionPartySnapshotId()))
                .hasValueSatisfying(found -> assertThat(found.getDisplayName()).isEqualTo("Original buyer"));

        snapshot.setDisplayName("Changed buyer");
        transactionPartySnapshotMapper.upsert(snapshot);
        assertThat(transactionPartySnapshotMapper.findByTransactionPartySnapshotId(snapshot.getTransactionPartySnapshotId()))
                .hasValueSatisfying(found -> assertThat(found.getDisplayName()).isEqualTo("Original buyer"));

        assertThat(transactionPartySnapshotMapper.delete(snapshot.getTransactionPartySnapshotId())).isOne();
        assertThat(transactionPartySnapshotMapper.findByTransactionPartySnapshotId(snapshot.getTransactionPartySnapshotId())).isEmpty();
    }

    @Test
    void transactionItemRevenueSupportsCrud() {
        TransactionItemRevenue revenue = TransactionItemRevenue.builder()
                .transactionItemId(1L).currencyCode("USD").unitAmount(new BigDecimal("4.50"))
                .quantity(2).totalAmount(new BigDecimal("9.00")).build();
        transactionItemRevenueMapper.insert(revenue);

        assertThat(transactionItemRevenueMapper.findAll()).singleElement()
                .satisfies(found -> assertThat(found.getTransactionItemRevenueId()).isEqualTo(revenue.getTransactionItemRevenueId()));
        assertThat(transactionItemRevenueMapper.findByTransactionItemRevenueId(revenue.getTransactionItemRevenueId()))
                .hasValueSatisfying(found -> assertThat(found.getTotalAmount()).isEqualByComparingTo("9.00"));

        revenue.setTotalAmount(new BigDecimal("10.00"));
        transactionItemRevenueMapper.upsert(revenue);
        assertThat(transactionItemRevenueMapper.findByTransactionItemId(1L))
                .hasValueSatisfying(found -> assertThat(found.getTotalAmount()).isEqualByComparingTo("10.00"));

        assertThat(transactionItemRevenueMapper.delete(revenue.getTransactionItemRevenueId())).isOne();
        assertThat(transactionItemRevenueMapper.findByTransactionItemRevenueId(revenue.getTransactionItemRevenueId())).isEmpty();
    }

    @Test
    void shipmentSupportsCrud() {
        Shipment shipment = Shipment.builder()
                .externalShipmentId("ss-1").shipmentDate(LocalDate.parse("2026-08-28"))
                .shipmentTrackingNumber("TRACK-1").carrierCode("USPS")
                .fulfillmentPlatformCode("SHIPSTATION").serviceCode("USPS_GROUND_ADVANTAGE").build();
        shipmentMapper.insert(shipment);

        assertThat(shipmentMapper.findAll()).containsExactly(shipment);
        assertThat(shipmentMapper.findByShipmentId(shipment.getShipmentId())).contains(shipment);

        shipment.setShipmentTrackingNumber("TRACK-2");
        shipmentMapper.upsert(shipment);
        assertThat(shipmentMapper.findByPlatformAndExternalShipmentId("SHIPSTATION", "ss-1"))
                .hasValueSatisfying(found -> assertThat(found.getShipmentTrackingNumber()).isEqualTo("TRACK-2"));

        assertThat(shipmentMapper.delete(shipment.getShipmentId())).isOne();
        assertThat(shipmentMapper.findByShipmentId(shipment.getShipmentId())).isEmpty();
    }

    @Test
    void shipmentTransactionItemLinksAreIdempotent() {
        Shipment shipment = Shipment.builder()
                .externalShipmentId("ss-link-1").shipmentDate(LocalDate.parse("2026-08-28"))
                .shipmentTrackingNumber("TRACK-LINK-1").carrierCode("USPS")
                .fulfillmentPlatformCode("SHIPSTATION").serviceCode("USPS_GROUND_ADVANTAGE").build();
        shipmentMapper.insert(shipment);

        shipmentMapper.linkTransactionItem(101L, shipment.getShipmentId());
        shipmentMapper.linkTransactionItem(101L, shipment.getShipmentId());

        assertThat(shipmentMapper.findTransactionItemIdsByShipmentId(shipment.getShipmentId()))
                .containsExactly(101L);
    }
}
