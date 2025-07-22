package org.example.authserver.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    private UUID id;
    private String clientId;
    private String clientName;
    private String secret;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RedirectUri> redirectUris;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RedirectUri> postLogoutRedirectUri;
    @ManyToMany(mappedBy = "clients", fetch = FetchType.EAGER)
    private List<AuthMethod> authMethods;
    @ManyToMany(mappedBy = "clients", fetch = FetchType.EAGER)
    private List<Scope> scopes;
}
