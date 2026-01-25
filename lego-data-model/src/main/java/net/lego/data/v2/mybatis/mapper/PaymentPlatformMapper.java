package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.PaymentPlatform;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface PaymentPlatformMapper {
    @Select("""
            SELECT payment_platform_id,
                   payment_platform_name,
                   payment_platform_url
            FROM payment_platform
            WHERE payment_platform_id = #{paymentPlatformId}
            """)
    Optional<PaymentPlatform> findPaymentPlatformById(Integer paymentPlatformId);

    @Select("""
            SELECT payment_platform_id,
                   payment_platform_name,
                   payment_platform_url
            FROM payment_platform
            WHERE payment_platform_name = #{paymentPlatformName}
            """)
    Optional<PaymentPlatform> findPaymentPlatformByName(Integer paymentPlatformName);

    @Insert("""
            INSERT INTO payment_platform(payment_platform_id, payment_platform_name, payment_platform_url)
            VALUES (#{paymentPlatformId}, #{paymentPlatformName}, #{paymentPlatformUrl})
            """)
    void insert(PaymentPlatform paymentPlatform);

    @Update("""
            UPDATE payment_platform
            SET payment_platform_id = #{paymentPlatformId},
                payment_platform_name = #{paymentPlatformName},
                payment_platform_url = #{paymentPlatformUrl}            
            WHERE payment_platform_id = #{paymentPlatformId}
            """)
    void update(PaymentPlatform paymentPlatform);

    @Select("""
            SELECT payment_platform_id,
                   payment_platform_name,
                   payment_platform_url
            FROM payment_platform
            """)
    List<PaymentPlatform> findAll();
}
