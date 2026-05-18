package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.TransactionCost;
import io.legohunter.data.dto.TransactionItemCost;
import io.legohunter.data.mybatis.mapper.TransactionCostMapper;
import io.legohunter.data.mybatis.mapper.TransactionItemCostMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionCostDao {
    private final TransactionCostMapper transactionCostMapper;
    private final TransactionItemCostMapper transactionItemCostMapper;

    public void setTransactionCosts(Long transactionId, List<TransactionCost> transactionCosts) {
        if (!CollectionUtils.isEmpty(transactionCosts)) {
            deleteTransactionCosts(transactionId);
            transactionCosts.forEach(transactionCost -> {
                transactionCost.setTransactionId(transactionId);
                insert(transactionCost);
            });
        }
    }

    public void setTransactionItemCosts(Long transactionItemId, List<TransactionItemCost> transactionItemCosts) {
        if (!CollectionUtils.isEmpty(transactionItemCosts)) {
            deleteTransactionItemCosts(transactionItemId);
            transactionItemCosts.forEach(transactionItemCost -> {
                transactionItemCost.setTransactionItemId(transactionItemId);
                insert(transactionItemCost);
            });
        }
    }

    public void deleteTransactionCosts(Long transactionId) {
        transactionCostMapper.deleteTransactionCosts(transactionId);
    }

    public void deleteTransactionItemCosts(Long transactionItemId) {
        transactionCostMapper.deleteTransactionCosts(transactionItemId);
    }

    public void delete(Long transactionCostId) {
        transactionCostMapper.delete(transactionCostId);
    }

    public void insert(TransactionCost transactionCost) {
        transactionCostMapper.insert(transactionCost);
    }

    public void insert(TransactionItemCost transactionItemCost) {
        transactionItemCostMapper.insert(transactionItemCost);
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