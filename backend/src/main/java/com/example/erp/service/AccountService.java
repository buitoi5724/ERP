package com.example.erp.service;

import com.example.erp.entity.User;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    User create(User user, String roleName);

    List<User> getAll();

    Optional<User> getById(Long id);

    User checkUsername(String username);

    User update(
            Long id,
            User user,
            String roleName
    );

    void delete(Long id);
}