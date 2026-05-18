package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.TransactionItemCost;
import io.legohunter.data.dto.Transactions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionItemCostMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDeleteFindByIdFindByTransactionItemAndFindAll() {
        Transactions transaction = insertTransaction();
        insertTransactionType();
        ItemInventory itemInventory = insertItemInventory("uuid-transaction-item-cost");
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(itemInventory.getItemInventoryId())
                .notes("Item")
                .build();
        transactionItemMapper.insert(transactionItem);
        costTypeMapper.insert(CostType.builder()
                .costTypeCode("SHIP")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping")
                .build());
        TransactionItemCost transactionItemCost = TransactionItemCost.builder()
                .transactionItemId(transactionItem.getTransactionItemId())
                .costTypeCode("SHIP")
                .currencyCode("USD")
                .amount(2.25)
                .notes("Item cost")
                .build();

        transactionItemCostMapper.insert(transactionItemCost);
        transactionItemCost.setAmount(3.50);
        transactionItemCostMapper.update(transactionItemCost);

        assertThat(transactionItemCostMapper.findById(transactionItemCost.getTransactionItemCostId()))
                .hasValueSatisfying(found -> assertThat(found.getAmount()).isEqualTo(3.50));
        assertThat(transactionItemCostMapper.findByTransactionItemIdAndCostTypeCode(transactionItem.getTransactionItemId(), "SHIP")).hasSize(1);
        assertThat(transactionItemCostMapper.findAll()).hasSize(1);

        transactionItemCostMapper.delete(transactionItemCost.getTransactionItemCostId());
        assertThat(transactionItemCostMapper.findById(transactionItemCost.getTransactionItemCostId())).isEmpty();

        TransactionItemCost migratedItemCost = TransactionItemCost.builder()
                .transactionItemCostId(10L)
                .transactionItemId(transactionItem.getTransactionItemId())
                .costTypeCode("SHIP")
                .currencyCode("USD")
                .amount(1.25)
                .notes("Migrated item cost")
                .build();
        transactionItemCostMapper.migrate(migratedItemCost);
        transactionItemCostMapper.deleteTransactionCosts(transactionItem.getTransactionItemId());

        assertThat(transactionItemCostMapper.findAll()).isEmpty();
    }
}
