package com.example.erp.service.impl;

import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.repository.RoleRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.service.AccountService;
import com.example.erp.util.RoleType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CREATE
    // =========================

    @Override
    @Transactional
    public User create(User user, String roleName) {

        // Kiểm tra username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException(
                    "Username đã tồn tại: " + user.getUsername()
            );
        }

        // Mã hóa password
        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password không được để trống"
            );
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // Tìm role
        RoleType roleType;

        try {
            roleType = RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Quyền không hợp lệ: " + roleName
            );
        }

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy quyền: " + roleName
                        )
                );

        // Gán role
        user.getRoles().clear();
        user.getRoles().add(role);

        // Status mặc định
        if (user.getStatus() == null) {
            user.setStatus(true);
        }

        return userRepository.save(user);
    }

    // =========================
    // GET ALL
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {

        return userRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {

        return userRepository.findById(id);
    }

    // =========================
    // CHECK USERNAME
    // =========================

    @Override
    @Transactional(readOnly = true)
    public User checkUsername(String username) {

        return userRepository
                .findByUsername(username)
                .orElse(null);
    }

    // =========================
    // UPDATE
    // =========================

    @Override
    @Transactional
    public User update(
            Long id,
            User newUser,
            String roleName
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User không tồn tại với id: " + id
                        )
                );

        // Username
        if (newUser.getUsername() != null &&
                !newUser.getUsername().isBlank()) {

            user.setUsername(newUser.getUsername());
        }

        // Status
        if (newUser.getStatus() != null) {

            user.setStatus(newUser.getStatus());
        }

        // =========================
        // PASSWORD
        // =========================

        // Chỉ đổi password nếu frontend nhập password mới
        if (newUser.getPassword() != null &&
                !newUser.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            newUser.getPassword()
                    )
            );
        }

        // =========================
        // ROLE
        // =========================

        if (roleName != null && !roleName.isBlank()) {

            RoleType roleType;

            try {
                roleType = RoleType.valueOf(
                        roleName.trim().toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Quyền không hợp lệ: " + roleName
                );
            }

            Role role = roleRepository
                    .findByName(roleType)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy quyền: " + roleName
                            )
                    );

            // Xóa quyền cũ
            user.getRoles().clear();

            // Thêm quyền mới
            user.getRoles().add(role);
        }

        return userRepository.save(user);
    }

    // =========================
    // DELETE
    // =========================

    @Override
    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User không tồn tại với id: " + id
                        )
                );

        user.getRoles().clear();

        userRepository.delete(user);
    }
}