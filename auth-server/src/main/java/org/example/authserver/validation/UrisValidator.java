package org.example.authserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UrisValidator implements ConstraintValidator<ValidUris, List<String>>{
    @Override
    public boolean isValid(List<String> uris, ConstraintValidatorContext context) {
        if(uris == null || uris.isEmpty()){
            return true;
        }
        List<String> invalidUris = new ArrayList<>();

        for (String uri : uris) {
            if (uri == null || uri.isBlank() || !isValidUri(uri)) {
                invalidUris.add(uri);
            }
        }

        if (!invalidUris.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Invalid redirect URIs: " + String.join(", ", invalidUris))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isValidUri(String uri) {
        try {
            URI parsed = new URI(uri);
            return parsed.isAbsolute() && parsed.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
