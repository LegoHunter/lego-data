package io.legohunter.data.dao;

import io.legohunter.data.dto.Payment;
import io.legohunter.data.mybatis.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentDao {
    private final PaymentMapper paymentMapper;

    public void setTransactionPayments(Long transactionId, List<Payment> payments) {
        if (!CollectionUtils.isEmpty(payments)) {
            deleteByTransactionId(transactionId);
            payments.forEach(payment -> {
                payment.setTransactionId(transactionId);
                insert(payment);
            });
        }
    }

    public void insert(Payment payment) {
        paymentMapper.insert(payment);
    }

    public void migrate(Payment payment) {
        paymentMapper.migrate(payment);
    }

    public void update(Payment payment) {
        paymentMapper.update(payment);
    }

    public Payment upsert(Payment payment) {
        paymentMapper.upsert(payment);
        return payment;
    }

    public void delete(Long paymentId) {
        paymentMapper.delete(paymentId);
    }

    public void deleteByTransactionId(Long transactionId) {
        paymentMapper.deleteByTransactionId(transactionId);
    }

    public List<Payment> findAll() {
        return paymentMapper.findAll();
    }

    public Optional<Payment> findById(Long paymentId) {
        return paymentMapper.findById(paymentId);
    }

    public List<Payment> findByTransactionId(Long transactionId) {
        return paymentMapper.findByTransactionId(transactionId);
    }
}
