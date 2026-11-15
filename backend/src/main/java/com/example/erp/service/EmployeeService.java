package com.example.erp.service;

import com.example.erp.entity.Employee;

import java.util.List;

public interface EmployeeService {

    // Lấy tất cả nhân viên
    List<Employee> getAll();

    // Lấy nhân viên theo ID
    Employee getById(Long empId);

    // Lấy nhân viên theo user ID
    Employee getByUserId(Long userId);

    // Lấy nhân viên theo mã nhân viên
    Employee getByEmpCode(String empCode);

    // Tạo nhân viên
    Employee create(Employee employee);

    // Cập nhật nhân viên
    Employee update(Long empId, Employee employee);

    // Xóa nhân viên
    void delete(Long empId);
}