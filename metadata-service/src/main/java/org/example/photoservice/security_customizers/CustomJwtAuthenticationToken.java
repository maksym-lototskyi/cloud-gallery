package org.example.photoservice.security_customizers;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends AbstractAuthenticationToken {
    private Jwt jwt;
    private CustomPrincipal principal;

    public CustomJwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Jwt jwt, CustomPrincipal principal) {
        super(authorities);
        this.jwt = jwt;
        this.principal = principal;
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return jwt.getTokenValue();
    }

    @Override
    public CustomPrincipal getPrincipal() {
        return this.principal;
    }
}
