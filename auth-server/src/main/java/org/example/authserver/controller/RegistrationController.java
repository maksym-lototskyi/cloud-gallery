package org.example.authserver.controller;

import org.example.authserver.dto.UserRequestDto;
import org.example.authserver.dto.UserResponseDto;
import org.example.authserver.service.UserRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto dto) {
        var body = userRegistrationService.registerUser(dto);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }


    @PostMapping("/client")
    public String registerClient() {
        // Logic to register a client
        return "Client registered successfully";
    }
}
