package org.example.authserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class AuthMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "client_auth_methods",
            joinColumns = @JoinColumn(name = "auth_method_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    private List<Client> clients;
}
