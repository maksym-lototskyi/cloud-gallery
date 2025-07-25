package org.example.authserver.repository;

import org.example.authserver.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByClientId(String clientId);

    boolean existsByClientId(String clientId);
}
