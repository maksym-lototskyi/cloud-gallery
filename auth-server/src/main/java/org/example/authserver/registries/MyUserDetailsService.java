package org.example.authserver.registries;

import org.example.authserver.CustomUserDetails;
import org.example.authserver.model.Role;
import org.example.authserver.model.User;
import org.example.authserver.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toArray(String[]::new))
                .build();*/
        User user = User.builder()
                .userId(UUID.randomUUID())
                .roles(List.of(Role.builder()
                        .name("USER")
                        .build()))
                .username("bob")
                .password("123")
                .build();

        return new CustomUserDetails(user);
    }
}
