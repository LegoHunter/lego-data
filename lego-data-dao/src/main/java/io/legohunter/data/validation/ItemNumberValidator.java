package io.legohunter.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.dao.ExternalCatalogItemDao;
import io.legohunter.data.dao.ExternalServiceDao;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemNumberValidator implements ConstraintValidator<ItemNumberExists, CharSequence> {
    private final ExternalCatalogItemDao externalCatalogItemDao;
    private final ExternalServiceDao externalServiceDao;
    private String externalServiceCode;

    @Override
    public void initialize(ItemNumberExists annotation) {
        externalServiceCode = annotation.externalService();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        return externalServiceDao.findByServiceCode(externalServiceCode)
                .flatMap(externalService -> externalCatalogItemDao.findByExternalServiceIdAndExternalItemKey(externalService.getExternalServiceId(), value.toString()))
                .map(externalCatalogItem -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Item Number was not found in external service %s", externalServiceCode))
                            .addConstraintViolation();
                    return false;
                });
    }
}
