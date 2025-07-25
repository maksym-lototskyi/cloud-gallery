package org.example.authserver.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.authserver.repository.ClientRepository;
import org.example.authserver.validation.annotations.UniqueClientId;
import org.springframework.stereotype.Component;

@Component
public class UniqueClientIdValidator implements ConstraintValidator<UniqueClientId, String> {
    private final ClientRepository clientRepository;

    public UniqueClientIdValidator(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !clientRepository.existsByClientId(s);
    }
}
