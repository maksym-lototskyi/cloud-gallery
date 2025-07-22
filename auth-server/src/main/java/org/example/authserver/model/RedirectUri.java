package org.example.authserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class RedirectUri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uri;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public RedirectUri(String uri) {
        this.uri = uri;
    }
}
