package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Payment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface PaymentMapper {

    @Insert("""
            INSERT INTO payment (
                payment_date,
                transaction_id,
                currency_code,
                seller_currency_code,
                exchange_rate,
                amount,
                payment_platform_id,
                payment_platform_transaction_id
            )
            VALUES (
                #{paymentDate},
                #{transactionId},
                #{currencyCode},
                #{sellerCurrencyCode},
                #{exchangeRate},
                #{amount},
                #{paymentPlatformId},
                #{paymentPlatformTransactionId}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "paymentId")
    void insert(Payment payment);

    @Insert("""
            INSERT INTO payment (
                payment_id,
                payment_date,
                transaction_id,
                currency_code,
                seller_currency_code,
                exchange_rate,
                amount,
                payment_platform_id,
                payment_platform_transaction_id
            )
            VALUES (
                #{paymentId},
                #{paymentDate},
                #{transactionId},
                #{currencyCode},
                #{sellerCurrencyCode},
                #{exchangeRate},
                #{amount},
                #{paymentPlatformId},
                #{paymentPlatformTransactionId}
            )
            """)
    void migrate(Payment payment);

    @Update("""
            UPDATE payment
            SET payment_date = #{paymentDate},
                transaction_id = #{transactionId},
                currency_code = #{currencyCode},
                seller_currency_code = #{sellerCurrencyCode},
                exchange_rate = #{exchangeRate},
                amount = #{amount},
                payment_platform_id = #{paymentPlatformId},
                payment_platform_transaction_id = #{paymentPlatformTransactionId}
            WHERE payment_id = #{paymentId}
            """)
    void update(Payment payment);

    @Delete("""
            DELETE FROM payment
            WHERE payment_id = #{paymentId}
            """)
    void delete(Long paymentId);

    @Delete("""
            DELETE FROM payment
            WHERE transaction_id = #{transactionId}
            """)
    void deleteByTransactionId(Long transactionId);

    @Select("""
            SELECT payment_id,
                   payment_date,
                   transaction_id,
                   currency_code,
                   seller_currency_code,
                   exchange_rate,
                   amount,
                   payment_platform_id,
                   payment_platform_transaction_id
            FROM payment
            """)
    @ResultMap("paymentResultMap")
    List<Payment> findAll();

    @Select("""
            SELECT payment_id,
                   payment_date,
                   transaction_id,
                   currency_code,
                   seller_currency_code,
                   exchange_rate,
                   amount,
                   payment_platform_id,
                   payment_platform_transaction_id
            FROM payment
            WHERE payment_id = #{paymentId}
            """)
    @ResultMap("paymentResultMap")
    Optional<Payment> findById(Long paymentId);

    @Select("""
            SELECT payment_id,
                   payment_date,
                   transaction_id,
                   currency_code,
                   seller_currency_code,
                   exchange_rate,
                   amount,
                   payment_platform_id,
                   payment_platform_transaction_id
            FROM payment
            WHERE transaction_id = #{transactionId}
            ORDER BY payment_id
            """)
    @ResultMap("paymentResultMap")
    List<Payment> findByTransactionId(Long transactionId);
}
