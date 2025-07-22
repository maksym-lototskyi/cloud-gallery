package org.example.authserver.repository;

import org.example.authserver.model.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ScopeRepository extends JpaRepository<Scope, Long> {
    @Query("""
        SELECT s FROM Scope s WHERE s.scopeName in :scopes
        """)
    List<Scope> findAllByScopeName(Iterable<String> scopes);
}
