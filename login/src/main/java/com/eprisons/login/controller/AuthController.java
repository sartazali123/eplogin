package com.eprisons.login.controller;



import com.eprisons.login.dto.SignupRequest;
import com.eprisons.login.dto.SignupResponse;
import com.eprisons.login.service.JwtService;
import com.eprisons.login.service.SignupService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final SignupService signupService;

    // Constructor injection
    public AuthController(
            AuthenticationManager authManager,
            JwtService jwtService,
            SignupService signupService) {

        this.authManager = authManager;
        this.jwtService = jwtService;
        this.signupService = signupService;
    }

    // Login request DTO
    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    // Login response DTO
    public record LoginResponse(
            String token,
            String tokenType,
            String username,
            List<String> roles
    ) {}

    // =========================
    // SIGNUP API
    // =========================

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        SignupResponse response =
                signupService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // LOGIN API
    // =========================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authManager.authenticate(
                        UsernamePasswordAuthenticationToken
                                .unauthenticated(
                                        request.username(),
                                        request.password()
                                )
                );

        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .toList();

        String token =
                jwtService.generate(authentication);

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        "Bearer",
                        authentication.getName(),
                        roles
                )
        );
    }
}
