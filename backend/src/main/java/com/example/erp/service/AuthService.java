package com.example.erp.service;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.RegisterRequest;

public interface AuthService {

	LoginResponse login(LoginRequest request);

	void register(RegisterRequest request);
}