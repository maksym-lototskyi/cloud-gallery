package org.example.authserver.validation.annotations;

import jakarta.validation.Constraint;
import org.example.authserver.validation.validators.GrantTypesValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GrantTypesValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGrantTypes {
    String message() default "Invalid grant types";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
