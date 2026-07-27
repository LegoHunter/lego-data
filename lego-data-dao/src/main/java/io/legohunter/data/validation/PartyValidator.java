package io.legohunter.data.validation;

import io.legohunter.data.dao.PartyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
@Slf4j
public class PartyValidator implements ConstraintValidator<PartyExists, Long> {
    private final PartyDao partyDao;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return partyDao.findPartyById(value)
                .map(party -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Party [%s] was not found", value))
                            .addConstraintViolation();
                    return false;
                });
    }
}
