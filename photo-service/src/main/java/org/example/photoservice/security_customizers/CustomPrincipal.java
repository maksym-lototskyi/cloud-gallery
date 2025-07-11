package org.example.photoservice.security_customizers;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CustomPrincipal {
    private final UUID userId;
    private final String username;
    private final List<String> roles;

    public CustomPrincipal(UUID userId, String username, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }


}
