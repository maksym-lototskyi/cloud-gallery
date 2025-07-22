package org.example.authserver.mapper;

import org.example.authserver.dto.ClientRequestDto;
import org.example.authserver.model.AuthMethod;
import org.example.authserver.model.Client;
import org.example.authserver.model.RedirectUri;
import org.example.authserver.model.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientMapper {
    public static Client mapToClient(RegisteredClient registeredClient,
                                     List<AuthMethod> authMethods,
                                     List<Scope> scopes,
                                     PasswordEncoder passwordEncoder){
        return Client.builder()
                .id(UUID.fromString(registeredClient.getId()))
                .clientId(registeredClient.getClientId())
                .clientName(registeredClient.getClientName())
                .secret(passwordEncoder.encode(registeredClient.getClientSecret()))
                .postLogoutRedirectUri(registeredClient.getPostLogoutRedirectUris()
                        .stream()
                        .map(RedirectUri::new)
                        .toList())
                .redirectUris(registeredClient.getRedirectUris()
                        .stream()
                        .map(RedirectUri::new)
                        .toList())
                .authMethods(authMethods)
                .scopes(scopes)
                .build();
    }

    public static RegisteredClient mapToRegisteredClient(Client client) {
        return RegisteredClient.withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .clientSecret(client.getSecret())
                .redirectUris(uris -> uris.addAll(client.getRedirectUris()
                        .stream()
                        .map(RedirectUri::getUri)
                        .collect(Collectors.toSet())))
                .postLogoutRedirectUris(uris -> uris.addAll(client.getPostLogoutRedirectUri()
                        .stream()
                        .map(RedirectUri::getUri)
                        .toList()))
                .clientAuthenticationMethods(methods -> methods.addAll(client.getAuthMethods()
                        .stream()
                        .map(auth -> ClientAuthenticationMethod.valueOf(auth.getName()))
                        .toList()))
                .scopes(scopes -> scopes.addAll(client.getScopes()
                        .stream()
                        .map(Scope::getScopeName)
                        .toList()))
                .build();
    }

    public static RegisteredClient mapToRegisteredClient(ClientRequestDto requestDto){
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(requestDto.getClientId())
                .clientSecret(requestDto.getClientSecret())
                .redirectUris(uris -> uris.addAll(requestDto.getRedirectUris()))
                .postLogoutRedirectUris(uris -> uris.addAll(requestDto.getPostLogoutRedirectUris()))
                .clientAuthenticationMethods(methods -> methods.addAll(
                        Arrays.stream(requestDto.getAuthMethods())
                                .map(ClientAuthenticationMethod::new)
                                .toList()))
                .scopes(scopes -> scopes.addAll(requestDto.getScopes()))
                .build();
    }
}
