package net.lego.data.v2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lego.data.v2.dao.ExternalItemDao;
import net.lego.data.v2.dao.ExternalServiceDao;
import org.springframework.stereotype.Component;

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
                .flatMap(_ -> externalItemDao.findByExternalServiceNumber(value.toString(), externalServiceName))
                .map(_ -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Item Number was not found in external service %s", externalServiceName))
                            .addConstraintViolation();
                    return false;
                });
    }
}