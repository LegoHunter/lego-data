package io.legohunter.data.validation;

import io.legohunter.data.dao.PaymentPlatformDao;
import io.legohunter.data.dto.PaymentPlatform;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentPlatformValidator implements ConstraintValidator<PaymentPlatformExists, CharSequence> {
    private List<String> acceptedValues;
    private final PaymentPlatformDao paymentPlatformDao;

    @Override
    public void initialize(PaymentPlatformExists annotation) {
        acceptedValues = paymentPlatformDao.findAll().stream()
                .map(PaymentPlatform::getPaymentPlatformName)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return paymentPlatformDao.findPaymentPlatformByName(value.toString())
                .map(paymentPlatform -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Payment Platform [%s] is invalid. Must be one of %s", value, acceptedValues))
                            .addConstraintViolation();
                    return false;
                });
    }
}
