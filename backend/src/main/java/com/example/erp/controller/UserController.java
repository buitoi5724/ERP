package com.example.erp.controller;

import com.example.erp.dto.UserRequest;
import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.repository.RoleRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.util.RoleType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;

        this.roleRepository = roleRepository;

        this.passwordEncoder = passwordEncoder;
    }


    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userRepository.findAll();

        System.out.println(
                "===== GET ALL USERS ====="
        );

        for (User user : users) {

            System.out.println(
                    "USER: "
                    + user.getUsername()
                    + " | ROLES: "
                    + user.getRoles()
            );
        }

        return ResponseEntity.ok(users);
    }


    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id) {

        return userRepository.findById(id)

                .map(ResponseEntity::ok)

                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =====================================================
    // CREATE USER
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody UserRequest request) {

        System.out.println(
                "===== CREATE USER ====="
        );

        System.out.println(
                "USERNAME: "
                + request.getUsername()
        );

        System.out.println(
                "ROLE: "
                + request.getRole()
        );


        // =================================================
        // CHECK USERNAME
        // =================================================

        if (userRepository.existsByUsername(
                request.getUsername())) {

            return ResponseEntity
                    .badRequest()
                    .body("Username đã tồn tại");
        }


        // =================================================
        // CHECK PASSWORD
        // =================================================

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Password không được để trống");
        }


        // =================================================
        // CREATE USER
        // =================================================

        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : true
        );


        // =================================================
        // ROLE
        // =================================================

        if (request.getRole() == null
                || request.getRole().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Role không được để trống");
        }


        RoleType roleType;

        try {

            roleType = RoleType.valueOf(
                    request.getRole().toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Role không hợp lệ: "
                            + request.getRole()
                    );
        }


        Role role = roleRepository
                .findByName(roleType)
                .orElse(null);


        if (role == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Role không tồn tại: "
                            + request.getRole()
                    );
        }


        // =================================================
        // SET ROLE
        // =================================================

        Set<Role> roles = new HashSet<>();

        roles.add(role);

        user.setRoles(roles);


        // =================================================
        // SAVE
        // =================================================

        User savedUser =
                userRepository.save(user);


        System.out.println(
                "===== CREATE SUCCESS ====="
        );

        System.out.println(
                "USER ID: "
                + savedUser.getId()
        );

        System.out.println(
                "ROLES: "
                + savedUser.getRoles()
        );


        return ResponseEntity.ok(savedUser);
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        System.out.println();
        System.out.println(
                "================================="
        );
        System.out.println(
                "===== UPDATE USER ====="
        );
        System.out.println(
                "================================="
        );

        System.out.println(
                "USER ID: "
                + id
        );

        System.out.println(
                "USERNAME: "
                + request.getUsername()
        );

        System.out.println(
                "ROLE REQUEST: "
                + request.getRole()
        );

        System.out.println(
                "STATUS: "
                + request.getStatus()
        );


        // =================================================
        // FIND USER
        // =================================================

        return userRepository.findById(id)

                .map(user -> {


                    // =====================================
                    // USERNAME
                    // =====================================

                    if (request.getUsername() != null
                            && !request.getUsername().isBlank()) {

                        user.setUsername(
                                request.getUsername()
                        );
                    }


                    // =====================================
                    // STATUS
                    // =====================================

                    if (request.getStatus() != null) {

                        user.setStatus(
                                request.getStatus()
                        );
                    }


                    // =====================================
                    // PASSWORD
                    // =====================================

                    if (request.getPassword() != null
                            && !request.getPassword().isBlank()) {

                        System.out.println(
                                "PASSWORD: CHANGE"
                        );

                        user.setPassword(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        );

                    } else {

                        System.out.println(
                                "PASSWORD: KEEP OLD"
                        );
                    }


                    // =====================================
                    // ROLE
                    // =====================================

                    if (request.getRole() != null
                            && !request.getRole().isBlank()) {


                        System.out.println(
                                "ROLE REQUEST: "
                                + request.getRole()
                        );


                        // ---------------------------------
                        // Convert String -> RoleType
                        // ---------------------------------

                        RoleType roleType;

                        try {

                            roleType = RoleType.valueOf(
                                    request.getRole()
                                            .toUpperCase()
                            );

                        } catch (IllegalArgumentException e) {

                            throw new RuntimeException(
                                    "Role không hợp lệ: "
                                    + request.getRole()
                            );
                        }


                        System.out.println(
                                "ROLE TYPE: "
                                + roleType
                        );


                        // ---------------------------------
                        // Find role in database
                        // ---------------------------------

                        Role role = roleRepository
                                .findByName(roleType)
                                .orElse(null);


                        if (role == null) {

                            throw new RuntimeException(
                                    "Không tìm thấy role: "
                                    + roleType
                            );
                        }


                        System.out.println(
                                "ROLE ID: "
                                + role.getId()
                        );

                        System.out.println(
                                "ROLE NAME: "
                                + role.getName()
                        );


                        // ---------------------------------
                        // Create new role set
                        // ---------------------------------

                        Set<Role> roles =
                                new HashSet<>();

                        roles.add(role);


                        // ---------------------------------
                        // Set roles to user
                        // ---------------------------------

                        user.setRoles(roles);


                        System.out.println(
                                "USER ROLES AFTER SET: "
                                + user.getRoles()
                        );
                    }


                    // =====================================
                    // SAVE USER
                    // =====================================

                    User savedUser =
                            userRepository.save(user);


                    // =====================================
                    // LOG RESULT
                    // =====================================

                    System.out.println(
                            "===== UPDATE SUCCESS ====="
                    );

                    System.out.println(
                            "USER ID: "
                            + savedUser.getId()
                    );

                    System.out.println(
                            "USERNAME: "
                            + savedUser.getUsername()
                    );

                    System.out.println(
                            "ROLES: "
                            + savedUser.getRoles()
                    );

                    System.out.println(
                            "================================="
                    );


                    return ResponseEntity.ok(
                            savedUser
                    );

                })

                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =====================================================
    // DELETE USER
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        System.out.println(
                "===== DELETE USER ====="
        );

        System.out.println(
                "USER ID: "
                + id
        );


        // =================================================
        // CHECK USER
        // =================================================

        if (!userRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        // =================================================
        // DELETE
        // =================================================

        userRepository.deleteById(id);


        System.out.println(
                "DELETE SUCCESS"
        );


        return ResponseEntity
                .noContent()
                .build();
    }
}