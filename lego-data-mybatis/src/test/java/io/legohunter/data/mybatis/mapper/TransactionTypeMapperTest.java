package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionTypeMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByCodeAndFindAll() {
        TransactionType transactionType = TransactionType.builder()
                .transactionTypeCode("BUY")
                .transactionTypeDescription("Buy")
                .conversionFactor(1)
                .build();

        transactionTypeMapper.insert(transactionType);
        transactionType.setTransactionTypeDescription("Purchase");
        transactionTypeMapper.update(transactionType);

        assertThat(transactionTypeMapper.findTransactionTypeByCode("BUY"))
                .hasValueSatisfying(found -> assertThat(found.getTransactionTypeDescription()).isEqualTo("Purchase"));
        assertThat(transactionTypeMapper.findAll()).extracting(TransactionType::getTransactionTypeCode).containsExactly("BUY");
    }
}
