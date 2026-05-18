package io.legohunter.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dao.ExternalItemDao;
import io.legohunter.data.dao.ExternalServiceDao;
import org.springframework.stereotype.Component;

import static io.legohunter.data.dto.ExternalService.ExternalServiceType.BRICKLINK;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemNumberValidator implements ConstraintValidator<ItemNumberExists, CharSequence> {
    private final ExternalItemDao externalItemDao;
    private final ExternalServiceDao externalServiceDao;
    private String externalServiceName;

    @Override
    public void initialize(ItemNumberExists annotation) {
        externalServiceName = annotation.externalService();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        return externalServiceDao.findExternalServiceByName(externalServiceName)
                .flatMap(_ -> externalItemDao.findByExternalServiceAndNumber(BRICKLINK.getExternalServiceId(), externalServiceName))
                .map(_ -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Item Number was not found in external service %s", externalServiceName))
                            .addConstraintViolation();
                    return false;
                });
    }
}