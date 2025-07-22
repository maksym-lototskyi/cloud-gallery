package org.example.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.authserver.validation.*;

import java.util.List;

@Getter
@Setter
public class ClientRequestDto {
    @NotNull
    @NotBlank
    @UniqueClientId
    private String clientId;
    @NotNull
    @NotBlank
    private String clientSecret;
    @ValidAuthMethods
    private String[] authMethods;
    @NotNull
    @NotEmpty
    @ValidUris
    private List<String> redirectUris;
    @ValidUris
    private List<String> postLogoutRedirectUris;
    @ValidScopes
    private List<String> scopes;
    @ValidGrantTypes
    private List<String> grantTypes;
}
