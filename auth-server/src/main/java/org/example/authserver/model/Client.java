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

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(nullable = false)
    private String secret;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RedirectUri> redirectUris;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PostLogoutRedirectUri> postLogoutRedirectUri;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_auth_methods",
            inverseJoinColumns = @JoinColumn(name = "auth_method_id"),
            joinColumns = @JoinColumn(name = "client_id"))
    private List<AuthMethod> authMethods;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_scopes",
            inverseJoinColumns = @JoinColumn(name = "scope_id"),
            joinColumns = @JoinColumn(name = "client_id")
    )
    private List<Scope> scopes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_grant_types",
            inverseJoinColumns = @JoinColumn(name = "grant_type_id"),
            joinColumns = @JoinColumn(name = "client_id"))
    private List<GrantType> grantTypes;
}
