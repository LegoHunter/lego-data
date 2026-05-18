package io.legohunter.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dao.CostTypeDao;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.validation.CostTypeExists;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CostTypeValidator implements ConstraintValidator<CostTypeExists, CharSequence> {
    private List<String> acceptedValues;

    private final CostTypeDao costTypeDao;

    @Override
    public void initialize(CostTypeExists annotation) {
        acceptedValues = costTypeDao.findAll().stream()
                .map(CostType::getCostTypeCode)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return costTypeDao.findCostTypeByCode(value.toString()).map(_ -> true).orElseGet(() -> {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("Cost Type Code [%s] is invalid. Must be one of %s", value, acceptedValues))
                    .addConstraintViolation();
            return false;
        });
    }
}