package com.example.erp.repository;

import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeDepartmentRepository
        extends JpaRepository<EmployeeDepartment, Long> {

    // Lấy toàn bộ lịch sử phòng ban của nhân viên
    List<EmployeeDepartment> findByEmployeeEmpId(Long empId);

    // Lấy phòng ban hiện tại của nhân viên
    Optional<EmployeeDepartment>
    findByEmployeeEmpIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Long empId,
            LocalDate fromDate,
            LocalDate toDate
    );

    // Lấy các bản ghi theo phòng ban
    List<EmployeeDepartment> findByDepartmentDeptId(Long deptId);
    
    List<EmployeeDepartment> findByEmployee(Employee employee);
}