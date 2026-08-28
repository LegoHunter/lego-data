package io.legohunter.data.dao;

import io.legohunter.data.dto.TransactionPartySnapshot;
import io.legohunter.data.mybatis.mapper.TransactionPartySnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class TransactionPartySnapshotDao {
    private final TransactionPartySnapshotMapper mapper;
    public List<TransactionPartySnapshot> findAll() { return mapper.findAll(); }
    public Optional<TransactionPartySnapshot> findByTransactionPartySnapshotId(Long transactionPartySnapshotId) { return mapper.findByTransactionPartySnapshotId(transactionPartySnapshotId); }
    public TransactionPartySnapshot insert(TransactionPartySnapshot snapshot) { mapper.insert(snapshot); return snapshot; }
    public TransactionPartySnapshot upsert(TransactionPartySnapshot snapshot) { mapper.upsert(snapshot); return snapshot; }
    public int delete(Long transactionPartySnapshotId) { return mapper.delete(transactionPartySnapshotId); }
    public List<TransactionPartySnapshot> findByTransactionId(Long transactionId) { return mapper.findByTransactionId(transactionId); }
}
