package com.example.erp.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.repository.RoleRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.util.RoleType;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Tạo các role nếu chưa có
        createRole(RoleType.ADMIN);
        createRole(RoleType.MANAGER);
        createRole(RoleType.SALES);
        createRole(RoleType.WAREHOUSE);

        // Tạo tài khoản admin nếu chưa có
        if (userRepository.findByUsername("admin").isEmpty()) {

            Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setStatus(true);
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);

            System.out.println("==================================");
            System.out.println("Admin account created");
            System.out.println("Username: admin");
            System.out.println("Password: 123456");
            System.out.println("==================================");
        }
    }

    private void createRole(RoleType roleType) {

        if (roleRepository.findByName(roleType).isEmpty()) {

            Role role = new Role();
            role.setName(roleType);

            roleRepository.save(role);

            System.out.println("Created role: " + roleType);
        }
    }
}