package io.legohunter.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dao.TransactionTypeDao;
import io.legohunter.data.dto.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionTypeValidator implements ConstraintValidator<TransactionTypeExists, CharSequence> {
    private List<String> acceptedValues;
    private final TransactionTypeDao transactionTypeDao;

    @Override
    public void initialize(TransactionTypeExists annotation) {
        acceptedValues = transactionTypeDao.findAll().stream()
                .map(TransactionType::getTransactionTypeCode)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }

        return transactionTypeDao.findTransactionTypeByCode(value.toString())
                .map(_ -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Transaction Type [%s] is invalid. Must be one of %s", value, acceptedValues))
                            .addConstraintViolation();
                    return false;
                });
    }
}