package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;

import java.util.Set;

public class AuthMethodsValidator implements ConstraintValidator<ValidAuthMethods, String[]> {
    private static final Set<String> ALLOWED_AUTH_METHODS = Set.of(
            "client_secret_basic",
            "client_secret_post",
            "client_secret_jwt",
            "private_key_jwt",
            "none"
    );

    @Override
    public boolean isValid(String[] authMethods, jakarta.validation.ConstraintValidatorContext context) {
        if (authMethods == null || authMethods.length == 0) {
            return false;
        }
        for (String method : authMethods) {
            if(method == null || method.isBlank() || !ALLOWED_AUTH_METHODS.contains(method)){
                return false;
            }
        }
        return true;
    }
}
