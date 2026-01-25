package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.TransactionItem;
import net.lego.data.v2.dto.Transactions;
import net.lego.data.v2.mybatis.mapper.TransactionItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionItemDao {
    private final TransactionItemMapper transactionItemMapper;

    public void insert(TransactionItem transactionItem) {
        transactionItemMapper.insert(transactionItem);
    }

    public void migrate(TransactionItem transactionItem) {
        transactionItemMapper.migrate(transactionItem);
    }

    public void update(TransactionItem transactionItem) {
        transactionItemMapper.update(transactionItem);
    }

    public List<Transactions> findAll() {
        return transactionItemMapper.findAll();
    }

    public Optional<TransactionItem> findById(Long transactionItemId) {
        return transactionItemMapper.findById(transactionItemId);
    }
}
