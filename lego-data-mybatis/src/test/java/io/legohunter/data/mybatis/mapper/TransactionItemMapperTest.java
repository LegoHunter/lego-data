package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventory;
import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.Transactions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionItemMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDeleteFindByIdAndFindAll() {
        Transactions transaction = insertTransaction();
        insertTransactionType();
        ItemInventory itemInventory = insertItemInventory("uuid-transaction-item");
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(itemInventory.getItemInventoryId())
                .notes("Item")
                .build();

        transactionItemMapper.insert(transactionItem);
        transactionItem.setNotes("Updated item");
        transactionItemMapper.update(transactionItem);

        assertThat(transactionItemMapper.findById(transactionItem.getTransactionItemId()))
                .hasValueSatisfying(found -> assertThat(found.getNotes()).isEqualTo("Updated item"));
        assertThat(transactionItemMapper.findAll()).hasSize(1);

        TransactionItem migratedItem = TransactionItem.builder()
                .transactionItemId(10L)
                .transactionId(transaction.getTransactionId())
                .transactionTypeCode("BUY")
                .itemInventoryId(itemInventory.getItemInventoryId())
                .notes("Migrated item")
                .build();
        transactionItemMapper.migrate(migratedItem);
        transactionItemMapper.delete(migratedItem);

        assertThat(transactionItemMapper.findById(10L)).isEmpty();
    }
}
