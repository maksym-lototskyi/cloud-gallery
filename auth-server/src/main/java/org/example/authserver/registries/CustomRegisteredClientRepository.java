package org.example.authserver.registries;

import org.example.authserver.mapper.ClientMapper;
import org.example.authserver.model.AuthMethod;
import org.example.authserver.model.Client;
import org.example.authserver.model.Scope;
import org.example.authserver.repository.AuthMethodRepository;
import org.example.authserver.repository.ClientRepository;
import org.example.authserver.repository.ScopeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomRegisteredClientRepository implements RegisteredClientRepository {
    private final ScopeRepository scopeRepository;
    private final AuthMethodRepository authMethodRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomRegisteredClientRepository(ScopeRepository scopeRepository, AuthMethodRepository authMethodRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.scopeRepository = scopeRepository;
        this.authMethodRepository = authMethodRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        List<Scope> scopes = scopeRepository.findAllByScopeName(registeredClient.getScopes());
        List<AuthMethod> authMethods = authMethodRepository.findAllByMethodName(registeredClient.getClientAuthenticationMethods()
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .toList());

        clientRepository.save(ClientMapper.mapToClient(registeredClient, authMethods, scopes, passwordEncoder));
    }

    @Override
    public RegisteredClient findById(String id) {
        Client client = clientRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));
        return ClientMapper.mapToRegisteredClient(client);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with clientId: " + clientId));
        return ClientMapper.mapToRegisteredClient(client);
    }
}
