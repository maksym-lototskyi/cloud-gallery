package org.example.authserver.controller;

import jakarta.validation.Valid;
import org.example.authserver.dto.ClientRequestDto;
import org.example.authserver.dto.ClientResponseDto;
import org.example.authserver.dto.UserRequestDto;
import org.example.authserver.dto.UserResponseDto;
import org.example.authserver.service.ClientRegistrationService;
import org.example.authserver.service.UserRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final UserRegistrationService userRegistrationService;
    private final ClientRegistrationService clientRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService, ClientRegistrationService clientRegistrationService) {
        this.userRegistrationService = userRegistrationService;
        this.clientRegistrationService = clientRegistrationService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto dto) {
        var body = userRegistrationService.registerUser(dto);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }


    @PostMapping("/client")
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientRequestDto requestDto) {
        var result = clientRegistrationService.registerClient(requestDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException .class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handle(MethodArgumentNotValidException e){
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                FieldError::getDefaultMessage, Collectors.toList()
                        )
                ));
    }
}
