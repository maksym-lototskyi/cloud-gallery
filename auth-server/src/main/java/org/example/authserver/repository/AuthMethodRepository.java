package org.example.authserver.repository;

import org.example.authserver.model.AuthMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long> {
    @Query("""
        SELECT a FROM AuthMethod a WHERE a.name in :clientAuthenticationMethods
    """)
    List<AuthMethod> findAllByMethodName(Iterable<String> clientAuthenticationMethods);
}
