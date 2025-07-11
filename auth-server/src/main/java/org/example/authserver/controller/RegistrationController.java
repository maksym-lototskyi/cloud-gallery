package org.example.authserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    @PostMapping("/user")
    public String registerUser() {
        // Logic to register a user
        return "User registered successfully";
    }


    @PostMapping("/client")
    public String registerClient() {
        // Logic to register a client
        return "Client registered successfully";
    }
}
