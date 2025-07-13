package org.example.authserver.service;

import org.example.authserver.dto.UserRequestDto;
import org.example.authserver.dto.UserResponseDto;
import org.example.authserver.mapper.UserMapper;
import org.example.authserver.model.Role;
import org.example.authserver.model.User;
import org.example.authserver.repository.RoleRepository;
import org.example.authserver.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public UserRegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UserResponseDto registerUser(UserRequestDto dto){
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
        User saved = userRepository.save(UserMapper.mapToUser(dto, List.of(role), passwordEncoder));

        System.out.println("User created with ID: " + saved.getId());
        rabbitTemplate.convertAndSend("folder.creation.exchange", "folder.create.metadata", saved.getUserId());
        return UserMapper.mapToResponseDto(saved);
    }
}
