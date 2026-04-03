package com.example.erp.service.impl;

import org.springframework.stereotype.Service;

import com.example.erp.entity.User;
import com.example.erp.repository.UserRepository;
import com.example.erp.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    
}