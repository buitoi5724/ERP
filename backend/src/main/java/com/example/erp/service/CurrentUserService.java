package com.example.erp.service;

import com.example.erp.entity.Employee;
import com.example.erp.entity.User;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public CurrentUserService(
            UserRepository userRepository,
            EmployeeRepository employeeRepository) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    // Lấy username của tài khoản đang đăng nhập
    public String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Người dùng chưa đăng nhập"
            );
        }

        return authentication.getName();
    }

    // Lấy User hiện tại
    public User getCurrentUser() {

        String username = getCurrentUsername();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy User: "
                                        + username
                        )
                );
    }

    // Lấy Employee hiện tại
    public Employee getCurrentEmployee() {

        User user = getCurrentUser();

        return employeeRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tài khoản chưa được liên kết với Employee"
                        )
                );
    }
}