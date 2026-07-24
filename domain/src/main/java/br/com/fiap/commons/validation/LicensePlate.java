package br.com.fiap.commons.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LicensePlate.LicensePlateValidator.class)
@Documented
public @interface LicensePlate {

    String message() default "Invalid license plate format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class LicensePlateValidator implements ConstraintValidator<LicensePlate, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            return value.matches("^[A-Z]{3}\\d[A-Z0-9]\\d{2}$") ||
                    value.matches("^[A-Z]{3}\\d{4}$");
        }
    }
}
