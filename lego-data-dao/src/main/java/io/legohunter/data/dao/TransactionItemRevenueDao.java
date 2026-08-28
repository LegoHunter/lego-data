package io.legohunter.data.dao;

import io.legohunter.data.dto.TransactionItemRevenue;
import io.legohunter.data.mybatis.mapper.TransactionItemRevenueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class TransactionItemRevenueDao {
    private final TransactionItemRevenueMapper mapper;
    public List<TransactionItemRevenue> findAll() { return mapper.findAll(); }
    public Optional<TransactionItemRevenue> findByTransactionItemRevenueId(Long transactionItemRevenueId) { return mapper.findByTransactionItemRevenueId(transactionItemRevenueId); }
    public TransactionItemRevenue insert(TransactionItemRevenue revenue) { mapper.insert(revenue); return revenue; }
    public TransactionItemRevenue upsert(TransactionItemRevenue revenue) { mapper.upsert(revenue); return revenue; }
    public int delete(Long transactionItemRevenueId) { return mapper.delete(transactionItemRevenueId); }
    public Optional<TransactionItemRevenue> findByTransactionItemId(Long transactionItemId) { return mapper.findByTransactionItemId(transactionItemId); }
}
