package org.example.photoservice.config;

import org.example.photoservice.security_customizers.CustomAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                auth.anyRequest().hasRole("USER")
        )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwtConfigurer -> {
                            jwtConfigurer.jwtAuthenticationConverter(new CustomAuthenticationConverter());
                            jwtConfigurer.jwkSetUri(jwkSetUri);
                        }));
        return http.build();
    }
}
