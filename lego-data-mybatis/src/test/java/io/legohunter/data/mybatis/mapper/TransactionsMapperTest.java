package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Transactions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

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
        assertThat(transactionsMapper.findAll()).hasSize(1);

        Transactions migratedTransaction = Transactions.builder()
                .transactionId(10L)
                .transactionDateTime(ZonedDateTime.parse("2026-01-02T00:00:00Z"))
                .notes("Migrated")
                .transactionPlatformId(1)
                .transactionOrderId("ORDER-2")
                .build();
        transactionsMapper.migrate(migratedTransaction);
        transactionsMapper.delete(migratedTransaction);

        assertThat(transactionsMapper.findById(10L)).isEmpty();
    }
}
