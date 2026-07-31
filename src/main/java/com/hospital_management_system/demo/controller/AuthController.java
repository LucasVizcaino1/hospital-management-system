package com.hospital_management_system.demo.controller;

import com.hospital_management_system.demo.dto.request.AuthLoginRequestDto;
import com.hospital_management_system.demo.dto.request.AuthRegisterRequestDto;
import com.hospital_management_system.demo.dto.response.AuthResponseDto;
import com.hospital_management_system.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthLoginRequestDto request) {
        log.info("Login request received for username={}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRegisterRequestDto request) {
        log.info("Register request received for username={}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}