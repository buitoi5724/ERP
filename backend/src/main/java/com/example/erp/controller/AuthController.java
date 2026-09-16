package com.example.erp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.RegisterRequest;
import com.example.erp.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
    	  System.out.println("===== LOGIN API =====");
    	    System.out.println(request.getUsername());
        return authService.login(request);
    }
    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return "Register success";
    }
    @GetMapping("/test")
    public String test() {
        System.out.println("TEST API");
        return "OK";
    }
}