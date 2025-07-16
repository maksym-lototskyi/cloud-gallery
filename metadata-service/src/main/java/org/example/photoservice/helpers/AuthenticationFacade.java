package org.example.photoservice.helpers;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthenticationFacade {
    public static UUID getUserId(){
        return UserIdExtractor.extractUserIdFromAuthentication(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }
}
