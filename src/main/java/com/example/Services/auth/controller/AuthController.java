package com.example.Services.auth.controller;

import com.example.Services.auth.dto.JwtDto;
import com.example.Services.auth.service.JwtService;
import com.example.Services.model.Customer;
import com.example.Services.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Customer customer) {
        customerService.save(customer);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody Customer customer) {
        return ResponseEntity.ok(new JwtDto(jwtService.generateToken(customer)));
    }
}
