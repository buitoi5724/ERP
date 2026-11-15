package com.example.erp.service;

import com.example.erp.entity.User;

public interface UserService {

    User findByUsername(String username);

    User save(User user);

    // === THÊM MỚI ===
    User saveWithDept(User user, String deptCode);
}