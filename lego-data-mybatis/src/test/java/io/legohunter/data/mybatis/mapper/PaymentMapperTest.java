package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PaymentMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDeleteFindByIdFindByTransactionIdAndFindAll() {
        Payment payment = payment(insertTransaction().getTransactionId(), "PAYPAL-1");
        paymentMapper.insert(payment);

        payment.setAmount(new BigDecimal("130.00000"));
        payment.setPaymentPlatformTransactionId("PAYPAL-UPDATED");
        paymentMapper.update(payment);

        assertThat(paymentMapper.findById(payment.getPaymentId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getAmount()).isEqualByComparingTo("130.00000");
                    assertThat(found.getPaymentPlatformTransactionId()).isEqualTo("PAYPAL-UPDATED");
                });
        assertThat(paymentMapper.findByTransactionId(payment.getTransactionId()))
                .extracting(Payment::getPaymentId)
                .containsExactly(payment.getPaymentId());
        assertThat(paymentMapper.findAll())
                .extracting(Payment::getPaymentPlatformTransactionId)
                .contains("PAYPAL-UPDATED");

        Payment migratedPayment = Payment.builder()
                .paymentId(10L)
                .paymentDate(LocalDate.parse("2026-01-02"))
                .transactionId(payment.getTransactionId())
                .currencyCode("USD")
                .sellerCurrencyCode("EUR")
                .exchangeRate(new BigDecimal("1.12000"))
                .amount(new BigDecimal("45.67000"))
                .paymentPlatformId(1)
                .paymentPlatformTransactionId("PAYPAL-MIGRATED")
                .build();
        paymentMapper.migrate(migratedPayment);
        assertThat(paymentMapper.findById(10L)).isPresent();

        migratedPayment.setAmount(new BigDecimal("50.00000"));
        migratedPayment.setPaymentPlatformTransactionId("PAYPAL-UPSERTED");
        paymentMapper.upsert(migratedPayment);
        assertThat(paymentMapper.findById(10L))
                .hasValueSatisfying(found -> {
                    assertThat(found.getAmount()).isEqualByComparingTo("50.00000");
                    assertThat(found.getPaymentPlatformTransactionId()).isEqualTo("PAYPAL-UPSERTED");
                });

        paymentMapper.delete(10L);
        assertThat(paymentMapper.findById(10L)).isEmpty();

        paymentMapper.deleteByTransactionId(payment.getTransactionId());
        assertThat(paymentMapper.findByTransactionId(payment.getTransactionId())).isEmpty();
    }
}
