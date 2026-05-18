package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.PaymentPlatform;
import io.legohunter.data.mybatis.mapper.PaymentPlatformMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentPlatformDao {

    private final PaymentPlatformMapper paymentPlatformMapper;

    public List<PaymentPlatform> findAll() {
        return paymentPlatformMapper.findAll();
    }

    public Optional<PaymentPlatform> findPaymentPlatformById(final Integer paymentPlatformId) {
        return paymentPlatformMapper.findPaymentPlatformById(paymentPlatformId);
    }

    public Optional<PaymentPlatform> findPaymentPlatformByName(final String paymentPlatformName) {
        return paymentPlatformMapper.findPaymentPlatformByName(paymentPlatformName);
    }

    public void insert(final PaymentPlatform paymentPlatform) {
        paymentPlatformMapper.insert(paymentPlatform);
    }

    public void update(final PaymentPlatform paymentPlatform) {
        paymentPlatformMapper.update(paymentPlatform);
    }
}
