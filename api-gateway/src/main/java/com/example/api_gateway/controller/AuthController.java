package com.example.api_gateway.controller;

import com.example.api_gateway.dto.LoginRequest;
import com.example.api_gateway.dto.LoginResponse;
import com.example.api_gateway.security.JwtService;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            ReactiveAuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        var loginToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        return authenticationManager.authenticate(loginToken)
                .map(authentication -> {
                    String token = jwtService.generateToken(authentication);
                    return new LoginResponse(
                            token,
                            "Bearer",
                            jwtService.getExpirationSeconds()
                    );
                });
    }

    private LoginResponse createResponse(Authentication authentication) {

        String token = jwtService.generateToken(authentication);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    }
}
