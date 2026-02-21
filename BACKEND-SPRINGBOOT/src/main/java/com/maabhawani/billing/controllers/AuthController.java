package com.maabhawani.billing.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // Hardcoded credentials for simplicity
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (USERNAME.equals(username) && PASSWORD.equals(password)) {
            // Generate a simple token (normally would be JWT)
            return ResponseEntity.ok(Map.of("token", "maabhawani-auth-token-xyz"));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }
}
