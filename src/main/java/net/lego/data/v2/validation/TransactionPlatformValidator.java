package net.lego.data.v2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lego.data.v2.dao.TransactionPlatformDao;
import net.lego.data.v2.dto.TransactionPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionPlatformValidator implements ConstraintValidator<TransactionPlatformExists, CharSequence> {
    private List<String> acceptedValues;
    private final TransactionPlatformDao transactionPlatformDao;

    @Override
    public void initialize(TransactionPlatformExists annotation) {
        acceptedValues = transactionPlatformDao.findAll().stream()
                .map(TransactionPlatform::getTransactionPlatformName)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }

        return transactionPlatformDao.findTransactionPlatformByName(value.toString())
                .map(_ -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Transaction Platform [%s] is invalid. Must be one of %s", value, acceptedValues))
                            .addConstraintViolation();
                    return false;
                });
    }
}
