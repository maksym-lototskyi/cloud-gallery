package org.example.authserver.mapper;

import org.example.authserver.dto.UserRequestDto;
import org.example.authserver.dto.UserResponseDto;
import org.example.authserver.model.Role;
import org.example.authserver.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public class UserMapper {

    public static User mapToUser(UserRequestDto dto, List<Role> roles, PasswordEncoder encoder) {
        return User.builder()
                .userId(UUID.randomUUID())
                .roles(roles)
                .password(encoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .build();
    }

    public static UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }
}
