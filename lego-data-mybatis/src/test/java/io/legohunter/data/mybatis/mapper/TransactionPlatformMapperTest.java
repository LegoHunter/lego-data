package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionPlatform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class TransactionPlatformMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByIdFindByNameAndFindAll() {
        TransactionPlatform transactionPlatform = TransactionPlatform.builder()
                .transactionPlatformId(1)
                .transactionPlatformName("BrickLink")
                .build();

        transactionPlatformMapper.insert(transactionPlatform);
        transactionPlatform.setTransactionPlatformName("BrickLink Marketplace");
        transactionPlatformMapper.update(transactionPlatform);

        assertThat(transactionPlatformMapper.findTransactionPlatformById(1))
                .hasValueSatisfying(found -> assertThat(found.getTransactionPlatformName()).isEqualTo("BrickLink Marketplace"));
        assertThat(transactionPlatformMapper.findTransactionPlatformByName("BrickLink Marketplace"))
                .hasValueSatisfying(found -> assertThat(found.getTransactionPlatformId()).isEqualTo(1));
        assertThat(transactionPlatformMapper.findAll()).extracting(TransactionPlatform::getTransactionPlatformId).containsExactly(1);
    }
}
