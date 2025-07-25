package org.example.authserver.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.authserver.validation.validators.UniqueClientIdValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UniqueClientIdValidator.class)
public @interface UniqueClientId {
    String message() default "The client id should be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
