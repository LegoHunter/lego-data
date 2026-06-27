package io.legohunter.data.dao;

import io.legohunter.data.dto.Carrier;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.InventoryIndex;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.PaymentPlatform;
import io.legohunter.data.dto.PricingDecision;
import io.legohunter.data.dto.TransactionCost;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.TransactionItemCost;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.dto.Party;
import io.legohunter.data.enums.CurrencyCode;
import io.legohunter.data.mybatis.mapper.CarrierMapper;
import io.legohunter.data.mybatis.mapper.ConditionMapper;
import io.legohunter.data.mybatis.mapper.CostTypeMapper;
import io.legohunter.data.mybatis.mapper.InventoryIndexMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import io.legohunter.data.mybatis.mapper.PartyMapper;
import io.legohunter.data.mybatis.mapper.PaymentPlatformMapper;
import io.legohunter.data.mybatis.mapper.PricingCrawlWorkItemMapper;
import io.legohunter.data.mybatis.mapper.PricingDecisionMapper;
import io.legohunter.data.mybatis.mapper.TransactionCostMapper;
import io.legohunter.data.mybatis.mapper.TransactionItemCostMapper;
import io.legohunter.data.mybatis.mapper.TransactionItemMapper;
import io.legohunter.data.mybatis.mapper.TransactionPlatformMapper;
import io.legohunter.data.mybatis.mapper.TransactionTypeMapper;
import io.legohunter.data.mybatis.mapper.TransactionsMapper;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DaoDelegationCoverageTest {

    @Test
    void transactionCostDaoDelegatesAllMapperOperationsAndSkipsEmptyCostLists() {
        TransactionCostMapper transactionCostMapper = mock(TransactionCostMapper.class);
        TransactionItemCostMapper transactionItemCostMapper = mock(TransactionItemCostMapper.class);
        TransactionCostDao dao = new TransactionCostDao(transactionCostMapper, transactionItemCostMapper);
        TransactionCost transactionCost = TransactionCost.builder()
                .currencyCode(CurrencyCode.USD)
                .build();
        TransactionItemCost transactionItemCost = TransactionItemCost.builder().build();

        dao.setTransactionCosts(10L, null);
        dao.setTransactionItemCosts(20L, List.of());
        verifyNoInteractions(transactionCostMapper, transactionItemCostMapper);

        dao.setTransactionCosts(10L, List.of(transactionCost));
        assertThat(transactionCost.getTransactionId()).isEqualTo(10L);
        verify(transactionCostMapper).deleteTransactionCosts(10L);
        verify(transactionCostMapper).insert(transactionCost);

        dao.setTransactionItemCosts(20L, List.of(transactionItemCost));
        assertThat(transactionItemCost.getTransactionItemId()).isEqualTo(20L);
        verify(transactionItemCostMapper).deleteTransactionCosts(20L);
        verify(transactionItemCostMapper).insert(transactionItemCost);

        when(transactionCostMapper.findAll()).thenReturn(List.of(transactionCost));
        when(transactionCostMapper.findById(30L)).thenReturn(Optional.of(transactionCost));
        when(transactionCostMapper.findByTransactionIdAndCostTypeCode(10L, "SHIP")).thenReturn(List.of(transactionCost));

        dao.deleteTransactionCosts(10L);
        dao.deleteTransactionItemCosts(20L);
        dao.delete(30L);
        dao.insert(transactionCost);
        dao.insert(transactionItemCost);
        dao.migrate(transactionCost);
        dao.update(transactionCost);

        assertThat(dao.findAll()).containsExactly(transactionCost);
        assertThat(dao.findById(30L)).contains(transactionCost);
        assertThat(dao.findByTransactionIdAndCostTypeCode(10L, "SHIP")).contains(transactionCost);

        verify(transactionCostMapper, times(2)).deleteTransactionCosts(10L);
        verify(transactionItemCostMapper, times(2)).deleteTransactionCosts(20L);
        verify(transactionCostMapper).delete(30L);
        verify(transactionCostMapper).migrate(transactionCost);
        verify(transactionCostMapper).update(transactionCost);
    }

    @Test
    void lookupAndTransactionDaosDelegateToMappers() {
        CarrierMapper carrierMapper = mock(CarrierMapper.class);
        ConditionMapper conditionMapper = mock(ConditionMapper.class);
        CostTypeMapper costTypeMapper = mock(CostTypeMapper.class);
        PaymentPlatformMapper paymentPlatformMapper = mock(PaymentPlatformMapper.class);
        TransactionPlatformMapper transactionPlatformMapper = mock(TransactionPlatformMapper.class);
        TransactionTypeMapper transactionTypeMapper = mock(TransactionTypeMapper.class);
        TransactionsMapper transactionsMapper = mock(TransactionsMapper.class);
        TransactionItemMapper transactionItemMapper = mock(TransactionItemMapper.class);
        PartyMapper partyMapper = mock(PartyMapper.class);

        Carrier carrier = Carrier.builder().build();
        Condition condition = Condition.builder().build();
        CostType costType = CostType.builder().build();
        PaymentPlatform paymentPlatform = PaymentPlatform.builder().build();
        TransactionPlatform transactionPlatform = TransactionPlatform.builder().build();
        TransactionType transactionType = TransactionType.builder().build();
        Transactions transactions = Transactions.builder().build();
        TransactionItem transactionItem = TransactionItem.builder().build();
        Party party = Party.builder().build();

        when(carrierMapper.findAll()).thenReturn(List.of(carrier));
        when(carrierMapper.findCarrierByCode("UPS")).thenReturn(Optional.of(carrier));
        CarrierDao carrierDao = new CarrierDao(carrierMapper);
        assertThat(carrierDao.findAll()).containsExactly(carrier);
        assertThat(carrierDao.findCarrierByCode("UPS")).contains(carrier);
        carrierDao.insert(carrier);
        carrierDao.update(carrier);
        verify(carrierMapper).insertCarrier(carrier);
        verify(carrierMapper).updateCarrier(carrier);

        when(conditionMapper.findAll()).thenReturn(List.of(condition));
        when(conditionMapper.findConditionById(1)).thenReturn(Optional.of(condition));
        when(conditionMapper.findByConditionCode("G")).thenReturn(Optional.of(condition));
        ConditionDao conditionDao = new ConditionDao(conditionMapper);
        assertThat(conditionDao.findAll()).containsExactly(condition);
        assertThat(conditionDao.findConditionById(1)).contains(condition);
        assertThat(conditionDao.findByConditionCode("G")).contains(condition);
        conditionDao.insert(condition);
        conditionDao.update(condition);
        verify(conditionMapper).insert(condition);
        verify(conditionMapper).update(condition);

        when(costTypeMapper.findAll()).thenReturn(List.of(costType));
        when(costTypeMapper.findCostTypeByCode("SHIP")).thenReturn(Optional.of(costType));
        CostTypeDao costTypeDao = new CostTypeDao(costTypeMapper);
        assertThat(costTypeDao.findAll()).containsExactly(costType);
        assertThat(costTypeDao.findCostTypeByCode("SHIP")).contains(costType);
        costTypeDao.insert(costType);
        costTypeDao.update(costType);
        verify(costTypeMapper).insert(costType);
        verify(costTypeMapper).update(costType);

        when(paymentPlatformMapper.findAll()).thenReturn(List.of(paymentPlatform));
        when(paymentPlatformMapper.findPaymentPlatformById(1)).thenReturn(Optional.of(paymentPlatform));
        when(paymentPlatformMapper.findPaymentPlatformByName("PayPal")).thenReturn(Optional.of(paymentPlatform));
        PaymentPlatformDao paymentPlatformDao = new PaymentPlatformDao(paymentPlatformMapper);
        assertThat(paymentPlatformDao.findAll()).containsExactly(paymentPlatform);
        assertThat(paymentPlatformDao.findPaymentPlatformById(1)).contains(paymentPlatform);
        assertThat(paymentPlatformDao.findPaymentPlatformByName("PayPal")).contains(paymentPlatform);
        paymentPlatformDao.insert(paymentPlatform);
        paymentPlatformDao.update(paymentPlatform);
        verify(paymentPlatformMapper).insert(paymentPlatform);
        verify(paymentPlatformMapper).update(paymentPlatform);

        when(transactionPlatformMapper.findAll()).thenReturn(List.of(transactionPlatform));
        when(transactionPlatformMapper.findTransactionPlatformById(1)).thenReturn(Optional.of(transactionPlatform));
        when(transactionPlatformMapper.findTransactionPlatformByName("BrickLink")).thenReturn(Optional.of(transactionPlatform));
        TransactionPlatformDao transactionPlatformDao = new TransactionPlatformDao(transactionPlatformMapper);
        assertThat(transactionPlatformDao.findAll()).containsExactly(transactionPlatform);
        assertThat(transactionPlatformDao.findTransactionPlatformById(1)).contains(transactionPlatform);
        assertThat(transactionPlatformDao.findTransactionPlatformByName("BrickLink")).contains(transactionPlatform);
        transactionPlatformDao.insert(transactionPlatform);
        transactionPlatformDao.update(transactionPlatform);
        verify(transactionPlatformMapper).insert(transactionPlatform);
        verify(transactionPlatformMapper).update(transactionPlatform);

        when(transactionTypeMapper.findAll()).thenReturn(List.of(transactionType));
        when(transactionTypeMapper.findTransactionTypeByCode("SALE")).thenReturn(Optional.of(transactionType));
        TransactionTypeDao transactionTypeDao = new TransactionTypeDao(transactionTypeMapper);
        assertThat(transactionTypeDao.findAll()).containsExactly(transactionType);
        assertThat(transactionTypeDao.findTransactionTypeByCode("SALE")).contains(transactionType);
        transactionTypeDao.insert(transactionType);
        transactionTypeDao.udpate(transactionType);
        verify(transactionTypeMapper).insert(transactionType);
        verify(transactionTypeMapper).update(transactionType);

        when(transactionsMapper.findAll()).thenReturn(List.of(transactions));
        when(transactionsMapper.findById(40L)).thenReturn(Optional.of(transactions));
        TransactionsDao transactionsDao = new TransactionsDao(transactionsMapper);
        transactionsDao.insert(transactions);
        transactionsDao.update(transactions);
        assertThat(transactionsDao.findAll()).containsExactly(transactions);
        assertThat(transactionsDao.findById(40L)).contains(transactions);
        verify(transactionsMapper).insert(transactions);
        verify(transactionsMapper).update(transactions);

        when(transactionItemMapper.findAll()).thenReturn(List.of(transactionItem));
        when(transactionItemMapper.findById(50L)).thenReturn(Optional.of(transactionItem));
        TransactionItemDao transactionItemDao = new TransactionItemDao(transactionItemMapper);
        transactionItemDao.insert(transactionItem);
        transactionItemDao.migrate(transactionItem);
        transactionItemDao.update(transactionItem);
        assertThat(transactionItemDao.findAll()).containsExactly(transactionItem);
        assertThat(transactionItemDao.findById(50L)).contains(transactionItem);
        verify(transactionItemMapper).insert(transactionItem);
        verify(transactionItemMapper).migrate(transactionItem);
        verify(transactionItemMapper).update(transactionItem);

        when(partyMapper.findAll()).thenReturn(List.of(party));
        when(partyMapper.findPartyById(60L)).thenReturn(Optional.of(party));
        PartyDao partyDao = new PartyDao(partyMapper);
        partyDao.insert(party);
        partyDao.migrate(party);
        partyDao.setAutoIncrementMode();
        partyDao.update(party);
        partyDao.decrementPartyId(60L);
        assertThat(partyDao.findAll()).containsExactly(party);
        assertThat(partyDao.findPartyById(60L)).contains(party);
        verify(partyMapper).insert(party);
        verify(partyMapper).migrate(party);
        verify(partyMapper).setAutoIncrementMode();
        verify(partyMapper).update(party);
        verify(partyMapper).decrementPartyId(60L);
    }

    @Test
    void inventoryIndexDaoDelegatesAndWrapsMapperExceptions() {
        InventoryIndexMapper mapper = mock(InventoryIndexMapper.class);
        InventoryIndexDao dao = new InventoryIndexDao(mapper);
        InventoryIndex inventoryIndex = InventoryIndex.builder().build();
        RuntimeException mapperFailure = new RuntimeException("mapper failed");

        when(mapper.getAll()).thenReturn(List.of(inventoryIndex));
        when(mapper.getAllForBox(7)).thenReturn(List.of(inventoryIndex));
        when(mapper.getAllWithNoItem()).thenReturn(List.of(inventoryIndex));
        when(mapper.findByItemNumber("6390-1")).thenReturn(Optional.of(inventoryIndex));
        when(mapper.findByBoxIdAndBoxIndex(7, 1)).thenReturn(Optional.of(inventoryIndex));
        when(mapper.findByBoxIdAndBoxIndexAndItemNumber("6390-1", 7, 1)).thenReturn(Optional.of(inventoryIndex));

        assertThat(dao.findAll()).containsExactly(inventoryIndex);
        assertThat(dao.getAllForBox(7)).containsExactly(inventoryIndex);
        assertThat(dao.getAllWithNoItem()).containsExactly(inventoryIndex);
        assertThat(dao.findByItemNumber("6390-1")).contains(inventoryIndex);
        assertThat(dao.findByBoxIdAndBoxIndex(7, 1)).contains(inventoryIndex);
        assertThat(dao.findByBoxIdAndBoxIndexAndItemNumber(7, 1, "6390-1")).contains(inventoryIndex);
        dao.insert(inventoryIndex);
        dao.update(inventoryIndex);
        verify(mapper).insert(inventoryIndex);
        verify(mapper).update(inventoryIndex);

        doThrow(mapperFailure).when(mapper).findByBoxIdAndBoxIndexAndItemNumber("bad", 7, 1);
        assertThatThrownBy(() -> dao.findByBoxIdAndBoxIndexAndItemNumber(7, 1, "bad"))
                .isInstanceOf(RuntimeException.class)
                .hasCause(mapperFailure);
    }

    @Test
    void marketplaceListingDaoCoversSnapshotCandidateAndUpsertBranches() {
        MarketplaceListingMapper mapper = mock(MarketplaceListingMapper.class);
        MarketplaceListingDao dao = new MarketplaceListingDao(mapper);
        MarketplaceListing listing = MarketplaceListing.builder()
                .marketplaceListingId(100)
                .listingExternalServiceId(1)
                .externalListingId("remote-1")
                .build();

        when(mapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 5))
                .thenReturn(Set.of(listing));
        assertThat(dao.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 5, true))
                .containsExactly(listing);

        when(mapper.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                1, "ACTIVE", "PENDING", "CLAIMED", ZonedDateTime.parse("2026-06-24T10:00:00Z"), 5
        )).thenReturn(Set.of(listing));
        assertThat(dao.findPricingCrawlSchedulingCandidatesByListingExternalServiceIdAndListingStatusCode(
                1, "ACTIVE", "PENDING", "CLAIMED", ZonedDateTime.parse("2026-06-24T10:00:00Z"), 5
        )).containsExactly(listing);

        dao.findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 0);
        verify(mapper).findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 1);

        MarketplaceListing missingIdListing = MarketplaceListing.builder()
                .listingExternalServiceId(1)
                .externalListingId("remote-2")
                .build();
        MarketplaceListing hydratedListing = MarketplaceListing.builder()
                .marketplaceListingId(101)
                .listingExternalServiceId(1)
                .externalListingId("remote-2")
                .build();
        when(mapper.findByListingExternalServiceIdAndExternalListingId(1, "remote-2"))
                .thenReturn(Optional.of(hydratedListing));

        assertThat(dao.upsert(missingIdListing)).isSameAs(hydratedListing);
        verify(mapper).upsert(missingIdListing);
    }

    @Test
    void pricingDecisionDaoCoversLatestReviewLookup() {
        PricingDecisionMapper mapper = mock(PricingDecisionMapper.class);
        PricingDecisionDao dao = new PricingDecisionDao(mapper);

        dao.countLatestByDecisionStatusCode("PROPOSED");
        dao.countLatestUnappliedByDecisionStatusCode("PROPOSED");
        dao.findLatestReviewsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 25);
        dao.findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
                1,
                "ACTIVE",
                "PROPOSED",
                25
        );

        verify(mapper).countLatestByDecisionStatusCode("PROPOSED");
        verify(mapper).countLatestUnappliedByDecisionStatusCode("PROPOSED");
        verify(mapper).findLatestReviewsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 25);
        verify(mapper).findLatestUnappliedDecisionReviewsByListingExternalServiceIdAndListingStatusCodeAndDecisionStatusCode(
                1,
                "ACTIVE",
                "PROPOSED",
                25
        );
    }

    @Test
    void pricingCrawlWorkItemDaoCoversObservabilityCounts() {
        PricingCrawlWorkItemMapper mapper = mock(PricingCrawlWorkItemMapper.class);
        PricingCrawlWorkItemDao dao = new PricingCrawlWorkItemDao(mapper);
        ZonedDateTime now = ZonedDateTime.parse("2026-06-25T10:00:00Z");

        dao.countByWorkStatusCode("PENDING");
        dao.countDueByWorkStatusCode("PENDING", now);
        dao.countRetryableByWorkStatusCode("PENDING");
        dao.countStaleClaimed("CLAIMED", now.minusHours(2));
        dao.summarizeMaintenance("PENDING", "CLAIMED", "SUCCEEDED", now, now.minusHours(2));
        dao.findDuplicateMarketplaceListingWorkItems("PENDING", "CLAIMED", 0);
        dao.findRecentFailures(0);

        verify(mapper).countByWorkStatusCode("PENDING");
        verify(mapper).countDueByWorkStatusCode("PENDING", now);
        verify(mapper).countRetryableByWorkStatusCode("PENDING");
        verify(mapper).countStaleClaimed("CLAIMED", now.minusHours(2));
        verify(mapper).summarizeMaintenance("PENDING", "CLAIMED", "SUCCEEDED", now, now.minusHours(2));
        verify(mapper).findDuplicateMarketplaceListingWorkItems("PENDING", "CLAIMED", 1);
        verify(mapper).findRecentFailures(1);
    }
}
