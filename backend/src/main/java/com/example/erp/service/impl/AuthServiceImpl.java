package com.example.erp.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.RegisterRequest;
import com.example.erp.dto.UserInfoResponse;
import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.exception.UsernameAlreadyExistsException;
import com.example.erp.repository.RoleRepository;
import com.example.erp.security.JwtService;
import com.example.erp.service.AuthService;
import com.example.erp.service.UserService;
import com.example.erp.util.RoleType;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void register(RegisterRequest request) {

        // Kiểm tra username đã tồn tại
    	if (userService.findByUsername(request.getUsername()) != null) {
    	    throw new UsernameAlreadyExistsException(
    	        "Username already exists"
    	    );
    	}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);

        // Gán quyền mặc định
        Role role = roleRepository.findByName(RoleType.SALES)
                .orElseThrow(() -> new RuntimeException("Role SALES not found"));

        user.setRoles(Set.of(role));

        userService.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // Xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // Lấy thông tin user
        User user = userService.findByUsername(request.getUsername());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.getStatus()) {
            throw new RuntimeException("Account is locked");
        }

        // Sinh JWT
        String token = jwtService.generateToken(authentication);

        // Danh sách quyền
        Set<RoleType> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Trả kết quả
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