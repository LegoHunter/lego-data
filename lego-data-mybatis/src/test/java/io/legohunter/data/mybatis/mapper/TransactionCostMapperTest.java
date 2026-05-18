package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.TransactionCost;
import io.legohunter.data.dto.Transactions;
import io.legohunter.data.enums.CurrencyCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionCostMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDeleteFindByIdFindByTransactionAndFindAll() {
        Transactions transaction = insertTransaction();
        costTypeMapper.insert(CostType.builder()
                .costTypeCode("SHIP")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping")
                .build());
        TransactionCost transactionCost = TransactionCost.builder()
                .transactionId(transaction.getTransactionId())
                .costTypeCode("SHIP")
                .currencyCode(CurrencyCode.USD)
                .amount(5.25)
                .notes("Cost")
                .build();

        transactionCostMapper.insert(transactionCost);
        transactionCost.setAmount(6.50);
        transactionCostMapper.update(transactionCost);

        assertThat(transactionCostMapper.findById(transactionCost.getTransactionCostId()))
                .hasValueSatisfying(found -> assertThat(found.getAmount()).isEqualTo(6.50));
        assertThat(transactionCostMapper.findByTransactionIdAndCostTypeCode(transaction.getTransactionId(), "SHIP")).hasSize(1);
        assertThat(transactionCostMapper.findAll()).hasSize(1);

        transactionCostMapper.delete(transactionCost.getTransactionCostId());
        assertThat(transactionCostMapper.findById(transactionCost.getTransactionCostId())).isEmpty();

        TransactionCost migratedCost = TransactionCost.builder()
                .transactionCostId(10L)
                .transactionId(transaction.getTransactionId())
                .costTypeCode("SHIP")
                .currencyCode(CurrencyCode.USD)
                .amount(4.25)
                .notes("Migrated cost")
                .build();
        transactionCostMapper.migrate(migratedCost);
        transactionCostMapper.deleteTransactionCosts(transaction.getTransactionId());

        assertThat(transactionCostMapper.findAll()).isEmpty();
    }
}
