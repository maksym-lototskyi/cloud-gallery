package org.example.authserver.repository;

import org.example.authserver.model.GrantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GrantTypeRepository extends JpaRepository<GrantType, Long> {
    @Query("""
            SELECT g FROM GrantType g WHERE  g.name in :names
           """)
    List<GrantType> findAllByName(Iterable<String> names);
}
