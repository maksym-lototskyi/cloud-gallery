package org.example.authserver.repository;

import org.example.authserver.model.Role;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface RoleRepository extends ListCrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
