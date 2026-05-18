package net.lego.data.v2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.lego.data.v2.validation.TransactionTypeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TransactionTypeValidator.class)
public @interface TransactionTypeExists {

    String message() default "Must be a valid transaction type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
