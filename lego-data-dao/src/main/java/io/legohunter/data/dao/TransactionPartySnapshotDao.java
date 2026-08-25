package io.legohunter.data.dao;

import io.legohunter.data.dto.TransactionPartySnapshot;
import io.legohunter.data.mybatis.mapper.TransactionPartySnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component @RequiredArgsConstructor
public class TransactionPartySnapshotDao {
    private final TransactionPartySnapshotMapper mapper;
    public void insert(TransactionPartySnapshot snapshot) { mapper.insert(snapshot); }
    public List<TransactionPartySnapshot> findByTransactionId(Long transactionId) { return mapper.findByTransactionId(transactionId); }
}
