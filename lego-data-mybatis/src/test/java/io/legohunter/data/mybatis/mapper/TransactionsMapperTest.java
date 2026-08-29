package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Transactions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionsMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDeleteFindByIdAndFindAll() {
        Transactions transaction = insertTransaction();
        transaction.setNotes("Updated transaction");
        transactionsMapper.update(transaction);

        assertThat(transactionsMapper.findById(transaction.getTransactionId()))
                .hasValueSatisfying(found -> assertThat(found.getNotes()).isEqualTo("Updated transaction"));
        assertThat(transactionsMapper.findByTransactionPlatformIdAndTransactionOrderId(
                transaction.getTransactionPlatformId(),
                transaction.getTransactionOrderId()
        )).hasValueSatisfying(found -> assertThat(found.getTransactionId()).isEqualTo(transaction.getTransactionId()));
        assertThat(transactionsMapper.findAll()).hasSize(1);

        Transactions migratedTransaction = Transactions.builder()
                .transactionId(10L)
                .transactionDate(LocalDate.parse("2026-01-02"))
                .notes("Migrated")
                .transactionPlatformId(1)
                .transactionOrderId("ORDER-2")
                .build();
        transactionsMapper.migrate(migratedTransaction);
        transactionsMapper.delete(migratedTransaction);

        assertThat(transactionsMapper.findById(10L)).isEmpty();
    }
}
