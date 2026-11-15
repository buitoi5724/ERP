
package com.example.erp.controller;

import com.example.erp.dto.UserRequest;
import com.example.erp.dto.EmployeeAccountRequest;

import com.example.erp.entity.Role;
import com.example.erp.entity.User;
import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.entity.Department;
import com.example.erp.entity.DepartmentApprover;

import com.example.erp.repository.RoleRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.DepartmentRepository;
import com.example.erp.repository.DepartmentApproverRepository;

import com.example.erp.util.RoleType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmployeeRepository employeeRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentApproverRepository departmentApproverRepository;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmployeeRepository employeeRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository,
            DepartmentRepository departmentRepository,
            DepartmentApproverRepository departmentApproverRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.departmentRepository = departmentRepository;
        this.departmentApproverRepository = departmentApproverRepository;
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================================
    // CREATE NORMAL USER
    // POST /api/users
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody UserRequest request) {

        if (request.getUsername() == null
                || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Username không được để trống");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Username đã tồn tại");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Password không được để trống");
        }

        if (request.getRole() == null
                || request.getRole().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Role không được để trống");
        }

        RoleType roleType;

        try {
            roleType = RoleType.valueOf(
                    request.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Role không hợp lệ: " + request.getRole());
        }

        Role role = roleRepository.findByName(roleType).orElse(null);

        if (role == null) {
            return ResponseEntity.badRequest()
                    .body("Role không tồn tại: " + roleType);
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null
                ? request.getStatus() : true);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    // =====================================================
    // CREATE EMPLOYEE ACCOUNT
    // POST /api/users/employees
    // =====================================================

    @PostMapping("/employees")
    @Transactional
    public ResponseEntity<?> createEmployeeAccount(
            @RequestBody EmployeeAccountRequest request) {

        // 1. Validate dữ liệu
        if (request.getUsername() == null
                || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()
                || request.getEmpCode() == null
                || request.getEmpCode().isBlank()
                || request.getEmpName() == null
                || request.getEmpName().isBlank()
                || request.getDepartmentId() == null
                || request.getPosition() == null
                || request.getPosition().isBlank()
                || request.getRole() == null
                || request.getRole().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Vui lòng nhập đầy đủ thông tin");
        }

        // 2. Kiểm tra username
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            return ResponseEntity.badRequest()
                    .body("Username đã tồn tại");
        }

        // 3. Kiểm tra mã nhân viên
        if (employeeRepository.findByEmpCode(
                request.getEmpCode().trim()).isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Mã nhân viên đã tồn tại");
        }

        // 4. Kiểm tra phòng ban
        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElse(null);

        if (department == null
                || !Boolean.TRUE.equals(department.getActive())) {

            return ResponseEntity.badRequest()
                    .body("Phòng ban không tồn tại hoặc đã ngừng hoạt động");
        }

        // 5. Kiểm tra chức vụ
        String position = request.getPosition()
                .trim()
                .toUpperCase();

        if (!Set.of(
                "EMPLOYEE",
                "DEPUTY_MANAGER",
                "DEPARTMENT_MANAGER"
        ).contains(position)) {

            return ResponseEntity.badRequest()
                    .body("Chức vụ không hợp lệ");
        }

        // 6. Kiểm tra Role
        RoleType roleType;

        try {
            roleType = RoleType.valueOf(
                    request.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Role không hợp lệ");
        }

        boolean isManager =
                "DEPUTY_MANAGER".equals(position)
                || "DEPARTMENT_MANAGER".equals(position);

        if (isManager && roleType != RoleType.MANAGER) {
            return ResponseEntity.badRequest()
                    .body("Phó phòng và Trưởng phòng phải có Role MANAGER");
        }

        if (!isManager
                && roleType != RoleType.SALES
                && roleType != RoleType.WAREHOUSE
                && roleType != RoleType.USER) {

            return ResponseEntity.badRequest()
                    .body("Nhân viên thường chỉ được chọn SALES, WAREHOUSE hoặc USER");
        }

        Role role = roleRepository.findByName(roleType).orElse(null);

        if (role == null) {
            return ResponseEntity.badRequest()
                    .body("Role chưa tồn tại trong database");
        }

        // 7. Xác định cấp phê duyệt
        Integer approvalOrder = null;

        if ("DEPUTY_MANAGER".equals(position)) {
            approvalOrder = 1;
        } else if ("DEPARTMENT_MANAGER".equals(position)) {
            approvalOrder = 2;
        }

        if (approvalOrder != null) {

            List<DepartmentApprover> approvers =
                    departmentApproverRepository
                            .findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
                                    department.getDeptId());

            final Integer currentApprovalOrder = approvalOrder;

            boolean orderExists = approvers.stream()
                    .anyMatch(a -> currentApprovalOrder.equals(
                            a.getApprovalOrder()));

            if (orderExists) {
                return ResponseEntity.badRequest()
                        .body("Phòng ban đã có người giữ cấp phê duyệt "
                                + approvalOrder);
            }
        }

        // 8. Tạo tài khoản User
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // 9. Tạo hồ sơ Employee
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setEmpCode(request.getEmpCode().trim());
        employee.setEmpName(request.getEmpName().trim());
        employee.setEmail(request.getEmail());
        employee.setActive(true);
        employee.setPosition(position);

        Employee savedEmployee = employeeRepository.save(employee);

        // 10. Gán nhân viên vào phòng ban
        EmployeeDepartment assignment = new EmployeeDepartment();
        assignment.setEmployee(savedEmployee);
        assignment.setDepartment(department);
        assignment.setFromDate(LocalDate.now());
        assignment.setToDate(LocalDate.of(9999, 12, 31));

        employeeDepartmentRepository.save(assignment);

        // 11. Tạo cấp phê duyệt nếu là quản lý
        if (approvalOrder != null) {

            DepartmentApprover approver = new DepartmentApprover();
            approver.setDepartment(department);
            approver.setEmployee(savedEmployee);
            approver.setApprovalOrder(approvalOrder);
            approver.setActive(true);

            departmentApproverRepository.save(approver);
        }

        // 12. Trả kết quả
        return ResponseEntity.ok(Map.of(
                "message", "Tạo tài khoản nhân viên thành công",
                "userId", savedUser.getId(),
                "employeeId", savedEmployee.getEmpId(),
                "username", savedUser.getUsername(),
                "empCode", savedEmployee.getEmpCode(),
                "empName", savedEmployee.getEmpName(),
                "departmentId", department.getDeptId(),
                "position", position,
                "role", roleType.name(),
                "approvalOrder", approvalOrder == null ? 0 : approvalOrder
        ));
    }

    // =====================================================
    // UPDATE USER
    // PUT /api/users/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        return userRepository.findById(id)
                .map(user -> {

                    if (request.getUsername() != null
                            && !request.getUsername().isBlank()) {

                        user.setUsername(request.getUsername().trim());
                    }

                    if (request.getStatus() != null) {
                        user.setStatus(request.getStatus());
                    }

                    if (request.getPassword() != null
                            && !request.getPassword().isBlank()) {

                        user.setPassword(
                                passwordEncoder.encode(request.getPassword()));
                    }

                    if (request.getRole() != null
                            && !request.getRole().isBlank()) {

                        RoleType roleType;

                        try {
                            roleType = RoleType.valueOf(
                                    request.getRole().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException(
                                    "Role không hợp lệ: " + request.getRole());
                        }

                        Role role = roleRepository.findByName(roleType)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Không tìm thấy role: " + roleType));

                        Set<Role> roles = new HashSet<>();
                        roles.add(role);
                        user.setRoles(roles);
                    }

                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(savedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================================
    // DELETE USER - SOFT LOCK
    // DELETE /api/users/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Khóa tài khoản thay vì xóa vật lý để giữ lịch sử
        user.setStatus(false);
        userRepository.save(user);

        Employee employee = employeeRepository.findByUserId(id).orElse(null);

        if (employee != null) {
            employee.setActive(false);
            employeeRepository.save(employee);
        }

        return ResponseEntity.ok(
                Map.of("message", "Đã khóa tài khoản thành công"));
    }
}
