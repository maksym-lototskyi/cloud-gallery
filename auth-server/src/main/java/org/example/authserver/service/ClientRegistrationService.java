package org.example.authserver.service;

import org.example.authserver.dto.ClientRequestDto;
import org.example.authserver.dto.ClientResponseDto;
import org.example.authserver.mapper.ClientMapper;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientRegistrationService {
    private final RegisteredClientRepository clientRepository;

    public ClientRegistrationService(RegisteredClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponseDto registerClient(ClientRequestDto client) {
        RegisteredClient registeredClient = ClientMapper.mapToRegisteredClient(client);
        clientRepository.save(registeredClient);
        return ClientMapper.mapToResponseDto(registeredClient);
    }
}
