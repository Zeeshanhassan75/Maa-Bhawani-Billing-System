package com.maabhawani.billing.services;

import com.maabhawani.billing.dto.AuthenticationRequest;
import com.maabhawani.billing.dto.AuthenticationResponse;
import com.maabhawani.billing.dto.RegisterRequest;
import com.maabhawani.billing.models.Role;
import com.maabhawani.billing.models.User;
import com.maabhawani.billing.repositories.UserRepository;
import com.maabhawani.billing.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
                        AuthenticationManager authenticationManager) {
                this.repository = repository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authenticationManager = authenticationManager;
        }

        public AuthenticationResponse register(RegisterRequest request) {
                if (repository.findByUsername(request.getUsername()).isPresent()) {
                        throw new IllegalArgumentException("User with this username already exists");
                }

                var user = User.builder()
                                .fullname(request.getFullname())
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.ADMIN) // Default role for users
                                .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                var user = repository.findByUsername(request.getUsername())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
