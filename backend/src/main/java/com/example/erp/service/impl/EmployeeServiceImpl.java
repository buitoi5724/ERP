package com.example.erp.service.impl;

import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.entity.Department;
import com.example.erp.entity.User;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.DepartmentRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final DepartmentRepository departmentRepository;

    // === Thêm 2 Repository mới vào constructor ===
    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository,
            DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Long empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với ID: " + empId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với User ID: " + userId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getByEmpCode(String empCode) {
        return employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với mã: " + empCode
                        )
                );
    }

    @Override
    public Employee create(Employee employee) {
        // ==============================
        // 1. Kiểm tra User
        // ==============================
        if (employee.getUser() == null ||
                employee.getUser().getId() == null) {
            throw new RuntimeException(
                    "User không được để trống"
            );
        }
        Long userId = employee.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy User với ID: " + userId
                        )
                );
        if (employeeRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException(
                    "User ID " + userId +
                    " đã được liên kết với một Employee khác"
            );
        }
        employee.setUser(user);

        // ==============================
        // 2. Kiểm tra mã nhân viên
        // ==============================
        if (employee.getEmpCode() == null ||
                employee.getEmpCode().trim().isEmpty()) {
            throw new RuntimeException(
                    "Mã nhân viên không được để trống"
            );
        }
        employee.setEmpCode(employee.getEmpCode().trim());

        // ==============================
        // 3. Kiểm tra mã trùng
        // ==============================
        if (employeeRepository.findByEmpCode(employee.getEmpCode()).isPresent()) {
            throw new RuntimeException(
                    "Mã nhân viên đã tồn tại: " + employee.getEmpCode()
            );
        }

        // ==============================
        // 4. Kiểm tra tên
        // ==============================
        if (employee.getEmpName() == null ||
                employee.getEmpName().trim().isEmpty()) {
            throw new RuntimeException(
                    "Tên nhân viên không được để trống"
            );
        }
        employee.setEmpName(employee.getEmpName().trim());

        // ==============================
        // 5. Active mặc định
        // ==============================
        if (employee.getActive() == null) {
            employee.setActive(true);
        }

        // ==============================
        // 6. Lưu Employee
        // ==============================
        Employee savedEmp = employeeRepository.save(employee);

        // ==================================================
        // ✅ TỰ ĐỘNG GÁN PHÒNG BAN NGAY SAU KHI TẠO
        // ==================================================
        Department department;
        String role = user.getRoles() != null 
                ? user.getRoles().toString().toUpperCase() 
                : "";

        if (role.contains("SALE") || role.contains("KINH_DOANH")) {
            department = departmentRepository.findByDeptCode("KD")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Kinh doanh (KD)"));
        } else if (role.contains("ACCOUNTANT") || role.contains("KE_TOAN")) {
            department = departmentRepository.findByDeptCode("KT")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Kế toán (KT)"));
        } else if (role.contains("ADMIN")) {
            department = departmentRepository.findByDeptCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Admin (ADMIN)"));
        } else {
            // Mặc định gán phòng Kinh doanh
            department = departmentRepository.findByDeptCode("KD")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban mặc định (KD)"));
        }

        // Tạo bản ghi liên kết phòng ban
        EmployeeDepartment empDept = new EmployeeDepartment();
        empDept.setEmployee(savedEmp);
        empDept.setDepartment(department);
        empDept.setFromDate(LocalDate.now());
        empDept.setToDate(LocalDate.of(2099, 12, 31));

        employeeDepartmentRepository.save(empDept);
        // ==================================================

        return savedEmp;
    }

    @Override
    public Employee update(
            Long empId,
            Employee employee) {
        Employee existingEmployee = getById(empId);
        
        // Cập nhật User nếu có
        if (employee.getUser() != null &&
                employee.getUser().getId() != null) {
            Long newUserId = employee.getUser().getId();
            if (existingEmployee.getUser() == null ||
                    !existingEmployee.getUser().getId().equals(newUserId)) {
                User user = userRepository.findById(newUserId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy User với ID: " + newUserId
                                )
                        );
                employeeRepository.findByUserId(newUserId)
                        .ifPresent(otherEmployee -> {
                            if (!otherEmployee.getEmpId().equals(empId)) {
                                throw new RuntimeException(
                                        "User ID " + newUserId +
                                        " đã được liên kết với một Employee khác"
                                );
                            }
                        });
                existingEmployee.setUser(user);
            }
        }

        // Cập nhật tên
        if (employee.getEmpName() != null &&
                !employee.getEmpName().trim().isEmpty()) {
            existingEmployee.setEmpName(employee.getEmpName().trim());
        }

        // Cập nhật email
        existingEmployee.setEmail(employee.getEmail());

        // Cập nhật trạng thái
        if (employee.getActive() != null) {
            existingEmployee.setActive(employee.getActive());
        }

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void delete(Long empId) {
        Employee employee = getById(empId);
        employeeRepository.delete(employee);
    }
}