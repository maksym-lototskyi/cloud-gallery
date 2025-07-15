package org.example.photoservice.helpers;

import org.example.photoservice.security_customizers.CustomPrincipal;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class UserIdExtractor {
    public static UUID extractUserIdFromAuthentication(Authentication authentication){
        if (authentication.getPrincipal() instanceof CustomPrincipal customPrincipal) {
            return customPrincipal.getUserId();
        } else {
            throw new IllegalArgumentException("Authentication principal is not of type CustomPrincipal");
        }
    }
}
