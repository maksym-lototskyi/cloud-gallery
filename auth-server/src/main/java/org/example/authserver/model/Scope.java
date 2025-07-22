package org.example.authserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Scope {
    @Id
    private Long id;
    private String scopeName;
    @ManyToMany
    @JoinTable(
        name = "client_scopes",
        joinColumns = @JoinColumn(name = "scope_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Client> clients;
}
