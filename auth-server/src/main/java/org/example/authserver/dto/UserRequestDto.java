package org.example.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.example.authserver.validation.annotations.UniqueUsername;

@Getter
public class UserRequestDto {
    @NotBlank
    @UniqueUsername
    private String username;
    @NotBlank
    private String password;
}
