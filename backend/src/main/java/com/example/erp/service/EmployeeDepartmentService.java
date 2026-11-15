package com.example.erp.service;

import com.example.erp.entity.EmployeeDepartment;

import java.util.List;

public interface EmployeeDepartmentService {

    // Lấy tất cả phân công phòng ban
    List<EmployeeDepartment> getAll();

    // Lấy theo ID
    EmployeeDepartment getById(Long id);

    // Lấy phòng ban của một nhân viên
    List<EmployeeDepartment> getByEmployee(Long empId);

    // Lấy nhân viên của một phòng ban
    List<EmployeeDepartment> getByDepartment(Long deptId);

    // Tạo phân công
    EmployeeDepartment create(EmployeeDepartment employeeDepartment);

    // Cập nhật phân công
    EmployeeDepartment update(
            Long id,
            EmployeeDepartment employeeDepartment
    );

    // Xóa phân công
    void delete(Long id);
}