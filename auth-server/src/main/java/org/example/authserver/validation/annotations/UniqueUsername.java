package org.example.authserver.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.authserver.validation.validators.UniqueUsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "The username should be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
