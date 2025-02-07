package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.TransactionCost;
import net.lego.data.v2.mybatis.mapper.TransactionCostMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionCostDao {
    private final TransactionCostMapper transactionCostMapper;

    public void insert(TransactionCost transactionCost) {
        transactionCostMapper.insert(transactionCost);
    }

    public void migrate(TransactionCost transactionCost) {
        transactionCostMapper.migrate(transactionCost);
    }

    public void update(TransactionCost transactionCost) {
        transactionCostMapper.update(transactionCost);
    }

    public List<TransactionCost> findAll() {
        return transactionCostMapper.findAll();
    }

    public Optional<TransactionCost> findById(Long transactionCostId) {
        return transactionCostMapper.findById(transactionCostId);
    }

    public Optional<TransactionCost> findByTransactionIdAndCostTypeCode(Long transactionId, String costTypeCode) {
        return Optional.empty();
    }
}