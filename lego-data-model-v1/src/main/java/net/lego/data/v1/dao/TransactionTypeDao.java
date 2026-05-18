package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.TransactionType;
import net.lego.data.v1.mybatis.mapper.TransactionTypeMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("transactionTypeDaoV1")
@RequiredArgsConstructor
public class TransactionTypeDao {
    private final TransactionTypeMapperV1 transactionTypeMapperV1;

    public List<TransactionType> findAll() {
        return transactionTypeMapperV1.findAll();
    }

    public Optional<TransactionType> findTransactionTypeByCode(final String transactionTypeCode) {
        return transactionTypeMapperV1.findTransactionTypeByCode(transactionTypeCode);
    }

}
