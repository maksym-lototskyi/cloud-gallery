package org.example.photoservice.security_customizers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.UUID;

public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new CustomGrantedAuthorityConverter();

    public CustomAuthenticationConverter() {
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        CustomPrincipal principal = CustomPrincipal.builder()
                .userId(UUID.fromString(jwt.getClaim("user_id")))
                .username(jwt.getClaim("sub"))
                .roles(jwt.getClaim("roles"))
                .build();
        return new CustomJwtAuthenticationToken(authorities, jwt, principal);
    }
}
