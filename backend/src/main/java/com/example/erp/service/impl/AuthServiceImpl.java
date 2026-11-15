package com.example.erp.service.impl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.LoginRequest;
import com.example.erp.dto.LoginResponse;
import com.example.erp.dto.RegisterRequest;
import com.example.erp.dto.UserInfoResponse;

import com.example.erp.entity.Department;
import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.entity.Role;
import com.example.erp.entity.User;

import com.example.erp.exception.UsernameAlreadyExistsException;

import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
    }

    // =========================================
    // ĐĂNG KÝ TÀI KHOẢN CÔNG KHAI
    // Chỉ tạo User, không tạo Employee
    // Không gán phòng ban
    // Không nhận quyền từ frontend
    // =========================================
    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (request == null) {
            throw new RuntimeException("Thông tin đăng ký không hợp lệ");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("Username không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password không được để trống");
        }

        String username = request.getUsername().trim();

        // 1. Kiểm tra username đã tồn tại
        if (userService.findByUsername(username) != null) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // 2. Chỉ lấy quyền USER từ database
        Role role = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy quyền USER trong database. Vui lòng cấu hình bảng roles."
                ));

        // 3. Tạo tài khoản
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);

        // Không lấy role từ request
        // Không lấy deptCode từ request
        user.setRoles(Set.of(role));

        // 4. Chỉ lưu User, không tạo Employee hoặc EmployeeDepartment
        userService.save(user);
    }

    // =========================================
    // ĐĂNG NHẬP
    // Giữ nguyên logic hiện tại
    // =========================================
    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userService.findByUsername(request.getUsername());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.getStatus()) {
            throw new RuntimeException("Account is locked");
        }

        String token = jwtService.generateToken(authentication);

        Set<RoleType> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Long empId = null;
        String empCode = null;
        String empName = null;

        Long deptId = null;
        String deptCode = null;
        String deptName = null;

        boolean approver = false;
        Integer approvalOrder = null;

        Optional<Employee> employeeOptional =
                employeeRepository.findByUserId(user.getId());

        if (employeeOptional.isPresent()) {

            Employee employee = employeeOptional.get();

            empId = employee.getEmpId();
            empCode = employee.getEmpCode();
            empName = employee.getEmpName();

            Optional<EmployeeDepartment> edOpt =
                    employeeDepartmentRepository
                            .findByEmployeeEmpIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                    employee.getEmpId(),
                                    LocalDate.now(),
                                    LocalDate.now()
                            );

            if (edOpt.isPresent() && edOpt.get().getDepartment() != null) {

                Department department = edOpt.get().getDepartment();

                deptId = department.getDeptId();
                deptCode = department.getDeptCode();
                deptName = department.getDeptName();

                // Kiểm tra người phê duyệt thuộc phòng ban
                // (giữ nguyên phần xử lý hiện tại)
            }
        }

        return new LoginResponse(
                token,
                new UserInfoResponse(
                        user.getId(),
                        user.getUsername(),
                        roles,
                        empId,
                        empCode,
                        empName,
                        deptId,
                        deptCode,
                        deptName,
                        approver,
                        approvalOrder
                )
        );
    }
}