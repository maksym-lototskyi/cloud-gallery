package org.example.authserver.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.authserver.repository.UserRepository;
import org.example.authserver.validation.annotations.UniqueUsername;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserRepository userRepository;

    public UniqueUsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepository.existsByUsername(s);
    }
}
