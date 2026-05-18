package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PaymentPlatform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PaymentPlatformMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByIdFindByNameAndFindAll() {
        PaymentPlatform paymentPlatform = PaymentPlatform.builder()
                .paymentPlatformId(1)
                .paymentPlatformName("PayPal")
                .paymentPlatformUrl("https://paypal.example")
                .build();

        paymentPlatformMapper.insert(paymentPlatform);
        paymentPlatform.setPaymentPlatformUrl("https://paypal.test");
        paymentPlatformMapper.update(paymentPlatform);

        assertThat(paymentPlatformMapper.findPaymentPlatformById(1))
                .hasValueSatisfying(found -> assertThat(found.getPaymentPlatformUrl()).isEqualTo("https://paypal.test"));
        assertThat(paymentPlatformMapper.findPaymentPlatformByName("PayPal"))
                .hasValueSatisfying(found -> assertThat(found.getPaymentPlatformId()).isEqualTo(1));
        assertThat(paymentPlatformMapper.findAll()).extracting(PaymentPlatform::getPaymentPlatformName).containsExactly("PayPal");
    }
}
