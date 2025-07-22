package org.example.photoservice.helpers;

import org.example.photoservice.security_customizers.CustomJwtAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class UserIdExtractor {
    public static UUID extractUserIdFromAuthentication(Authentication authentication){
        if (authentication instanceof CustomJwtAuthenticationToken authenticationToken) {
            return authenticationToken.getUserId();
        } else {
            throw new IllegalArgumentException("Authentication must be of type CustomJwtAuthenticationToken");
        }
    }
}
