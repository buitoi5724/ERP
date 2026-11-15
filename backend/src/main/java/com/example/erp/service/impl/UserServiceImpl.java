package com.example.erp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.entity.User;
import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.entity.Department;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.DepartmentRepository;
import com.example.erp.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final DepartmentRepository departmentRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository,
            DepartmentRepository departmentRepository) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.departmentRepository = departmentRepository;
    }

    // Tìm tài khoản
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // =========================================
    // 1. TẠO TÀI KHOẢN THÔNG THƯỜNG
    // Không tạo Employee
    // Không gán phòng ban
    // =========================================
    @Override
    @Transactional
    public User save(User user) {

        user.setRoles(
            user.getRoles() != null
                ? new HashSet<>(user.getRoles())
                : new HashSet<>()
        );

        return userRepository.save(user);
    }

    // =========================================
    // 2. TẠO TÀI KHOẢN NHÂN VIÊN NỘI BỘ
    // Chỉ sử dụng khi có chỉ định phòng ban
    // =========================================
    @Override
    @Transactional
    public User saveWithDept(User user, String deptCode) {

        if (deptCode == null || deptCode.isBlank()) {
            throw new IllegalArgumentException(
                "Vui lòng chọn phòng ban cho nhân viên"
            );
        }

        // Phòng ban phải tồn tại, không tự tạo phòng ban
        Department department = departmentRepository
            .findByDeptCode(deptCode.trim().toUpperCase())
            .orElseThrow(() -> new RuntimeException(
                "Phòng ban không tồn tại: " + deptCode
            ));

        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new RuntimeException("Phòng ban đã bị khóa");
        }

        user.setRoles(
            user.getRoles() != null
                ? new HashSet<>(user.getRoles())
                : new HashSet<>()
        );

        User savedUser = userRepository.save(user);

        // Tìm Employee hiện có
        Employee employee = savedUser.getEmployee();

        if (employee == null) {
            Optional<Employee> existingEmployee =
                employeeRepository.findByUser(savedUser);

            employee = existingEmployee.orElse(null);
        }

        // Nếu chưa có Employee thì tạo mới
        if (employee == null) {

            employee = new Employee();

            employee.setEmpCode(
                String.format("EMP%03d", savedUser.getId())
            );

            employee.setEmpName(savedUser.getUsername());
            employee.setActive(true);
            employee.setUser(savedUser);

            employee = employeeRepository.save(employee);

            savedUser.setEmployee(employee);
            userRepository.save(savedUser);
        }

        // Kiểm tra lịch sử phân công
        if (employeeDepartmentRepository.findByEmployee(employee).isEmpty()) {

            EmployeeDepartment empDept = new EmployeeDepartment();

            empDept.setEmployee(employee);
            empDept.setDepartment(department);
            empDept.setFromDate(LocalDate.now());
            empDept.setToDate(LocalDate.of(9999, 12, 31));

            employeeDepartmentRepository.save(empDept);
        }

        return savedUser;
    }
}