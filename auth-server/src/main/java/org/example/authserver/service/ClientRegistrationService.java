package org.example.authserver.service;

import org.example.authserver.dto.ClientRequestDto;
import org.example.authserver.mapper.ClientMapper;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientRegistrationService {
    private final RegisteredClientRepository clientRepository;

    public ClientRegistrationService(RegisteredClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void registerClient(ClientRequestDto client) {
        clientRepository.save(ClientMapper.mapToRegisteredClient(client));
    }
}
