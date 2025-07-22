package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.List;
import java.util.Set;

public class ScopesValidator implements ConstraintValidator<ValidScopes, List<String>> {
    private static final Set<String> ALLOWED_OIDC_SCOPES = Set.of(
            "openid",
            "profile",
            "email",
            "address",
            "phone"
    );
    @Override
    public boolean isValid(List<String> scopes, ConstraintValidatorContext context) {
        if (scopes == null || scopes.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Scopes list must not be empty and must include 'openid'")
                    .addConstraintViolation();
            return false;
        }

        if (!scopes.contains(OidcScopes.OPENID)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("'openid' scope is required for OpenID Connect")
                    .addConstraintViolation();
            return false;
        }

        List<String> invalidScopes = scopes.stream()
                .filter(scope -> scope == null || scope.isBlank() || !ALLOWED_OIDC_SCOPES.contains(scope))
                .toList();

        if (!invalidScopes.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid scopes: " + String.join(", ", invalidScopes))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
