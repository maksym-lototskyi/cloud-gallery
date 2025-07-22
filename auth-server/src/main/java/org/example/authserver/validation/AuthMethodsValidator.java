package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.authserver.model.AuthMethod;
import org.example.authserver.repository.AuthMethodRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthMethodsValidator implements ConstraintValidator<ValidAuthMethods, String[]> {

    private final AuthMethodRepository repository;
    private static final Set<String> SUPPORTED_AUTH_METHODS = Set.of(
            "client_secret_basic",
            "client_secret_post",
            "client_secret_jwt",
            "private_key_jwt",
            "none"
    );


    public AuthMethodsValidator(AuthMethodRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(String[] authMethods, ConstraintValidatorContext context) {
        if (authMethods == null || authMethods.length == 0) {
            return true;
        }

        Set<String> allowedMethodsInDb = repository.findAll().stream()
                .map(AuthMethod::getName)
                .collect(Collectors.toSet());

        Set<String> invalid = Arrays.stream(authMethods)
                .filter(method ->
                        method == null ||
                                method.isBlank() ||
                                !SUPPORTED_AUTH_METHODS.contains(method) ||
                                !allowedMethodsInDb.contains(method)
                )
                .collect(Collectors.toSet());

        if (!invalid.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Unsupported authentication methods: " + String.join(", ", invalid)
            ).addConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "List of supported authentication methods: " + String.join(", ", allowedMethodsInDb)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

}

