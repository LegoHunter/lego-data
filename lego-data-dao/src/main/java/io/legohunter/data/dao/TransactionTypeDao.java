package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.TransactionType;
import io.legohunter.data.mybatis.mapper.TransactionTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionTypeDao {
    private final TransactionTypeMapper transactionTypeMapper;

    public List<TransactionType> findAll() {
        return transactionTypeMapper.findAll();
    }

    public Optional<TransactionType> findTransactionTypeByCode(final String transactionTypeCode) {
        return transactionTypeMapper.findTransactionTypeByCode(transactionTypeCode);
    }

    public void insert(TransactionType transactionType) {
        transactionTypeMapper.insert(transactionType);
    }

    public void udpate(TransactionType transactionType) {
        transactionTypeMapper.update(transactionType);
    }
}
