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
        deleteTransactionCosts(transactionId);
        if (!CollectionUtils.isEmpty(transactionCosts)) {
            transactionCosts.forEach(transactionCost -> {
                transactionCost.setTransactionId(transactionId);
                insert(transactionCost);
            });
        }
    }

    public void setTransactionItemCosts(Long transactionItemId, List<TransactionItemCost> transactionItemCosts) {
        deleteTransactionItemCosts(transactionItemId);
        if (!CollectionUtils.isEmpty(transactionItemCosts)) {
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
        transactionItemCostMapper.deleteTransactionCosts(transactionItemId);
    }

    public void delete(Long transactionCostId) {
        transactionCostMapper.delete(transactionCostId);
    }

    public void deleteTransactionItemCost(Long transactionItemCostId) {
        transactionItemCostMapper.delete(transactionItemCostId);
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

    public void update(TransactionItemCost transactionItemCost) {
        transactionItemCostMapper.update(transactionItemCost);
    }

    public List<TransactionCost> findAll() {
        return transactionCostMapper.findAll();
    }

    public Optional<TransactionCost> findById(Long transactionCostId) {
        return transactionCostMapper.findById(transactionCostId);
    }

    public List<TransactionCost> findByTransactionId(Long transactionId) {
        return transactionCostMapper.findByTransactionId(transactionId);
    }

    public Optional<TransactionCost> findByTransactionIdAndCostTypeCode(Long transactionId, String costTypeCode) {
        return transactionCostMapper.findByTransactionIdAndCostTypeCode(transactionId, costTypeCode)
                .stream()
                .findFirst();
    }

    public List<TransactionItemCost> findByTransactionItemId(Long transactionItemId) {
        return transactionItemCostMapper.findByTransactionItemId(transactionItemId);
    }

    public Optional<TransactionItemCost> findTransactionItemCostById(Long transactionItemCostId) {
        return transactionItemCostMapper.findById(transactionItemCostId);
    }

    public Optional<TransactionItemCost> findByTransactionItemIdAndCostTypeCode(Long transactionItemId, String costTypeCode) {
        return transactionItemCostMapper.findByTransactionItemIdAndCostTypeCode(transactionItemId, costTypeCode)
                .stream()
                .findFirst();
    }
}
