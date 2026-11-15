package com.example.erp.controller;

import com.example.erp.dto.ForgotPasswordRequest;
import com.example.erp.dto.ResetPasswordRequest;
import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.RegisterRequest;

import com.example.erp.service.AuthService;
import com.example.erp.service.PasswordResetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            PasswordResetService passwordResetService) {

        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    // =========================================
    // ĐĂNG NHẬP
    // =========================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // =========================================
    // ĐĂNG KÝ TÀI KHOẢN CÔNG KHAI
    // =========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok(
                Map.of("message", "Đăng ký tài khoản thành công")
        );
    }

    // =========================================
    // KIỂM TRA API
    // =========================================
    @GetMapping("/test")
    public ResponseEntity<?> test() {

        return ResponseEntity.ok(
                Map.of("message", "API hoạt động bình thường")
        );
    }

    // =========================================
    // QUÊN MẬT KHẨU
    // =========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.createResetToken(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Nếu thông tin tài khoản hợp lệ, hướng dẫn đặt lại mật khẩu sẽ được gửi qua email."
                )
        );
    }

    // =========================================
    // ĐẶT LẠI MẬT KHẨU
    // =========================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                Map.of("message", "Đặt lại mật khẩu thành công")
        );
    }
}