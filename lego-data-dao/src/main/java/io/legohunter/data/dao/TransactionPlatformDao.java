package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.mybatis.mapper.TransactionPlatformMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionPlatformDao {
    private final TransactionPlatformMapper transactionPlatformMapper;

    public List<TransactionPlatform> findAll() {
        return transactionPlatformMapper.findAll();
    }

    public Optional<TransactionPlatform> findTransactionPlatformById(final Integer transactionPlatformId) {
        return transactionPlatformMapper.findTransactionPlatformById(transactionPlatformId);
    }

    public Optional<TransactionPlatform> findTransactionPlatformByName(final String transactionPlatformName) {
        return transactionPlatformMapper.findTransactionPlatformByName(transactionPlatformName);
    }

    public void insert(TransactionPlatform transactionPlatform) {
        transactionPlatformMapper.insert(transactionPlatform);
    }

    public void update(TransactionPlatform transactionPlatform) {
        transactionPlatformMapper.update(transactionPlatform);
    }
}