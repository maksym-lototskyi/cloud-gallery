package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;

import org.example.authserver.model.GrantType;
import org.example.authserver.repository.GrantTypeRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Set;
import java.util.stream.Collectors;

public class GrantTypesValidator implements ConstraintValidator<ValidGrantTypes, List<String>> {
    private final GrantTypeRepository grantTypeRepository;
    private static final Set<String> ALLOWED_GRANT_TYPES = Set.of(
            AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
            AuthorizationGrantType.REFRESH_TOKEN.getValue(),
            AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()
    );

    public GrantTypesValidator(GrantTypeRepository grantTypeRepository) {
        this.grantTypeRepository = grantTypeRepository;
    }

    @Override
    public boolean isValid(List<String> grantTypes, ConstraintValidatorContext context) {
        if(grantTypes == null){
            return true;
        }
        Set<String> invalidGrantTypes = grantTypes.stream()
                .filter(gt -> gt == null || !ALLOWED_GRANT_TYPES.contains(gt))
                .filter(gt -> !grantTypeRepository.findAll()
                        .stream()
                        .map(GrantType::getName)
                        .collect(Collectors.toCollection(HashSet::new))
                        .contains(gt))
                .collect(Collectors.toSet());

        if (!invalidGrantTypes.isEmpty()) {
            context.disableDefaultConstraintViolation();
            String message = "Unsupported grant types: " + String.join(", ", invalidGrantTypes);
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate("List of supported grant types: "+ String.join(", ", grantTypes))
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}

