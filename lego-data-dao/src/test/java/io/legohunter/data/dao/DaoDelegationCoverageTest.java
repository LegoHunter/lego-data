package io.legohunter.data.dao;

import io.legohunter.data.dto.Carrier;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.InventoryIndex;
import io.legohunter.data.dto.ItemInventorySeedSource;
import io.legohunter.data.dto.MarketplaceListing;
import io.legohunter.data.dto.MarketplaceListingSyncRequest;
import io.legohunter.data.dto.Payment;
import io.legohunter.data.dto.PaymentPlatform;
import io.legohunter.data.dto.PricingDataQualitySummary;
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
import io.legohunter.data.mybatis.mapper.ItemInventorySeedSourceMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceListingMapper;
import io.legohunter.data.mybatis.mapper.MarketplaceListingSyncRequestMapper;
import io.legohunter.data.mybatis.mapper.PartyMapper;
import io.legohunter.data.mybatis.mapper.PaymentMapper;
import io.legohunter.data.mybatis.mapper.PaymentPlatformMapper;
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
    void transactionCostDaoDelegatesAllMapperOperationsAndSupportsEmptyReplacementLists() {
        TransactionCostMapper transactionCostMapper = mock(TransactionCostMapper.class);
        TransactionItemCostMapper transactionItemCostMapper = mock(TransactionItemCostMapper.class);
        TransactionCostDao dao = new TransactionCostDao(transactionCostMapper, transactionItemCostMapper);
        TransactionCost transactionCost = TransactionCost.builder()
                .currencyCode(CurrencyCode.USD)
                .build();
        TransactionItemCost transactionItemCost = TransactionItemCost.builder().build();

        dao.setTransactionCosts(10L, null);
        dao.setTransactionItemCosts(20L, List.of());

        dao.setTransactionCosts(10L, List.of(transactionCost));
        assertThat(transactionCost.getTransactionId()).isEqualTo(10L);
        verify(transactionCostMapper).insert(transactionCost);

        dao.setTransactionItemCosts(20L, List.of(transactionItemCost));
        assertThat(transactionItemCost.getTransactionItemId()).isEqualTo(20L);
        verify(transactionItemCostMapper).insert(transactionItemCost);

        when(transactionCostMapper.findAll()).thenReturn(List.of(transactionCost));
        when(transactionCostMapper.findById(30L)).thenReturn(Optional.of(transactionCost));
        when(transactionCostMapper.findByTransactionId(10L)).thenReturn(List.of(transactionCost));
        when(transactionCostMapper.findByTransactionIdAndCostTypeCode(10L, "SHIP")).thenReturn(List.of(transactionCost));
        when(transactionItemCostMapper.findByTransactionItemId(20L)).thenReturn(List.of(transactionItemCost));
        when(transactionItemCostMapper.findById(40L)).thenReturn(Optional.of(transactionItemCost));
        when(transactionItemCostMapper.findByTransactionItemIdAndCostTypeCode(20L, "PRICE")).thenReturn(List.of(transactionItemCost));

        dao.deleteTransactionCosts(10L);
        dao.deleteTransactionItemCosts(20L);
        dao.delete(30L);
        dao.insert(transactionCost);
        dao.insert(transactionItemCost);
        dao.migrate(transactionCost);
        dao.update(transactionCost);
        dao.update(transactionItemCost);
        dao.deleteTransactionItemCost(40L);

        assertThat(dao.findAll()).containsExactly(transactionCost);
        assertThat(dao.findById(30L)).contains(transactionCost);
        assertThat(dao.findByTransactionId(10L)).containsExactly(transactionCost);
        assertThat(dao.findByTransactionIdAndCostTypeCode(10L, "SHIP")).contains(transactionCost);
        assertThat(dao.findByTransactionItemId(20L)).containsExactly(transactionItemCost);
        assertThat(dao.findTransactionItemCostById(40L)).contains(transactionItemCost);
        assertThat(dao.findByTransactionItemIdAndCostTypeCode(20L, "PRICE")).contains(transactionItemCost);

        verify(transactionCostMapper, times(3)).deleteTransactionCosts(10L);
        verify(transactionItemCostMapper, times(3)).deleteTransactionCosts(20L);
        verify(transactionCostMapper).delete(30L);
        verify(transactionItemCostMapper).delete(40L);
        verify(transactionCostMapper).migrate(transactionCost);
        verify(transactionCostMapper).update(transactionCost);
        verify(transactionItemCostMapper).update(transactionItemCost);
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
        when(transactionItemMapper.findByTransactionId(40L)).thenReturn(List.of(transactionItem));
        when(transactionItemMapper.findByItemInventoryId(70)).thenReturn(List.of(transactionItem));
        TransactionItemDao transactionItemDao = new TransactionItemDao(transactionItemMapper);
        transactionItemDao.insert(transactionItem);
        transactionItemDao.migrate(transactionItem);
        transactionItemDao.update(transactionItem);
        assertThat(transactionItemDao.findAll()).containsExactly(transactionItem);
        assertThat(transactionItemDao.findByTransactionId(40L)).containsExactly(transactionItem);
        assertThat(transactionItemDao.findByItemInventoryId(70)).containsExactly(transactionItem);
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

        when(mapper.findPricingDecisionCandidatesWithCurrentSnapshotByListingExternalServiceIdAndListingStatusCodes(1, Set.of("ACTIVE"), 5))
                .thenReturn(Set.of(listing));
        assertThat(dao.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 5, true))
                .containsExactly(listing);
        assertThat(dao.findPricingDecisionCandidatesByListingExternalServiceIdAndListingStatusCodes(1, Set.of(), 5, true))
                .isEmpty();


        dao.findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 0);
        verify(mapper).findPricingHydrationGapsByListingExternalServiceIdAndListingStatusCode(1, "ACTIVE", 1);

        dao.findPricingDataQualityGapsByListingExternalServiceIdAndListingStatusCodes(1, Set.of("ACTIVE"), 0);
        verify(mapper).findPricingDataQualityGapsByListingExternalServiceIdAndListingStatusCodes(1, Set.of("ACTIVE"), 1);
        assertThat(dao.findPricingDataQualityGapsByListingExternalServiceIdAndListingStatusCodes(1, Set.of(), 5)).isEmpty();
        PricingDataQualitySummary summary = PricingDataQualitySummary.builder().listingsChecked(3).build();
        when(mapper.summarizePricingDataQualityByListingExternalServiceIdAndListingStatusCodes(1, Set.of("ACTIVE")))
                .thenReturn(summary);
        assertThat(dao.summarizePricingDataQualityByListingExternalServiceIdAndListingStatusCodes(1, Set.of("ACTIVE")))
                .isSameAs(summary);
        assertThat(dao.summarizePricingDataQualityByListingExternalServiceIdAndListingStatusCodes(1, Set.of()).getListingsChecked())
                .isZero();

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
    void marketplaceListingSyncRequestDaoCoversFilteredLookupBranches() {
        MarketplaceListingSyncRequestMapper mapper = mock(MarketplaceListingSyncRequestMapper.class);
        MarketplaceListingSyncRequestDao dao = new MarketplaceListingSyncRequestDao(mapper);
        MarketplaceListingSyncRequest syncRequest = MarketplaceListingSyncRequest.builder()
                .marketplaceListingSyncRequestId(100L)
                .marketplaceListingId(200)
                .syncRequestTypeCode("LISTING_CREATE")
                .syncRequestStatusCode("PENDING")
                .build();
        ZonedDateTime dueAt = ZonedDateTime.parse("2026-06-24T10:00:00Z");

        when(mapper.findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
                200,
                "LISTING_CREATE",
                Set.of("PENDING", "CLAIMED")
        )).thenReturn(Set.of(syncRequest));
        when(mapper.findClaimableByStatusCodeAndSyncRequestTypeCodes(
                "PENDING",
                Set.of("PRICE_UPDATE"),
                dueAt,
                5
        )).thenReturn(Set.of(syncRequest));

        assertThat(dao.findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(
                200,
                "LISTING_CREATE",
                Set.of("PENDING", "CLAIMED")
        )).containsExactly(syncRequest);
        when(mapper.findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(200, 300L, "PRICE_UPDATE"))
                .thenReturn(Optional.of(syncRequest));
        assertThat(dao.findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(200, 300L, "PRICE_UPDATE"))
                .contains(syncRequest);
        assertThat(dao.findByMarketplaceListingIdAndPricingDecisionIdAndSyncRequestTypeCode(200, 301L, "PRICE_UPDATE"))
                .isEmpty();
        assertThat(dao.findClaimableByStatusCodeAndSyncRequestTypeCodes(
                "PENDING",
                Set.of("PRICE_UPDATE"),
                dueAt,
                5
        )).containsExactly(syncRequest);
        assertThat(dao.findByMarketplaceListingIdAndSyncRequestTypeCodeAndSyncRequestStatusCodes(200, "LISTING_CREATE", Set.of()))
                .isEmpty();
        assertThat(dao.findClaimableByStatusCodeAndSyncRequestTypeCodes("PENDING", Set.of(), dueAt, 5))
                .isEmpty();
    }

    @Test
    void itemInventorySeedSourceDaoDelegatesAllOperations() {
        ItemInventorySeedSourceMapper mapper = mock(ItemInventorySeedSourceMapper.class);
        ItemInventorySeedSourceDao dao = new ItemInventorySeedSourceDao(mapper);
        ItemInventorySeedSource seedSource = ItemInventorySeedSource.builder()
                .itemInventoryId(7)
                .sourceSystem(ItemInventorySeedSource.SOURCE_SYSTEM_LEGO_PROD_LEGACY)
                .sourceKey("28")
                .seedRunId("run-1")
                .build();
        when(mapper.findAll()).thenReturn(List.of(seedSource));
        when(mapper.findByItemInventoryId(7)).thenReturn(Optional.of(seedSource));
        when(mapper.findBySourceSystemAndSourceKey(ItemInventorySeedSource.SOURCE_SYSTEM_LEGO_PROD_LEGACY, "28")).thenReturn(Optional.of(seedSource));
        when(mapper.findBySeedRunId("run-1")).thenReturn(List.of(seedSource));
        when(mapper.deleteByItemInventoryId(7)).thenReturn(1);

        assertThat(dao.findAll()).containsExactly(seedSource);
        assertThat(dao.findByItemInventoryId(7)).contains(seedSource);
        assertThat(dao.findBySourceSystemAndSourceKey(ItemInventorySeedSource.SOURCE_SYSTEM_LEGO_PROD_LEGACY, "28")).contains(seedSource);
        assertThat(dao.findBySeedRunId("run-1")).containsExactly(seedSource);
        assertThat(dao.insert(seedSource)).isSameAs(seedSource);
        verify(mapper).insert(seedSource);
        assertThat(dao.deleteByItemInventoryId(7)).isOne();
    }
}
