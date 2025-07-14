package org.example.authserver.repository;

import org.example.authserver.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUserId(UUID userUUID);

    Optional<User> findByUserId(UUID userUUID);
}
