package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.Transactions;
import net.lego.data.v2.mybatis.mapper.TransactionsMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionsDao {
    private final TransactionsMapper transactionsMapper;

    public void insert(Transactions transactions) {
        transactionsMapper.insert(transactions);
    }

    public void update(Transactions transactions) {
        transactionsMapper.update(transactions);
    }

    public List<Transactions> findAll() {
        return transactionsMapper.findAll();
    }

    public Optional<Transactions> findById(Long transactionId) {
        return transactionsMapper.findById(transactionId);
    }
}
