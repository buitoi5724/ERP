package com.example.erp.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.UserInfoResponse;
import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.security.JwtService;
import com.example.erp.service.AuthService;
import com.example.erp.service.UserService;
import com.example.erp.util.RoleType;


@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1️⃣ Xác thực username + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2️⃣ Lấy user từ DB
        User user = userService.findByUsername(request.getUsername());

        // 3️⃣ Kiểm tra trạng thái tài khoản
        if (!user.getStatus()) {
            throw new RuntimeException("Account is locked");
        }

        // 4️⃣ Sinh JWT
        String token = jwtService.generateToken(authentication);

        // 5️⃣ Lấy role ENUM
        Set<RoleType> roles = user.getRoles()
                .stream()
                .map(Role::getName) // RoleType
                .collect(Collectors.toSet());

        // 6️⃣ Trả response
        return new LoginResponse(
                token,
                new UserInfoResponse(
                        user.getId(),
                        user.getUsername(),
                        roles
                )
        );
    }
}
