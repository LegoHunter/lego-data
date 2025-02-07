package net.lego.data.v2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lego.data.v2.dao.CostTypeDao;
import net.lego.data.v2.dto.CostType;
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
            context.buildConstraintViolationWithTemplate(String.format("Must be one of %s", acceptedValues))
                    .addConstraintViolation();
            return false;
        });
    }
}