package com.example.erp.service;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    
    
}