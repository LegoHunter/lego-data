package io.legohunter.data.dao;

import io.legohunter.data.dto.TransactionItemRevenue;
import io.legohunter.data.mybatis.mapper.TransactionItemRevenueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class TransactionItemRevenueDao {
    private final TransactionItemRevenueMapper mapper;
    public void insert(TransactionItemRevenue revenue) { mapper.insert(revenue); }
    public Optional<TransactionItemRevenue> findByTransactionItemId(Long transactionItemId) { return mapper.findByTransactionItemId(transactionItemId); }
}
