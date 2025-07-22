package org.example.authserver.mapper;

import org.example.authserver.dto.ClientRequestDto;
import org.example.authserver.dto.ClientResponseDto;
import org.example.authserver.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.*;
import java.util.stream.Collectors;

public class ClientMapper {
    public static Client mapToClient(RegisteredClient registeredClient,
                                     List<AuthMethod> authMethods,
                                     List<Scope> scopes,
                                     List<GrantType> grantTypes,
                                     PasswordEncoder passwordEncoder){

        Client client =  Client.builder()
                .id(UUID.fromString(registeredClient.getId()))
                .clientId(registeredClient.getClientId())
                .clientName(registeredClient.getClientName())
                .secret(passwordEncoder.encode(registeredClient.getClientSecret()))
                .authMethods(authMethods)
                .scopes(scopes)
                .grantTypes(grantTypes)
                .build();

        List<RedirectUri> redirectUris = registeredClient.getRedirectUris()
                .stream()
                .map(u -> RedirectUri.builder()
                        .uri(u)
                        .client(client)
                        .build())
                .toList();

        List<PostLogoutRedirectUri> postLogoutRedirectUris = registeredClient.getPostLogoutRedirectUris()
                .stream()
                .map(uri -> PostLogoutRedirectUri.builder()
                        .client(client)
                        .uri(uri)
                        .build())
                .toList();

        client.setRedirectUris(redirectUris);
        client.setPostLogoutRedirectUri(postLogoutRedirectUris);
        return client;
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
                        .map(PostLogoutRedirectUri::getUri)
                        .toList()))
                .clientAuthenticationMethods(methods -> methods.addAll(client.getAuthMethods()
                        .stream()
                        .map(auth -> ClientAuthenticationMethod.valueOf(auth.getName()))
                        .toList()))
                .scopes(scopes -> scopes.addAll(client.getScopes()
                        .stream()
                        .map(Scope::getScopeName)
                        .toList()))
                .authorizationGrantTypes(grants -> grants.addAll(client.getGrantTypes().stream()
                        .map(g -> new AuthorizationGrantType(g.getName()))
                        .toList()))
                .build();
    }

    public static ClientResponseDto mapToResponseDto(RegisteredClient client){
        return ClientResponseDto.builder()
                .grantTypes(client.getAuthorizationGrantTypes().stream().map(AuthorizationGrantType::getValue).toList())
                .authMethods(client.getClientAuthenticationMethods().stream().map(ClientAuthenticationMethod::getValue).toList())
                .scopes(client.getScopes())
                .redirectUris(client.getRedirectUris())
                .postLogoutRedirectUris(client.getPostLogoutRedirectUris())
                .clientId(client.getClientId())
                .build();
    }

    public static RegisteredClient mapToRegisteredClient(ClientRequestDto requestDto){
        List<String> postLogoutUris = isEmptyOrNull(requestDto.getPostLogoutRedirectUris()) ?
                new ArrayList<>() : requestDto.getPostLogoutRedirectUris();

        List<ClientAuthenticationMethod> authMethods = requestDto.getAuthMethods() == null || requestDto.getAuthMethods().length == 0 ?
                List.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) :
                Arrays.stream(requestDto.getAuthMethods()).map(ClientAuthenticationMethod::new).toList();

        List<AuthorizationGrantType> grantTypes = isEmptyOrNull(requestDto.getGrantTypes())?
                List.of(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN) :
                requestDto.getGrantTypes().stream().map(AuthorizationGrantType::new).toList();

        List<String> clientScopes = isEmptyOrNull(requestDto.getScopes()) ?
                List.of(OidcScopes.OPENID) :
                requestDto.getScopes();

        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(requestDto.getClientId())
                .clientSecret(requestDto.getClientSecret())
                .redirectUris(uris -> uris.addAll(requestDto.getRedirectUris()))
                .postLogoutRedirectUris(uris -> uris.addAll(postLogoutUris))
                .clientAuthenticationMethods(methods -> methods.addAll(authMethods))
                .scopes(scopes -> scopes.addAll(clientScopes))
                .authorizationGrantTypes(types -> types.addAll(grantTypes))
                .build();
    }

    private static  <T> boolean isEmptyOrNull(Collection<T> collection){
        return collection == null || collection.isEmpty();
    }
}
