package io.legohunter.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dao.ConditionDao;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.validation.ConditionCodeExists;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConditionCodeValidator implements ConstraintValidator<ConditionCodeExists, CharSequence> {
    private List<String> acceptedValues;
    private final ConditionDao conditionDao;

    @Override
    public void initialize(ConditionCodeExists annotation) {
        acceptedValues = conditionDao.findAll().stream()
                .map(Condition::getConditionCode)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }

        return conditionDao.findByConditionCode(value.toString())
                .map(_ -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Must be one of %s", acceptedValues))
                            .addConstraintViolation();
                    return false;
                });
    }
}