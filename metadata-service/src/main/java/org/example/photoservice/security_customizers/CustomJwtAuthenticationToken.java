package org.example.photoservice.security_customizers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {
    private final UUID userId;

    public CustomJwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Jwt jwt, UUID userId) {
        super(jwt, authorities);
        this.userId = userId;
        setAuthenticated(true);
    }

    public UUID getUserId() {
        return userId;
    }
}
