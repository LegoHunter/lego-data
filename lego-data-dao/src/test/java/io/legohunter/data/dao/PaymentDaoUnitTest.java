package io.legohunter.data.dao;

import io.legohunter.data.dto.Payment;
import io.legohunter.data.mybatis.mapper.PaymentMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PaymentDaoUnitTest {

    private final PaymentMapper paymentMapper = mock(PaymentMapper.class);
    private final PaymentDao paymentDao = new PaymentDao(paymentMapper);

    @Test
    void setTransactionPaymentsReplacesExistingPaymentsAndAssignsTransactionId() {
        Payment payment = Payment.builder().build();

        paymentDao.setTransactionPayments(10L, List.of(payment));

        assertThat(payment.getTransactionId()).isEqualTo(10L);
        verify(paymentMapper).deleteByTransactionId(10L);
        verify(paymentMapper).insert(payment);
    }

    @Test
    void setTransactionPaymentsSkipsNullOrEmptyLists() {
        paymentDao.setTransactionPayments(10L, null);
        paymentDao.setTransactionPayments(10L, List.of());

        verifyNoInteractions(paymentMapper);
    }

    @Test
    void delegatesCrudAndFindOperations() {
        Payment payment = Payment.builder().paymentId(1L).transactionId(10L).build();

        when(paymentMapper.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.findByTransactionId(10L)).thenReturn(List.of(payment));

        paymentDao.insert(payment);
        paymentDao.migrate(payment);
        paymentDao.update(payment);
        paymentDao.delete(1L);
        paymentDao.deleteByTransactionId(10L);

        assertThat(paymentDao.findAll()).containsExactly(payment);
        assertThat(paymentDao.findById(1L)).contains(payment);
        assertThat(paymentDao.findByTransactionId(10L)).containsExactly(payment);

        verify(paymentMapper).insert(payment);
        verify(paymentMapper).migrate(payment);
        verify(paymentMapper).update(payment);
        verify(paymentMapper).delete(1L);
        verify(paymentMapper).deleteByTransactionId(10L);
        verify(paymentMapper, never()).deleteByTransactionId(99L);
    }
}
