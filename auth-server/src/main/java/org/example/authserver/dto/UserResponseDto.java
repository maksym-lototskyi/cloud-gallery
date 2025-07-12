package org.example.authserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private String username;
    private UUID userId;
}
