package org.example.authserver.service;

import org.example.authserver.dto.UserRequestDto;
import org.example.authserver.dto.UserResponseDto;
import org.example.authserver.mapper.UserMapper;
import org.example.authserver.model.Role;
import org.example.authserver.model.User;
import org.example.authserver.repository.RoleRepository;
import org.example.authserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto registerUser(UserRequestDto dto){
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
        User saved = userRepository.save(UserMapper.mapToUser(dto, List.of(role), passwordEncoder));
        return UserMapper.mapToResponseDto(saved);
    }
}
