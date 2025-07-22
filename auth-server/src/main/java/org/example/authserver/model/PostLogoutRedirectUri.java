package org.example.authserver.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostLogoutRedirectUri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uri;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public PostLogoutRedirectUri(String uri) {
        this.uri = uri;
    }
}
