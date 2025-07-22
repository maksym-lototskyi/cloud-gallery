package org.example.authserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class ClientResponseDto {
    private String clientId;
    private Set<String> redirectUris;
    private List<String> grantTypes;
    private List<String> authMethods;
    private Set<String> scopes;
    private Set<String> postLogoutRedirectUris;
}
