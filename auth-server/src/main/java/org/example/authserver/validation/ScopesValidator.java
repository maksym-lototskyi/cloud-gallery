package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.authserver.model.Scope;
import org.example.authserver.repository.ScopeRepository;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScopesValidator implements ConstraintValidator<ValidScopes, List<String>> {
    private static final Set<String> ALLOWED_OIDC_SCOPES = Set.of(
            "openid",
            "profile",
            "email",
            "address",
            "phone"
    );
    private final ScopeRepository repository;

    public ScopesValidator(ScopeRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(List<String> scopes, ConstraintValidatorContext context) {
        if(scopes == null || scopes.isEmpty()){
            return true;
        }
        if (!scopes.contains(OidcScopes.OPENID)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("'openid' scope is required for OpenID Connect")
                    .addConstraintViolation();
            return false;
        }

        Set<String> allowedScopes = repository.findAll()
                .stream()
                .map(Scope::getScopeName)
                .collect(Collectors.toCollection(HashSet::new));

        List<String> invalidScopes = scopes.stream()
                .filter(scope -> scope == null || scope.isBlank() || !ALLOWED_OIDC_SCOPES.contains(scope))
                .filter(scope -> !allowedScopes.contains(scope))
                .toList();

        if (!invalidScopes.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Unsupported scopes: " + String.join(", ", invalidScopes))
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate("List of supported scopes: " + String.join(", ", allowedScopes));
            return false;
        }

        return true;
    }

}
